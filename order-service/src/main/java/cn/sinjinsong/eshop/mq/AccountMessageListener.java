package cn.sinjinsong.eshop.mq;

import cn.sinjinsong.eshop.common.domain.entity.message.ConsumerTransactionMessageDO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import cn.sinjinsong.eshop.common.util.ProtoStuffUtil;
import cn.sinjinsong.eshop.config.MQConsumerConfig;
import cn.sinjinsong.eshop.config.MQProducerConfig;
import cn.sinjinsong.eshop.service.message.ConsumerTransactionMessageService;
import cn.sinjinsong.eshop.service.order.OrderService;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author sinjinsong
 * @date 2017/12/25
 */
@Component
@Slf4j
public class AccountMessageListener implements MessageListenerConcurrently {
    @Autowired
    private OrderService orderService;
    @Autowired
    @Qualifier("consumerTransactionMessageService")
    private ConsumerTransactionMessageService messageService;
    @Autowired
    private MQConsumerConfig consumerConfig;
    @Autowired
    private MQProducerConfig producerConfig;
    @Autowired
    private MQProducer producer;
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        log.info("接收到消息数量为:{}", msgs.size());
        for (MessageExt msg : msgs) {
            ConsumerTransactionMessageDO messageDO = null;
            OrderDO order = null;
            try {
                String topic = msg.getTopic();
                String keys = msg.getKeys();

                // 如果是回查消息
                if (keys.equals(consumerConfig.getCheckKeys())) {
                    List<Long> ids = ProtoStuffUtil.deserialize(msg.getBody(),List.class);
                    log.info("消费者接收到回查消息:topic: {}, keys:{} ,body: {}", topic, keys,ids);
                    Map<Long, MessageStatus> result = messageService.findConsumerMessageStatuses(ids);
                    Message checkReply = new Message();
                    checkReply.setTopic(producerConfig.getTopic());
                    checkReply.setBody(ProtoStuffUtil.serialize(result));
                    producer.send(checkReply);
                    continue;
                }


                order = ProtoStuffUtil.deserialize(msg.getBody(), OrderDO.class);
                log.info("消费者接收到消息:topic: {}, keys:{} , order: {}", topic, keys, order);


                // 如果已经被消费过并且消费成功，那么不再重复消费（未被消费->id不存在或消费失败或超过重试次数的都会继续消费）
                if (messageService.isMessageConsumedSuccessfully(order.getId())) {
                    continue;
                }
                messageDO = ConsumerTransactionMessageDO.builder()
                        .id(order.getId())
                        .createTime(LocalDateTime.now())
                        .topic(msg.getTopic())
                        .build();
                // 业务逻辑处理
                orderService.finishOrder(order);
                // 如果业务逻辑抛出异常，那么会跳过插入CONSUMED
                messageDO.setMessageStatus(MessageStatus.CONSUMED);
                // 如果是未被消费，第一次就消费成功了，则插入
                // 如果是超过重试次数，又人工设置重试，则更新状态为已被消费
                messageService.insertOrUpdate(messageDO);
            } catch (Exception e) {
                e.printStackTrace();
                // 重试次数达到最大重试次数 
                if (msg.getReconsumeTimes() == consumerConfig.getRetryTimes()) {
                    log.info("客户端重试三次,需要人工处理");
                    messageService.update(
                            ConsumerTransactionMessageDO.builder()
                                    .id(order.getId())
                                    .messageStatus(MessageStatus.OVER_CONSUME_RETRY_TIME).build()
                    );
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } else {
                    log.info("消费失败，进行重试，当前重试次数为: {}", msg.getReconsumeTimes());
                    messageDO.setMessageStatus(MessageStatus.CONSUME_FAILED);
                    // 如果第一次消费失败，那么插入
                    // 如果之前消费失败，继续重试，那么doNothing
                    // 如果之前是超过重试次数，人工设置重试，那么将状态改为消费失败
                    messageService.insertOrUpdate(messageDO);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

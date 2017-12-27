package cn.sinjinsong.eshop.mq;

import cn.sinjinsong.eshop.common.domain.entity.message.ConsumerTransactionMessageDO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import cn.sinjinsong.eshop.common.util.ProtoStuffUtil;
import cn.sinjinsong.eshop.config.MQConsumerConfig;
import cn.sinjinsong.eshop.service.message.ConsumerTransactionMessageService;
import cn.sinjinsong.eshop.service.order.OrderService;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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
    private MQConsumerConfig config;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        log.info("接收到消息数量为:{}", msgs.size());
        for (MessageExt msg : msgs) {
            ConsumerTransactionMessageDO messageDO = null;
            OrderDO order = null;
            try {
                String topic = msg.getTopic();
                String keys = msg.getKeys();
                order = ProtoStuffUtil.deserialize(msg.getBody(), OrderDO.class);
                log.info("消费者接收到消息:topic: {}, keys:{} , order: {}", topic, keys, order);
                // 如果已经被消费过并且消费成功，那么不再重复消费
                if(messageService.isMessageConsumedSuccessfully(order.getId())){
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
                messageService.insert(messageDO);
            } catch (Exception e) {
                e.printStackTrace();
                // 重试次数达到最大重试次数 
                if (msg.getReconsumeTimes() == config.getRetryTimes()) {
                    log.info("客户端重试三次,需要人工处理");
                    messageService.update(
                            ConsumerTransactionMessageDO.builder()
                                    .id(order.getId())
                                    .messageStatus(MessageStatus.OVER_CONSUME_RETRY_TIME).build()
                    );
                    //记录日志
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } else {
                    log.info("消费失败，进行重试，当前重试次数为: {}", msg.getReconsumeTimes());
                    messageDO.setMessageStatus(MessageStatus.CONSUME_FAILED);
                    messageService.insertIfNotExists(messageDO);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

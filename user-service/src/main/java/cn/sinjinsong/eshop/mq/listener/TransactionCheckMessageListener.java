package cn.sinjinsong.eshop.mq.listener;

import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import cn.sinjinsong.eshop.common.util.ProtoStuffUtil;
import cn.sinjinsong.eshop.service.message.ProducerTransactionMessageService;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author sinjinsong
 * @date 2018/3/27
 */
@Component
@Slf4j
public class TransactionCheckMessageListener implements MessageListenerConcurrently {
    @Autowired
    private ProducerTransactionMessageService messageService;
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        log.info("接收到消息数量为:{}", msgs.size());
        for (MessageExt msg : msgs) {
            Map<Long,MessageStatus> checkResult = null;
            try {
                String topic = msg.getTopic();
                String keys = msg.getKeys();
                checkResult = ProtoStuffUtil.deserialize(msg.getBody(), Map.class);
                log.info("消费者接收到消息:topic: {}, keys:{} , order: {}", topic, keys, checkResult);
                messageService.updateStatusAndReSend(checkResult);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

package cn.sinjinsong.eshop.mq;

import cn.sinjinsong.eshop.service.order.OrderService;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        MessageExt msg = msgs.get(0);
        try {
            //Message Body
            byte[] body = msg.getBody();
//            orderService.finishOrder();
        } catch (Exception e) {
            e.printStackTrace();
            //重试次数为3情况 
            if (msg.getReconsumeTimes() == 3) {
                log.info("客户端重试三次");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                //记录日志
            }
            log.info("消费失败");
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

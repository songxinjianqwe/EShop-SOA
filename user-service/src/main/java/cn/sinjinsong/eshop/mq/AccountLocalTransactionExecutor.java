package cn.sinjinsong.eshop.mq;

import cn.sinjinsong.eshop.common.domain.entity.message.ProducerTransactionMessageDO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.exception.order.OrderStateIllegalException;
import cn.sinjinsong.eshop.common.util.ProtoStuffUtil;
import cn.sinjinsong.eshop.service.message.ProducerTransactionMessageService;
import cn.sinjinsong.eshop.service.pay.PayService;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
@Component
@Slf4j
public class AccountLocalTransactionExecutor implements LocalTransactionExecuter {
    @Autowired
    private PayService payService;
    @Autowired
    private ProducerTransactionMessageService messageService;

    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
        try {
            String paymentPassword = (String) arg;
            OrderDO order = ProtoStuffUtil.deserialize(msg.getBody(), OrderDO.class);
            if (order.getOrderStatus() != OrderStatus.UNPAID) {
                log.info("{} 订单状态不为unpaid", order.getId());
                throw new OrderStateIllegalException(order.getOrderStatus().toString());
            }
            // 本地事务，减少用户账户余额
            // 抛出异常时会进行回滚，下面构造消息存储到数据库也不会被执行
            payService.decreaseAccount(order.getUser().getId(), order.getTotalPrice(), paymentPassword);
            // 保存消息至数据库
            ProducerTransactionMessageDO messageDO = ProducerTransactionMessageDO.builder()
                    .id(order.getId())
                    .body(msg.getBody())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .messageStatus(MessageStatus.UNCONSUMED)
                    .topic(msg.getTopic())
                    .sendTimes(0)
                    .build();
            messageService.save(messageDO);
            // 成功通知MQ消息变更 该消息变为：<确认发送>
            return LocalTransactionState.COMMIT_MESSAGE;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("本地事务执行失败，直接回滚!");
            // 失败则不通知MQ 该消息一直处于：<暂缓发送>
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }
}

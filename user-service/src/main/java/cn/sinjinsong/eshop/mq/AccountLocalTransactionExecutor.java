package cn.sinjinsong.eshop.mq;

import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.common.exception.order.OrderStateIllegalException;
import cn.sinjinsong.eshop.service.pay.PayService;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
@Component
public class AccountLocalTransactionExecutor implements LocalTransactionExecuter {
    @Autowired
    private PayService payService;

    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
        if (order.getOrderStatus() != OrderStatus.UNPAID) {
            log.info("{} 订单状态不为unpaid", order.getId());
            throw new OrderStateIllegalException(order.getOrderStatus().toString());
        }
        // 本地事务，减少用户账户余额
        payService.decreaseAccount(order.getUser().getId(), order.getTotalPrice(), paymentPassword);

        return null;
    }
}

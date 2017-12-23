package cn.sinjinsong.eshop.service.pay.cancel;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.domain.entity.pay.BalanceDO;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.common.exception.order.OrderStateIllegalException;
import cn.sinjinsong.eshop.common.properties.DbResult;
import cn.sinjinsong.eshop.dao.pay.BalanceDOMapper;
import cn.sinjinsong.eshop.service.order.OrderService;
import cn.sinjinsong.eshop.service.pay.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sinjinsong
 * @date 2017/12/23
 */
@Service("accountServiceCancel")
@Slf4j
public class AccountServiceCancel implements AccountService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private BalanceDOMapper balanceDOMapper;

    /**
     * cancel:
     * 1. 读取账户表的version
     * 2. 读取订单状态
     * 3. 如果订单状态是paying，则将订单状态设置为pay_failure，执行账户余额的回增，version++(where中检查version)
     * 如果没有执行更新，则表示有请求已经回增过余额了，则不再次回增。
     *
     * @param order
     * @param paymentPassword
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void pay(OrderDO order, String paymentPassword) {
        log.info("支付:cancel阶段");
        BalanceDO balanceDO = balanceDOMapper.selectByPrimaryKey(order.getUser().getId());
        if (order.getOrderStatus() != OrderStatus.PAYING) {
            throw new OrderStateIllegalException(order.getOrderStatus().toString());
        }
        order.setOrderStatus(OrderStatus.PAY_FAILED);
        orderService.updateOrder(order);
        balanceDO.setBalance(balanceDO.getBalance() + order.getTotalPrice());
        int result = balanceDOMapper.updateBalanceMVCC(balanceDO);
        if (result == DbResult.FAILURE) {
            log.info("{} 订单cancel出现并发，MVCC更新失败", order.getId());
        }
    }
}

package cn.sinjinsong.eshop.service.pay.impl;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.domain.entity.pay.BalanceDO;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.common.exception.order.OrderStateIllegalException;
import cn.sinjinsong.eshop.common.exception.pay.BalanceNotEnoughException;
import cn.sinjinsong.eshop.common.exception.pay.PaymentPasswordInCorrectException;
import cn.sinjinsong.eshop.common.properties.DbResult;
import cn.sinjinsong.eshop.dao.pay.BalanceDOMapper;
import cn.sinjinsong.eshop.service.order.OrderService;
import cn.sinjinsong.eshop.service.pay.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.bytesoft.compensable.Compensable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TCC的try阶段的实现
 *
 * @author sinjinsong
 * @date 2017/12/23
 */
@Service("accountService")
@Compensable(interfaceClass = AccountService.class, confirmableKey = "accountServiceConfirm", cancellableKey = "accountServiceCancel")
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private BalanceDOMapper balanceDOMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 这里会涉及分布式事务
     * 本地事务是更新用户的余额表
     * 远程事务是更新订单的状态和更新企业的余额表
     * 1. 读取账户表的version
     * 2. 读取订单状态
     * 3. 如果订单状态为unpaid，则将订单状态设置为paying，执行账户余额扣减(可能会抛出异常此时会执行cancel)，version++(where中检查version不变)；
     * 如果没有执行更新，则表示有请求已经扣减过账户余额了，则不再次扣减。
     *
     * @param order
     * @param paymentPassword
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void pay(OrderDO order, String paymentPassword) {
        log.info("支付:try阶段");
        // 读取version
        BalanceDO balanceDO = balanceDOMapper.selectByPrimaryKey(order.getUser().getId());
        // 检查订单状态，订单不符则会抛出异常，不会更新任何数据
        if (order.getOrderStatus() != OrderStatus.UNPAID) {
            log.info("{} 订单状态不为unpaid", order.getId());
            throw new OrderStateIllegalException(order.getOrderStatus().toString());
        }
        // 更新订单状态
        order.setOrderStatus(OrderStatus.PAYING);
        orderService.updateOrder(order);

        // 检查账户表的状态，如果在申请资源的过程中出现问题，则抛出异常，此时会调用cancel，将订单状态设置为支付失败
        if (balanceDO == null) {
            throw new BalanceNotEnoughException("0");
        }
        if (order.getTotalPrice().compareTo(balanceDO.getBalance()) > 0) {
            log.info("{} 订单余额不足", order.getId());
            throw new BalanceNotEnoughException(String.valueOf(balanceDO.getBalance()));
        }
        if (!passwordEncoder.matches(paymentPassword, balanceDO.getPaymentPassword())) {
            log.info("{} 订单支付密码错误", order.getId());
            throw new PaymentPasswordInCorrectException(order.getUser().getId());
        }
        balanceDO.setBalance(balanceDO.getBalance() - order.getTotalPrice());
        // 执行带有并发控制的更新账户余额的操作，只有没有别人在此期间更新过账户余额，才会成功
        int result = balanceDOMapper.updateBalanceMVCC(balanceDO);
        if (result == DbResult.FAILURE) {
            // 如果没有更新，说明该订单已经进入支付状态了，不会重复扣减
            log.info("账户表的MVCC失败，该订单的支付请求产生并发");
        }
    }

}

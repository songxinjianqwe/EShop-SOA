package cn.sinjinsong.eshop.service.pay.impl;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.domain.entity.pay.BalanceDO;
import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.common.exception.order.OrderStateIllegalException;
import cn.sinjinsong.eshop.common.exception.pay.BalanceNotEnoughException;
import cn.sinjinsong.eshop.common.exception.pay.PaymentPasswordInCorrectException;
import cn.sinjinsong.eshop.dao.pay.BalanceDOMapper;
import cn.sinjinsong.eshop.service.order.OrderService;
import cn.sinjinsong.eshop.service.pay.PayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SinjinSong on 2017/10/7.
 */
@Service
@Slf4j
public class PayServiceImpl implements PayService {
    @Autowired
    private BalanceDOMapper balanceDOMapper;
    @Autowired
    private OrderService orderService;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    @Override
    public void deposit(Long userId, Integer amount) {
        BalanceDO balanceDO = balanceDOMapper.selectByPrimaryKey(userId);
        balanceDO.setBalance(balanceDO.getBalance() + amount);
        balanceDOMapper.updateByPrimaryKeySelective(balanceDO);
    }

    /**
     * 这里会涉及分布式事务
     * 本地事务是更新用户的余额表
     * 远程事务是更新订单的状态
     *
     * @param order
     * @param paymentPassword
     */
    @Transactional
    @Override
    public void pay(OrderDO order, String paymentPassword) {
        log.info("order:{}",order);
        if (order.getOrderStatus() != OrderStatus.UNPAID) {
            throw new OrderStateIllegalException(order.getOrderStatus().toString());
        }
        BalanceDO balanceDO = balanceDOMapper.selectByPrimaryKey(order.getUser().getId());
        if (balanceDO == null) {
            throw new BalanceNotEnoughException("0");
        }
        if (order.getTotalPrice().compareTo(balanceDO.getBalance()) > 0) {
            throw new BalanceNotEnoughException(String.valueOf(balanceDO.getBalance()));
        }
        log.info("paymentPassword:{}", paymentPassword);
        log.info("passwordEncoder.encode(\"admin\"):{}", passwordEncoder.encode("admin"));
        log.info(" balanceDO.getPaymentPassword():{}", balanceDO.getPaymentPassword());
        log.info("match?:{}",passwordEncoder.matches(paymentPassword, balanceDO.getPaymentPassword()));
        if (!passwordEncoder.matches(paymentPassword, balanceDO.getPaymentPassword())) {
            throw new PaymentPasswordInCorrectException(order.getUser().getUsername());
        }
        balanceDO.setBalance(balanceDO.getBalance() - order.getTotalPrice());
        balanceDOMapper.updateByPrimaryKeySelective(balanceDO);

        order.setOrderStatus(OrderStatus.PAID);
        orderService.updateOrder(order);
    }

    @Transactional
    @Override
    public void setPaymentPassword(Long userId, String oldPaymentPassword, String newPaymentPassword) {
        BalanceDO balanceDO = balanceDOMapper.selectByPrimaryKey(userId);
        if (StringUtils.isNotEmpty(balanceDO.getPaymentPassword()) && !passwordEncoder.matches(oldPaymentPassword, balanceDO.getPaymentPassword())) {
            throw new PaymentPasswordInCorrectException(balanceDO.getUser().getUsername());
        }
        balanceDO.setPaymentPassword(passwordEncoder.encode(newPaymentPassword));
        balanceDOMapper.updateByPrimaryKeySelective(balanceDO);

    }
}

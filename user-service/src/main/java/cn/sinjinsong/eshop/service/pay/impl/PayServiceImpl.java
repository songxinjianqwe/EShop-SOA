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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SinjinSong on 2017/10/7.
 */
@Service
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

    @Transactional
    @Override
    public void pay(OrderDO order, String paymentPassword) {
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

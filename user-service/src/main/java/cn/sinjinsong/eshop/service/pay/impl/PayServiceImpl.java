package cn.sinjinsong.eshop.service.pay.impl;

import cn.sinjinsong.eshop.common.domain.entity.pay.BalanceDO;
import cn.sinjinsong.eshop.common.exception.pay.PaymentPasswordInCorrectException;
import cn.sinjinsong.eshop.dao.pay.BalanceDOMapper;
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
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void deposit(Long userId, Integer amount) {
        BalanceDO balanceDO = balanceDOMapper.selectByPrimaryKey(userId);
        balanceDO.setBalance(balanceDO.getBalance() + amount);
        balanceDOMapper.updateByPrimaryKeySelective(balanceDO);
    }


    @Transactional
    @Override
    public void setPaymentPassword(Long userId, String oldPaymentPassword, String newPaymentPassword) {
        BalanceDO balanceDO = balanceDOMapper.selectByPrimaryKey(userId);
        if (StringUtils.isNotEmpty(balanceDO.getPaymentPassword()) && !passwordEncoder.matches(oldPaymentPassword, balanceDO.getPaymentPassword())) {
            throw new PaymentPasswordInCorrectException(balanceDO.getUser().getId());
        }
        balanceDO.setPaymentPassword(passwordEncoder.encode(newPaymentPassword));
        balanceDOMapper.updateByPrimaryKeySelective(balanceDO);
    }
}

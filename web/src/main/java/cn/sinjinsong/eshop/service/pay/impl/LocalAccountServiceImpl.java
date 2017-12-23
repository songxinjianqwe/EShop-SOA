package cn.sinjinsong.eshop.service.pay.impl;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.service.pay.AccountService;
import cn.sinjinsong.eshop.service.pay.LocalAccountService;
import lombok.extern.slf4j.Slf4j;
import org.bytesoft.compensable.Compensable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sinjinsong
 * @date 2017/12/24
 */
@Service
@Compensable(interfaceClass = LocalAccountService.class)
@Slf4j
public class LocalAccountServiceImpl implements LocalAccountService {
    @Autowired
    private AccountService remoteAccountService;
    
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void pay(OrderDO order, String paymentPassword) {
        remoteAccountService.pay(order,paymentPassword);
    }
}

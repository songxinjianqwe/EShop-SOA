package cn.sinjinsong.eshop.service.pay.impl;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.service.pay.AccountService;
import com.alibaba.rocketmq.client.producer.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sinjinsong
 * @date 2017/12/23
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private MQProducer producer;
    
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void pay(OrderDO order, String paymentPassword) {
        this.producer.sendMessageInTransaction(message, localTransactionExecuter, transactionMapArgs);
    }
}

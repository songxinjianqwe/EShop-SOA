package cn.sinjinsong.eshop.service.pay.impl;

import cn.sinjinsong.eshop.common.domain.entity.message.ProducerTransactionMessageDO;
import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import cn.sinjinsong.eshop.common.util.ProtoStuffUtil;
import cn.sinjinsong.eshop.config.MQProducerConfig;
import cn.sinjinsong.eshop.exception.pay.OrderPaymentException;
import cn.sinjinsong.eshop.mq.AccountLocalTransactionExecutor;
import cn.sinjinsong.eshop.service.message.ProducerTransactionMessageService;
import cn.sinjinsong.eshop.service.pay.AccountService;
import cn.sinjinsong.eshop.service.pay.PayService;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author sinjinsong
 * @date 2017/12/23
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private MQProducerConfig config;
    @Autowired
    private MQProducer producer;
    @Autowired
    private AccountLocalTransactionExecutor executor;
    @Autowired
    private ProducerTransactionMessageService messageService;
    @Autowired
    private PayService payService;


    @Override
    public void commit(OrderDO order, String paymentPassword) {
        Message message = new Message();
        message.setTopic(config.getTopic());
        message.setBody(ProtoStuffUtil.serialize(order));
        TransactionSendResult result = null;
        try {
            result = this.producer.sendMessageInTransaction(message, executor, paymentPassword);
            log.info("事务消息发送结果：{}", result);
            log.info("TransactionState:{} ", result.getLocalTransactionState());
            // 因为无法获得executor中抛出的异常，只能模糊地返回订单支付失败信息。
            // TODO 想办法从executor中找到原生异常
        } catch (Exception e) {
            log.info("AccountService抛出异常...");
            e.printStackTrace();
        }
        if (result.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
            throw new OrderPaymentException(order.getId());
        }
    }

    @Transactional
    @Override
    public void rollback(ProducerTransactionMessageDO message) {
        OrderDO order = ProtoStuffUtil.deserialize(message.getBody(), OrderDO.class);
        message.setMessageStatus(MessageStatus.ROLLBACK);
        message.setUpdateTime(LocalDateTime.now());
        messageService.update(message);
        payService.increaseAccount(order.getUser().getId(), order.getTotalPrice());
    }
}

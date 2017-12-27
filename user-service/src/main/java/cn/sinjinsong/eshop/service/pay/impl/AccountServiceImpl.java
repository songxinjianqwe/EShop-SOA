package cn.sinjinsong.eshop.service.pay.impl;

import cn.sinjinsong.eshop.common.domain.entity.order.OrderDO;
import cn.sinjinsong.eshop.exception.pay.OrderPaymentException;
import cn.sinjinsong.eshop.common.util.ProtoStuffUtil;
import cn.sinjinsong.eshop.config.MQProducerConfig;
import cn.sinjinsong.eshop.mq.AccountLocalTransactionExecutor;
import cn.sinjinsong.eshop.service.pay.AccountService;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Override
    public void pay(OrderDO order, String paymentPassword) {
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
            // 消息发送失败，dubbo服务会重试三次
            e.printStackTrace();
        }
        if (result.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
            throw new OrderPaymentException(order.getId());
        }
    }
}

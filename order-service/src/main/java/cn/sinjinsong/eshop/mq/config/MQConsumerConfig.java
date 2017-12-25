package cn.sinjinsong.eshop.mq.config;

import cn.sinjinsong.eshop.mq.AccountMessageListener;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author sinjinsong
 * @date 2017/12/25
 */
@Configuration
@Slf4j
public class MQConsumerConfig {
    @Value("${spring.rocketmq.group-name}")
    private String GROUP_NAME;
    @Value("${spring.rocketmq.namesrv-addr}")
    private String NAMESRV_ADDR;
    @Value("${spring.rocktmq.topic}")
    private String TOPIC;
    private DefaultMQPushConsumer consumer;
    @Autowired
    private AccountMessageListener accountMessageListener;
    
    @PostConstruct
    public void init() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer(GROUP_NAME);
        this.consumer.setNamesrvAddr(NAMESRV_ADDR);
        this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        this.consumer.subscribe(TOPIC, "*");
        this.consumer.registerMessageListener(accountMessageListener);
        this.consumer.start();
        log.info("consumer started!");
    }
    
    
}

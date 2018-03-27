package cn.sinjinsong.eshop.config;

import cn.sinjinsong.eshop.mq.AccountMessageListener;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import lombok.Getter;
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
@Getter
public class MQConsumerConfig {
    private DefaultMQPushConsumer consumer;
    
    @Value("${spring.rocketmq.consumer.group-name}")
    private String groupName;
    @Value("${spring.rocketmq.consumer.namesrv-addr}")
    private String namesrvAddr;
    @Value("${spring.rocketmq.consumer.topic}")
    private String topic;
    @Autowired
    private AccountMessageListener accountMessageListener;
    @Value("${spring.rocketmq.consumer.consume-failure-retry-times}")
    private Integer retryTimes;
    @Value("${spring.rocketmq.consumer.check-keys}")
    private String checkKeys;
    
    @PostConstruct
    public void init() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer(groupName);
        this.consumer.setNamesrvAddr(namesrvAddr);
        // 启动后从队列头部开始消费
        this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        this.consumer.subscribe(topic, "*");
        this.consumer.registerMessageListener(accountMessageListener);
        this.consumer.start();
        log.info("consumer started!");
    }
}

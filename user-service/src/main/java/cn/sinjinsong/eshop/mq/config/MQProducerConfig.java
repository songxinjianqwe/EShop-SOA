package cn.sinjinsong.eshop.mq.config;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.TransactionMQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
@Configuration
@Slf4j
public class MQProducerConfig {
    @Value("${spring.rocketmq.group-name}")
    private String GROUP_NAME;
    @Value("${spring.rocketmq.namesrv-addr}")
    private String NAMESRV_ADDR;
    @Value("${spring.rocketmq.topic}")
    private String TOPIC;
    @Value("${spring.rocketmq.check-thread-pool}")
    private String checkThreadPool;
    
    @Bean
    public MQProducer mqProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(GROUP_NAME);
        producer.setNamesrvAddr(NAMESRV_ADDR); 
        producer.start();
        return producer;
    }
    
}

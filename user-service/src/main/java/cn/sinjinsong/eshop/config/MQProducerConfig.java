package cn.sinjinsong.eshop.config;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionMQProducer;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.Getter;
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
@Getter
public class MQProducerConfig {
    @Value("${spring.rocketmq.producer.group-name}")
    private String groupName;
    @Value("${spring.rocketmq.producer.namesrv-addr}")
    private String namesrvAddr;
    @Value("${spring.rocketmq.producer.topic}")
    private String topic;
    @Value("${spring.rocketmq.producer.confirm-message-faiure-retry-times}")  
    private Integer retryTimes;
    public static final Integer CHECK_GAP = 1; 
    @Value("${spring.rocketmq.producer.check-keys}")
    private String checkKeys;
    
    @Bean
    public MQProducer mqProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(groupName);
        producer.setNamesrvAddr(namesrvAddr); 
        producer.setTransactionCheckListener(new TransactionCheckListener() {
            @Override
            public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
                // doNothing
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				producer.shutdown();
			}
		}));
        producer.start();
        log.info("producer started!");
        return producer;
    }
}

package cn.sinjinsong.eshop.mq.scheduler;

import cn.sinjinsong.eshop.service.message.ProducerTransactionMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author sinjinsong
 * @date 2017/12/27
 */
@Component
public class TransactionCheckScheduler {
    @Autowired
    private ProducerTransactionMessageService messageService;
    
    /**
     * 每分钟执行一次事务回查
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void checkTransactionMessage(){
        messageService.check();
    }
}

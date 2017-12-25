package cn.sinjinsong.eshop.common.aop;

import cn.sinjinsong.eshop.common.condition.DBCondition;
import cn.sinjinsong.eshop.common.config.db.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SinjinSong on 2017/8/20.
 */
@Aspect
@Order(1)
@Configuration
@Conditional(DBCondition.class)
@Slf4j
public class DataSourceAspect {
    
    @Before("@annotation(transaction)")
    public void switchDataSourceType(Transactional transaction) {
        if(transaction.readOnly()){
            DataSourceContextHolder.read();
            log.info("dataSource切换到：Read");
        }else{
            DataSourceContextHolder.write();
            log.info("dataSource切换到：Write");
        }
    }
}

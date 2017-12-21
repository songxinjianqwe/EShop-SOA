package cn.sinjinsong.eshop.common.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by SinjinSong on 2017/9/22.
 */
@Slf4j
public class DBCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        log.info("条件：{}",context.getEnvironment().getProperty("mybatis.mapper-locations"));
        log.info("条件：{}",context.getEnvironment().getProperty("mybatis.config-location"));
        
        return context.getEnvironment().getProperty("mybatis.mapper-locations") != null &&
                context.getEnvironment().getProperty("mybatis.config-location") != null;
    }
}

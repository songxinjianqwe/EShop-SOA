package cn.sinjinsong.eshop.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;

@Aspect
@Order(2)
@Configuration
@Slf4j
public class LoggingAspect {
    @Pointcut("@within(org.springframework.stereotype.Service)||@annotation(org.springframework.web.bind.annotation.RequestMapping)||execution(* cn.sinjinsong.skeleton.dao..*.*(..))")
    public void declareJoinPointExpression() {
    }

    @Before("declareJoinPointExpression()")
    public void beforeMethod(JoinPoint joinPoint) {
        // 取得方法参数
        Object[] args = joinPoint.getArgs();
        log.info("The method [ {} ] begins with Parameters: {}", joinPoint.getSignature(), Arrays.toString(args));
    }

    @AfterReturning(value = "declareJoinPointExpression()", returning = "result")
    public void afterMethodReturn(JoinPoint joinPoint, Object result) {
        log.info("The method [ {} ] ends with Result: {}", joinPoint.getSignature(), result);
    }

//    @AfterThrowing(value = "declareJoinPointExpression()", throwing = "e")
//    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
//        log.error("Error happened in method: [ {} ]", joinPoint.getSignature());
//        log.error("Parameters: {}", Arrays.toString(joinPoint.getArgs()));
//        log.error("Exception StackTrace: {}", e);
//    }
}

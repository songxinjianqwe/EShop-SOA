package cn.sinjinsong.eshop.exception.handler;

import cn.sinjinsong.eshop.common.aop.LoggingAspect;
import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by SinjinSong on 2017/5/6.
 */
@Component
public class EmailExceptionHandler extends SimpleAsyncUncaughtExceptionHandler {
    private static final Logger logger = Logger.getLogger(LoggingAspect.class.getName());
    
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        logger.error(String.format("%s[%s] throws an exception", method.getName(), Arrays.toString(objects)), throwable);
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            method.invoke(objects);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

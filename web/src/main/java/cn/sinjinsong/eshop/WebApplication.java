package cn.sinjinsong.eshop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by SinjinSong on 2017/9/22.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties
@ComponentScan({"cn.sinjinsong"})
@ImportResource({"classpath:dubbo.xml"})
@Slf4j
public class WebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        synchronized (WebApplication.class) {
            while (true) {
                try {
                    WebApplication.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }
}

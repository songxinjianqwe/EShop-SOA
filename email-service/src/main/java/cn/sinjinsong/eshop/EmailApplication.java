package cn.sinjinsong.eshop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by SinjinSong on 2017/9/22.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties
@ComponentScan({"cn.sinjinsong"})
@ImportResource("classpath:dubbo.xml")
@Slf4j
public class EmailApplication implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EmailApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
        synchronized (EmailApplication.class) {
            while (true) {
                try {
                    EmailApplication.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
    }
}

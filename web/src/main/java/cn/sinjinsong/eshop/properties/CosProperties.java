package cn.sinjinsong.eshop.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by SinjinSong on 2017/10/18.
 * 腾讯云COS
 */
@Component
@ConfigurationProperties
@PropertySource("classpath:cos.properties")
@Getter
@Setter
@Slf4j
public class CosProperties {
    private String appId;
    private String secretId;
    private String secretKey;
}

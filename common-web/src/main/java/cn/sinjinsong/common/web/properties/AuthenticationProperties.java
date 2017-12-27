package cn.sinjinsong.common.web.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@Component
@ConfigurationProperties(prefix = "auth")
@PropertySource("classpath:auth.properties")
@Setter
@Getter
public class AuthenticationProperties {
    private String secretKey;
    private Integer tokenExpireTime;
    private Integer captchaExpireTime;
    private Integer activationCodeExpireTime;
    private Integer forgetNameCodeExpireTime;
    
    public static final String AUTH_HEADER = "Authentication";
    public static final String USER_ID = "id";
    public static final String LOGIN_URL = "/tokens";
    public static final String EXCEPTION_ATTR_NAME = "BaseRestException";
}

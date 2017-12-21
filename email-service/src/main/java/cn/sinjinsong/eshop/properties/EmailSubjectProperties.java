package cn.sinjinsong.eshop.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by SinjinSong on 2017/7/11.
 */
@Component
@ConfigurationProperties
@PropertySource("classpath:email-subject.properties")
@Getter
@Setter
@Slf4j
public class EmailSubjectProperties {
    private Map<String,String> subjects;
    public String getProperty(String key) {
        return subjects.get(key);
    }
}

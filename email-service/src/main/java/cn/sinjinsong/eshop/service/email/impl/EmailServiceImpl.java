package cn.sinjinsong.eshop.service.email.impl;

import cn.sinjinsong.eshop.exception.email.EmailTemplateNotFoundException;
import cn.sinjinsong.eshop.properties.EmailSubjectProperties;
import cn.sinjinsong.eshop.service.email.EmailService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/5/5.
 */
@Service
@Async("emailExecutor")
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private EmailSubjectProperties subjectProperties;
    private String username;
    
    @Override
    public void sendHTML(String to, String subject, Map<String, Object> params, List<String> filePaths) {
        Context context = new Context();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String emailContent = templateEngine.process(subject , context);
        send(to, subjectProperties.getProperty(subject), emailContent, filePaths);
    }

    @Override
    public void sendBatch(List<String> tos, String subject, Map<String, Object> params, List<String> filePaths) {
        StringBuffer sb = new StringBuffer();
        Context context = new Context();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String emailContent = templateEngine.process(subject, context);
        for (int i = 0; i < tos.size(); ++i) {
            sb.append(tos.get(i));
            if (i != tos.size() - 1) {
                sb.append(",");
            }
        }
        send(sb.toString(), subjectProperties.getProperty(subject), emailContent, filePaths);
    }

    @Override
    public void send(String to, String subject, String content, List<String> filePaths) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //true表示需要创建一个multipart message
            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            if (filePaths != null && filePaths.size() > 0) {
                File file;
                FileSystemResource fileSystemResource;
                for (String filePath : filePaths) {
                    file = new File(filePath);
                    if (!file.exists()) {
                        throw new EmailTemplateNotFoundException(filePath);
                    }
                    fileSystemResource = new FileSystemResource(file);
                    helper.addAttachment(filePath.substring(filePath.lastIndexOf(File.separator)), fileSystemResource);
                }
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        javaMailSender.send(message);
    }

}

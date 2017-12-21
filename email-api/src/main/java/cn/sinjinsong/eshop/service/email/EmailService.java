package cn.sinjinsong.eshop.service.email;

import java.util.List;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/5/5.
 */

public interface EmailService {
    /**
     * 
     * @param to 收信人的邮箱
     * @param subject 主题，是mailSubject配置文件中的key
     * @param filePaths 附件文件名们，不发送附件可以为空
     * @return
     */
    void send(String to, String subject, String content, List<String> filePaths);

    /**
     * 
     * @param to 收信人的邮箱
     * @param subject 主题，是mailSubject配置文件中的key，也是emailTemplates中的html模板文件名
     * @param params 待绑定的参数
     * @param filePaths 附件文件名们，不发送附件可以为空
     * @return
     */
    void sendHTML(String to, String subject, Map<String, Object> params, List<String> filePaths);
    
    void sendBatch(List<String> to, String subject, Map<String, Object> params, List<String> filePaths);
}

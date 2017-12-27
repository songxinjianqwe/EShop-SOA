package cn.sinjinsong.common.web.security.verification;

/**
 * Created by SinjinSong on 2017/5/6.
 */
public interface VerificationManager {
    void createVerificationCode(String key, String value, long expireTime);

    boolean checkVerificationCode(String key, String value);

    void deleteVerificationCode(String key);
}

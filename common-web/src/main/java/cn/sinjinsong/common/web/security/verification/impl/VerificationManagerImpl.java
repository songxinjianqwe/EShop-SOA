package cn.sinjinsong.common.web.security.verification.impl;

import cn.sinjinsong.eshop.common.cache.RedisCacheManager;
import cn.sinjinsong.common.web.security.verification.VerificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by SinjinSong on 2017/5/6.
 */
@Component
@Slf4j
class VerificationManagerImpl implements VerificationManager {
    @Autowired
    private RedisCacheManager redisCacheManager;
    
    @Override
    public void createVerificationCode(String key, String value,long expireTime) {
        log.info("creating...");
        log.info("expireTime:{}",expireTime);
        redisCacheManager.putWithExpireTime(key,value,expireTime);
    }

    @Override
    public boolean checkVerificationCode(String key, String value) {
        String realValue = redisCacheManager.get(key, String.class);
        if(realValue == null){
            return false;
        }
        if(!value.equals(realValue)){
            return false;
        }
        return true;
    }

    @Override
    public void deleteVerificationCode(String key) {
        redisCacheManager.delete(key);
    }
}

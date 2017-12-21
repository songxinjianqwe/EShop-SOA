package cn.sinjinsong.eshop.util;


import cn.sinjinsong.eshop.common.properties.CharsetProperties;
import cn.sinjinsong.eshop.properties.CosProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by SinjinSong on 2017/10/18.
 */
@Component
@Slf4j
public class CosUtil {
    @Autowired
    private CosProperties cosProperties;
    private ThreadLocalRandom threadLocalRandom;
    private static final String MAC_NAME = "HmacSHA1";

    /**
     * 
     * @param cosPath
     * @param bucket
     * @return
     */
    public String getSign(String bucket, String cosPath) {
       try {  
            String Original = getSignOriginal(bucket,cosPath);  
            byte[] HmacSHA1 = HmacSHA1Encrypt(cosProperties.getSecretKey(), Original);  
            byte[] all = new byte[HmacSHA1.length + Original.getBytes(CharsetProperties.charset).length];  
            System.arraycopy(HmacSHA1, 0, all, 0, HmacSHA1.length);  
            System.arraycopy(Original.getBytes(CharsetProperties.charset), 0, all, HmacSHA1.length, Original.getBytes(CharsetProperties.charset).length);  
            String SignData = Base64UtilForCos.encode(all);  
            return SignData;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "get sign failed";  
    }

    private byte[] HmacSHA1Encrypt(String SecretKey, String EncryptText) throws Exception {
        byte[] data = SecretKey.getBytes(CharsetProperties.charset);
        javax.crypto.SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);
        byte[] text = EncryptText.getBytes(CharsetProperties.charset);
        return mac.doFinal(text);
    }
    
    private String getRandomTenStr() {
        threadLocalRandom = ThreadLocalRandom.current();
        StringBuilder randomStr = new StringBuilder();
        randomStr.append(String.valueOf(new Random().nextInt(8) + 1));
        int random = threadLocalRandom.nextInt(3) + 5;
        for (int i = 0; i < random; i++) {
            randomStr.append(String.valueOf(threadLocalRandom.nextInt(9)));
        }
        return randomStr.toString();
    }

    private long getLinuxDateSimple() {
        try {
            long unixTimestamp = System.currentTimeMillis() / 1000L;
            return unixTimestamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * getLinuxDateSimple()获取时间戳，单位秒
     * getRandomTenStr()获取随机数
     */
    private String getSignOriginal(String bucket,String cosPath) {
         return String.format(  
                "a=%s&b=%s&k=%s&e=%s&t=%s&r=%s&f=",  
                cosProperties.getAppId(),  
                bucket,  
                cosProperties.getSecretId(),  
                String.valueOf(getLinuxDateSimple() + 60),  
                String.valueOf(getLinuxDateSimple()),  
                getRandomTenStr());  
    }
}  
    
  
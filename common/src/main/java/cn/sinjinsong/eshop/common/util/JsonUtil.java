package cn.sinjinsong.eshop.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by SinjinSong on 2017/5/9.
 */
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static String json(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

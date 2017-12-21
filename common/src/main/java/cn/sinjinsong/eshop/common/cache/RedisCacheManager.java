package cn.sinjinsong.eshop.common.cache;

import java.util.List;

/**
 * Created by SinjinSong on 2017/4/27.
 */
public interface RedisCacheManager {
    
    <T> boolean put(String key, T obj);

    <T> boolean putWithExpireTime(String key, T obj, final long expireTime);

    <T> boolean putList(String key, List<T> objList);

    <T> boolean putListWithExpireTime(String key, List<T> objList, final long expireTime);

    <T> T get(final String key, Class<T> targetClass);

    <T> List<T> getList(final String key, Class<T> targetClass);

    void delete(String key);

    void deleteCacheWithPattern(String pattern);

    void clearCache();
       
}

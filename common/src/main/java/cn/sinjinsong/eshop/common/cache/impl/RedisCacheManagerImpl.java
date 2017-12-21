package cn.sinjinsong.eshop.common.cache.impl;

import cn.sinjinsong.eshop.common.cache.RedisCacheManager;
import cn.sinjinsong.eshop.common.properties.CharsetProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * redis缓存
 *
 * @author SinjinSong
 */
@Component("redisCacheManager")
public class RedisCacheManagerImpl implements RedisCacheManager {
        
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public boolean put(String key, Object obj) {
        final byte[] keyBytes = key.getBytes(CharsetProperties.charset);
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        final byte[] valueBytes = valueSerializer.serialize(obj);
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.setNX(keyBytes, valueBytes);
            }
        });
        return result;
    }

    public <T> boolean putWithExpireTime(String key, T obj, final long expireTime) {
        final byte[] keyBytes = key.getBytes(CharsetProperties.charset);
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        final byte[] valueBytes = valueSerializer.serialize(obj);
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(keyBytes, expireTime, valueBytes);
                return true;
            }
        });
        return result;
    }

    public <T> boolean putList(String key, List<T> objList) {
        return put(key, objList);
    }

    public <T> boolean putListWithExpireTime(String key, List<T> objList, final long expireTime) {
        return putWithExpireTime(key, objList, expireTime);
    }

    public <T> T get(final String key, Class<T> targetClass) {
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key.getBytes(CharsetProperties.charset));
            }
        });
        if (result == null) {
            return null;
        }
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        return (T) valueSerializer.deserialize(result);
    }

    public <T> List<T> getList(final String key, Class<T> targetClass) {
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key.getBytes(CharsetProperties.charset));
            }
        });
        if (result == null) {
            return null;
        }
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        return (List<T>) valueSerializer.deserialize(result);
    }

    /**
     * 精确删除key
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 模糊删除key
     *
     * @param pattern
     */
    public void deleteCacheWithPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 清空所有缓存
     */
    public void clearCache() {
        deleteCacheWithPattern("*");
    }
}

package com.cm.cloud.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisClient {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 是否存在key
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置指定 key 的值
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    /**
     * 设置指定 key 的值超时秒
     * @param key
     * @param value
     */
    public void setTime(String key, String value,Long time) {
        redisTemplate.opsForValue().set(key, value,time,TimeUnit.MINUTES);
    }
    /**
     * 设置指定 key 的值
     * @param key
     * @param value
     */
    public void setex(String key, String value, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,value,time,timeUnit);
    }
    /**
     * 获取指定 key 的值
     * @param key
     * @return
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 精确删除
     * @param key
     */
    public void del(String key){
        redisTemplate.delete(key);
    }

    /**
     * 模糊删除
     * @param key
     */
    public void delAll(String key){
        Set<String> keys = redisTemplate.keys(key+"*");
        redisTemplate.delete(keys);
    }
}

package com.swp.springboot.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述:
 * redis服务
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-06 3:26 PM
 */
@Component
public class RedisService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 默认过期时间
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时间
     */
    public static final long NOT_EXPIRE = -1;

    /**
     * key是否存在
     * @param key
     * @return
     */
    public boolean existKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 重命名，如果newKey已经存在，则newKey的原值被覆盖
     * @param oldKey
     * @param newKey
     */
    public void renameKey(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * newKey不存在时才重命名
     * @param oldKey
     * @param newKey
     * @return
     */
    public boolean renameKeyNotExist(String oldKey, String newKey) {
         return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 删除key
     * @param key
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     * @param keys
     */
    public void deleteKey(String... keys) {
        Set<String> set = Stream.of(keys).collect(Collectors.toSet());
        redisTemplate.delete(set);
    }
    /**
     * 删除Key的集合
     *
     */
    public void deleteKey(Collection<String> keys) {
        Set<String> set = keys.stream().collect(Collectors.toSet());
        redisTemplate.delete(set);
    }

    /**
     * 设置key的声明周期
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expireKey(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     * @param key
     * @param date
     */
    public void expireKeyAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    /**
     * 获取key的生命周期
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效
     * @param key
     */
    public void persisKey(String key) {
        redisTemplate.persist(key);
    }

}

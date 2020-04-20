package org.siu.akagi.authentication.jwt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;

import javax.annotation.Resource;
import java.io.Serializable;
import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * @Author Siu
 * @Date 2020/3/29 15:01
 * @Version 0.0.1
 */
@Slf4j
public class CacheManager {

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    protected static final Cache<String, Key> TOKEN_SIGN_KEY_CACHE = CacheBuilder.newBuilder()
            //设置cache的初始大小
            .initialCapacity(10)
            // 缓存的最大大小
            .maximumSize(2000)
            // 缓存的最大重量 注:此功能不能与@link maximumsize结合使用。
            // .maximumWeight(2000)
            //设置并发数为n，即同一时间最多只能有n个线程往cache执行写入操作
            .concurrencyLevel(16)
            //设置cache中的数据在写入之后的存活时间为7天
            .expireAfterWrite(7, TimeUnit.DAYS)
            //设置缓存多久没读就自动清除
            .expireAfterAccess(8, TimeUnit.HOURS)
            //构建cache实例
            .build();


    protected static final Cache<String, Authentication> TOKEN_AUTHORITIES_CACHE = CacheBuilder.newBuilder()
            //设置cache的初始大小
            .initialCapacity(10)
            // 缓存的最大大小
            .maximumSize(2000)
            // 缓存的最大重量 注:此功能不能与@link maximumsize结合使用。
            // .maximumWeight(2000)
            //设置并发数为n，即同一时间最多只能有n个线程往cache执行写入操作
            .concurrencyLevel(16)
            //设置cache中的数据在写入之后的存活时间为7天
            .expireAfterWrite(7, TimeUnit.DAYS)
            //设置缓存多久没读就自动清除
            .expireAfterAccess(8, TimeUnit.HOURS)
            //构建cache实例
            .build();

    protected static final Cache<String, String> COMMON_CACHE = CacheBuilder.newBuilder()
            //设置cache的初始大小
            .initialCapacity(10)
            // 缓存的最大大小
            .maximumSize(2000)
            // 缓存的最大重量 注:此功能不能与@link maximumsize结合使用。
            // .maximumWeight(2000)
            //设置并发数为n，即同一时间最多只能有n个线程往cache执行写入操作
            .concurrencyLevel(16)
            //设置cache中的数据在写入之后的存活时间为7天
            .expireAfterWrite(7, TimeUnit.DAYS)
            //设置缓存多久没读就自动清除
            .expireAfterAccess(8, TimeUnit.HOURS)
            //构建cache实例
            .build();


    /**
     * 获取权限
     *
     * @param key
     * @return
     */
    public Authentication getAuthentication(String key) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_AUTHORITIES_CACHE.stats().toString());
        }
        Authentication authentication = null;
        if (redisExists(key)) {
            authentication = TOKEN_AUTHORITIES_CACHE.getIfPresent(key);
            if (authentication == null) {
                ValueOperations<String, Serializable> operations = redisTemplate.opsForValue();
                authentication = (Authentication) operations.get(key);
                setAuthentication(key, authentication);
            }
        }

        return authentication;
    }

    public void setAuthentication(String key, Authentication value) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_AUTHORITIES_CACHE.stats().toString());
        }
        TOKEN_AUTHORITIES_CACHE.put(key, value);
        redisSet(key, value);
    }

    public void setString(String key, String value) {
        if (log.isDebugEnabled()) {
            log.info(COMMON_CACHE.stats().toString());
        }
        COMMON_CACHE.put(key, value);
        redisSet(key, value);
    }

    /**
     * 获取缓存中的值
     *
     * @param signKeyRedisKey
     * @return
     */
    public Key getSignKey(String signKeyRedisKey) {
        Key key = null;
        if (redisExists(signKeyRedisKey)) {
            key = TOKEN_SIGN_KEY_CACHE.getIfPresent(signKeyRedisKey);
            if (key == null) {
                ValueOperations<String, Serializable> operations = redisTemplate.opsForValue();
                key = (Key) operations.get(signKeyRedisKey);
                setSignKey(signKeyRedisKey, key);
            }
        }

        return key;
    }


    /**
     * 放入缓存
     *
     * @param key
     * @param value
     */
    public void setSignKey(String key, Key value) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_SIGN_KEY_CACHE.stats().toString());
        }
        TOKEN_SIGN_KEY_CACHE.put(key, value);
        redisSet(key, value);
    }

    /**
     * 删除
     *
     * @param key
     */
    public void removeSignKey(String key) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_SIGN_KEY_CACHE.stats().toString());
        }
        TOKEN_SIGN_KEY_CACHE.invalidate(key);
        redisRemove(key);
    }


    public boolean redisSet(final String key, Serializable value) {
        boolean result = false;
        try {
            ValueOperations<String, Serializable> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("set error: key {}, value {}", key, value, e);
        }
        return result;
    }


    public void redisRemove(final String key) {
        redisTemplate.delete(key);
    }

    public boolean redisExists(final String key) {
        return redisTemplate.hasKey(key);
    }


}

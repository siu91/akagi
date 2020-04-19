package org.siu.akagi.authentication.jwt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * Token Sign key 缓存
 *
 * @Author Siu
 * @Date 2020/3/29 15:01
 * @Version 0.0.1
 */
@Slf4j
public class TokenCache {

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
            .expireAfterAccess(2, TimeUnit.DAYS)
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
            .expireAfterAccess(2, TimeUnit.DAYS)
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
            .expireAfterAccess(2, TimeUnit.DAYS)
            //构建cache实例
            .build();


    /**
     * 获取权限
     *
     * @param key
     * @return
     */
    public static Authentication getAuthentication(String key) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_AUTHORITIES_CACHE.stats().toString());
        }
        return TOKEN_AUTHORITIES_CACHE.getIfPresent(key);
    }

    public static void setAuthentication(String key, Authentication value) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_AUTHORITIES_CACHE.stats().toString());
        }
        TOKEN_AUTHORITIES_CACHE.put(key, value);
    }

    /**
     * 获取缓存中的值
     *
     * @param key
     * @return
     */
    public static Key getSignKey(String key) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_SIGN_KEY_CACHE.stats().toString());
        }
        return TOKEN_SIGN_KEY_CACHE.getIfPresent(key);
    }


    /**
     * 放入缓存
     *
     * @param key
     * @param value
     */
    public static void setSignKey(String key, Key value) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_SIGN_KEY_CACHE.stats().toString());
        }
        TOKEN_SIGN_KEY_CACHE.put(key, value);
    }

    /**
     * 删除
     *
     * @param key
     */
    public static void removeSignKey(String key) {
        if (log.isDebugEnabled()) {
            log.info(TOKEN_SIGN_KEY_CACHE.stats().toString());
        }
        TOKEN_SIGN_KEY_CACHE.invalidate(key);
    }

}

package org.siu.myboot.auth.service;


import org.siu.myboot.auth.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;

import java.io.Serializable;

/**
 * 封装redis 操作
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
public class AkagiTokenStatefulService {
    private Logger logger = LoggerFactory.getLogger(AkagiTokenStatefulService.class);

    private final RedisTemplate redisTemplate;

    public AkagiTokenStatefulService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }




    public Long getTokenVersion(final String userName) {
        Object result;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(Constant.RedisKey.USER_AUTH_KEY + userName);

        if (result != null) {
            return Long.parseLong(result.toString());
        }
        return null;
    }


    /**
     * @param userName
     * @param value
     * @return
     */
    public boolean setTokenVersion(final String userName, long value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(Constant.RedisKey.USER_AUTH_KEY + userName, value);
            result = true;
        } catch (Exception e) {
            logger.error("set error: key {}, value {}", Constant.RedisKey.USER_AUTH_KEY + userName, value, e);
        }
        return result;
    }



}

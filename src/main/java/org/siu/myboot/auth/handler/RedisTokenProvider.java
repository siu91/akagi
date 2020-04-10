package org.siu.myboot.auth.handler;


import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.model.AuthUser;
import org.siu.myboot.auth.service.SecretCache;
import org.siu.myboot.auth.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Optional;

/**
 * redis实现token有状态
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
public class RedisTokenProvider extends AbstractTokenProvider {
    private Logger logger = LoggerFactory.getLogger(RedisTokenProvider.class);

    protected SecretCache cache;

    public RedisTokenProvider(String refreshPermit, long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe, SecretCache cache) {
        super(refreshPermit, tokenValidityInSeconds, tokenValidityInSecondsForRememberMe);
        this.cache = cache;
    }

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public String getTokenSecret() {
        Optional<String> userName = SecurityUtils.getCurrentUsername();
        String key = Constant.RedisKey.USER_TOKEN_SECRET_KEY + userName.get();
        String s = cache.get(key);
        if (s != null) {
            return s;
        }

        Object result;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(Constant.RedisKey.USER_TOKEN_SECRET_KEY + userName.get());

        if (result != null) {
            return result.toString();
        }
        return null;
    }

    @Override
    public boolean setSecret() {
        Optional<AuthUser> currentUser = SecurityUtils.getCurrentUser();
        currentUser.ifPresent(authUser -> cache.set(Constant.RedisKey.USER_TOKEN_SECRET_KEY + authUser.getUsername(), authUser.toBase64()));
        return currentUser.filter(authUser -> set(Constant.RedisKey.USER_TOKEN_SECRET_KEY + authUser.getUsername(), authUser.toBase64())).isPresent();
    }


    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("set error: key {}, value {}", key, value, e);
        }
        return result;
    }

}

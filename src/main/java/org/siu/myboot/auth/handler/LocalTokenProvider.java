package org.siu.myboot.auth.handler;


import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.model.AuthUser;
import org.siu.myboot.auth.util.SecurityUtils;

import java.security.Key;
import java.util.Optional;

/**
 * redis实现token有状态
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
public class LocalTokenProvider extends AbstractTokenProvider {

    protected TokenSecretKeyCache cache;

    public LocalTokenProvider(String refreshPermit, long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe, TokenSecretKeyCache cache) {
        super(refreshPermit, tokenValidityInSeconds, tokenValidityInSecondsForRememberMe);
        this.cache = cache;
    }


    @Override
    public boolean setKey() {
        Optional<AuthUser> currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.isPresent()) {
            String base64 = currentUser.get().toBase64();
            Key key = toKey(base64);
            cache.set(Constant.RedisKey.USER_TOKEN_SECRET_KEY + currentUser.get().getUsername(), key);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public Key getKey() {
        Optional<String> userName = SecurityUtils.getCurrentUsername();
        return cache.get(Constant.RedisKey.USER_TOKEN_SECRET_KEY + userName.get());
    }


}

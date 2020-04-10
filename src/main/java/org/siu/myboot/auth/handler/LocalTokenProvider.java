package org.siu.myboot.auth.handler;


import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.handler.AbstractTokenProvider;
import org.siu.myboot.auth.model.AuthUser;
import org.siu.myboot.auth.service.SecretCache;
import org.siu.myboot.auth.util.SecurityUtils;

import java.util.Optional;

/**
 * redis实现token有状态
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
public class LocalTokenProvider extends AbstractTokenProvider {

    protected SecretCache cache;

    public LocalTokenProvider(String refreshPermit, long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe,SecretCache cache) {
        super(refreshPermit, tokenValidityInSeconds, tokenValidityInSecondsForRememberMe);
        this.cache = cache;
    }

    @Override
    public String getTokenSecret() {
        Optional<String> userName = SecurityUtils.getCurrentUsername();
        return cache.get(Constant.RedisKey.USER_TOKEN_SECRET_KEY + userName.get());
    }

    @Override
    public boolean setSecret() {
        Optional<AuthUser> currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.isPresent()) {
            cache.set(Constant.RedisKey.USER_TOKEN_SECRET_KEY + currentUser.get().getUsername(), currentUser.get().toBase64());
            return true;
        } else {
            return false;
        }

    }


}

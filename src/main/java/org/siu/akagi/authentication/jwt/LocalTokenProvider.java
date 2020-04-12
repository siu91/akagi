package org.siu.akagi.authentication.jwt;


import org.siu.akagi.model.AuthUser;
import org.siu.akagi.support.AkagiUtils;
import org.siu.akagi.constant.Constant;

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

    protected TokenSignKeyCache cache;

    public LocalTokenProvider(String refreshPermit, long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe, TokenSignKeyCache cache) {
        super(refreshPermit, tokenValidityInSeconds, tokenValidityInSecondsForRememberMe);
        this.cache = cache;
    }


    @Override
    public void removeKey() {
        Optional<String> user = AkagiUtils.getCurrentUsername();
        user.ifPresent(s -> cache.remove(Constant.RedisKey.USER_TOKEN_SECRET_KEY + s));
    }


    @Override
    public boolean setKey() {
        Optional<AuthUser> currentUser = AkagiUtils.getCurrentUser();
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
    public Key signKey(String user) {
        return cache.get(Constant.RedisKey.USER_TOKEN_SECRET_KEY + user);
    }


}

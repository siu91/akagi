package org.siu.akagi.authentication.jwt;


import org.siu.akagi.model.User;
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
    public void remove() {
        Optional<String> user = AkagiUtils.getCurrentUsername();
        user.ifPresent(s -> cache.remove(Constant.RedisKey.USER_TOKEN_SECRET_KEY + s));
    }


    @Override
    public void store() {
        Optional<User> currentUser = AkagiUtils.getCurrentUser();
        if (currentUser.isPresent()) {
            String base64 = currentUser.get().toBase64();
            Key key = toKey(base64);
            cache.set(Constant.RedisKey.USER_TOKEN_SECRET_KEY + currentUser.get().getUsername(), key);
        } else {
        }

    }

    @Override
    public Key get(String user) {
        return cache.get(Constant.RedisKey.USER_TOKEN_SECRET_KEY + user);
    }


}

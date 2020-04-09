package org.siu.myboot.auth.service;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * @Author Siu
 * @Date 2020/4/9 21:31
 * @Version 0.0.1
 */
public abstract class AbstractSecretService implements ITokenSecretService {

    @Override
    public Key getKey() {
        String secret = getTokenSecret();
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * 获取 token secret
     *
     * @return
     */
    protected abstract String getTokenSecret();

}

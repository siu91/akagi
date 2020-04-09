package org.siu.myboot.auth.service;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * 默认token secret 服务
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
public class DefaultTokenSecretService extends AbstractSecretService {

    private String secret;
    private Key key;

    public DefaultTokenSecretService(String secret) {
        this.secret = secret;
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Key getKey() {
        return this.key;
    }

    @Override
    public String getTokenSecret() {
        return this.secret;
    }

    @Override
    public boolean setTokenSecret() {
        // do nothing
        return true;
    }


}

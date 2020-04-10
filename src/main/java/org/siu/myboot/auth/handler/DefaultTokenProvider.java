package org.siu.myboot.auth.handler;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.siu.myboot.auth.handler.AbstractTokenProvider;

import java.security.Key;

/**
 * 默认token secret 服务
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
public class DefaultTokenProvider extends AbstractTokenProvider {

    private String secret;
    private Key key;

    public DefaultTokenProvider(String refreshPermit, long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe, String secret) {
        super(refreshPermit, tokenValidityInSeconds, tokenValidityInSecondsForRememberMe);
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
    public boolean setSecret() {
        // do nothing
        return true;
    }


}
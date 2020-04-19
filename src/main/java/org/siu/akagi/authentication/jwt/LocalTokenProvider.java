package org.siu.akagi.authentication.jwt;


import io.jsonwebtoken.Claims;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.context.AkagiSecurityContextHolder;
import org.siu.akagi.model.JWT;
import org.siu.akagi.model.User;
import org.siu.akagi.constant.Constant;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

/**
 * redis实现token有状态
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
@Slf4j
public class LocalTokenProvider extends AbstractTokenProvider {


    public LocalTokenProvider(long expire, long expire4Remember) {
        super(expire, expire4Remember);
    }

    @Override
    public JWT createToken(Authentication authentication, boolean remember) {
        this.storeSignKey();
        TokenPair tp = super.createTokenPair(authentication, remember);

        String user = ((User) authentication.getPrincipal()).getUsername();
        // 标记token 对应的权限
        TokenCache.setAuthentication(tp.getT().getKey(), authentication);
        // 标记 refresh 和token 对应关系
        TokenCache.COMMON_CACHE.put(tp.getRt().getKey(), tp.getT().getKey());

        return tp.toJWT(user);
    }

    @Override
    public JWT refreshToken() {
        this.storeSignKey();

        Optional<User> authUser = AkagiSecurityContextHolder.getCurrentUser();
        if (authUser.isPresent()) {
            Claims claims = authUser.get().getClaimsJws().getBody();
            String rti = claims.get(Constant.Auth.TOKEN_ID_KEY).toString();

            String ti = TokenCache.COMMON_CACHE.getIfPresent(rti);
            Authentication authentication = TokenCache.getAuthentication(ti);
            TokenPair tp = super.createTokenPair(authentication, true);
            // 标记token 对应的权限
            TokenCache.setAuthentication(tp.getT().getKey(), authentication);
            // 标记 refresh 和token 对应关系
            TokenCache.COMMON_CACHE.put(tp.getRt().getKey(), tp.getT().getKey());

            return tp.toJWT(claims.getSubject());
        }

        return null;
    }

    @Override
    public Token parseToken(String jwt) {
        Token token = super.authentication(jwt);
        Authentication authentication;
        if (TokenType.COMMON.equals(token.getType())) {
            String tokenId = token.getClaimsJws().getBody().get(Constant.Auth.TOKEN_ID_KEY).toString();
            authentication = TokenCache.getAuthentication(tokenId);
        } else {
            User principal = new User(token.username, "", AkagiSecurityContextHolder.getAkagiGlobalProperties().getRefreshTokenAuthorities());
            principal.setClaimsJws(token.claimsJws);
            authentication = new UsernamePasswordAuthenticationToken(principal, token, AkagiSecurityContextHolder.getAkagiGlobalProperties().getRefreshTokenAuthorities());
        }
        token.setAuthentication(authentication);

        return token;
    }

    @Override
    public void removeSignKey() {
        Optional<String> user = AkagiSecurityContextHolder.getCurrentUserName();
        user.ifPresent(s -> TokenCache.removeSignKey(Constant.RedisKey.USER_TOKEN_SECRET_KEY + s));
    }


    public void storeSignKey() {
        Optional<User> currentUser = AkagiSecurityContextHolder.getCurrentUser();
        if (currentUser.isPresent()) {
            String base64 = currentUser.get().toBase64();
            Key key = toKey(base64);
            TokenCache.setSignKey(Constant.RedisKey.USER_TOKEN_SECRET_KEY + currentUser.get().getUsername(), key);
        } else {
            log.info("current user is null");
        }

    }

    @Override
    public Key signKey(String user) {
        return TokenCache.getSignKey(Constant.RedisKey.USER_TOKEN_SECRET_KEY + user);
    }


}

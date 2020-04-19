package org.siu.akagi.authentication.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.context.AkagiSecurityContextHolder;
import org.siu.akagi.constant.Constant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * token 生成器
 *
 * @Author Siu
 * @Date 2020/3/4 15:01
 * @Version 0.0.1
 */
@Slf4j
public abstract class AbstractTokenProvider implements TokenProvider {


    /**
     * 默认token失效时间(秒)
     * 设置默认值，也可以从配置文件中配置
     */
    protected long expire;

    /**
     * 记住密码时token失效时间（秒）
     * 设置默认值，也可以从配置文件中配置
     */
    protected long expire4Remember;


    public AbstractTokenProvider(long expire, long expire4Remember) {
        this.expire = expire;
        this.expire4Remember = expire4Remember;
    }


    /**
     * 生成Key
     *
     * @param base64
     * @return
     */
    protected Key toKey(String base64) {
        byte[] keyBytes = Decoders.BASE64.decode(base64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     *
     * @param authentication
     * @param remember
     * @return
     */
    public TokenPair createTokenPair(Authentication authentication, boolean remember) {
        long now = (new Date()).getTime();
        Date validity;
        if (remember) {
            validity = new Date(now + this.expire4Remember * 1000);
        } else {
            validity = new Date(now + this.expire * 1000);
        }
        Key key = signKey(authentication.getName());
        Pair<String, String> token = this.buildJWT(TokenType.COMMON, key, authentication.getName(), validity);
        Pair<String, String> refreshToken = this.buildJWT(TokenType.REFRESH, key, authentication.getName(), validity);


        return new TokenPair(token, refreshToken);
    }


    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param remember
     * @return
     */
    protected String buildJWT(Authentication authentication, boolean remember) {
        // 过期时间处理
        long now = (new Date()).getTime();
        Date validity;
        if (remember) {
            validity = new Date(now + this.expire4Remember * 1000);
        } else {
            validity = new Date(now + this.expire * 1000);
        }

        // 构建token信息
        return buildJWT(authentication, validity, TokenType.COMMON);
    }


    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param validity
     * @return
     */
    protected String buildJWT(Authentication authentication, Date validity, TokenType provider) {
        String authorities;
        String originAuthorities = null;
        if (TokenType.REFRESH.equals(provider)) {
            authorities = AkagiSecurityContextHolder.getAkagiGlobalProperties().getJsonWebTokenRefreshPermit();
            originAuthorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(Constant.Auth.AUTHORITIES_SPLIT));
        } else {
            authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(Constant.Auth.AUTHORITIES_SPLIT));
        }

        // 构建token信息
        return buildJWT(authentication.getName(), authorities, originAuthorities, validity, provider);
    }


    /**
     * @param subject
     * @param authorities       权限标识
     * @param originAuthorities 源权限标识
     * @param validity
     * @return
     */
    protected String buildJWT(String subject, String authorities, String originAuthorities, Date validity, TokenType provider) {
        // 构建token信息
        return Jwts.builder()
                // 该JWT的签发者
                .setIssuer(provider.toString())
                // 该JWT所面向的用户:放入用户信息（用户名）
                .setSubject(subject)
                // 接收该JWT的一方
                // .setAudience("")
                // 放入权限信息
                .claim(Constant.Auth.AUTHORITIES_KEY, authorities)
                .claim(Constant.Auth.ORIGIN_AUTHORITIES_KEY, originAuthorities == null ? "" : originAuthorities)
                // 签名
                .signWith(this.signKey(subject), SignatureAlgorithm.HS512)
                // 过期时间
                .setExpiration(validity)
                // 生效开始时间
                .setNotBefore(new Date())
                // 在什么时候签发的
                .setIssuedAt(new Date())
                .compact();
    }

    /**
     * @param type
     * @param signKey
     * @param subject
     * @param validity
     * @return
     */
    protected Pair<String, String> buildJWT(TokenType type, Key signKey, String subject, Date validity) {
        String tokenId = UUID.randomUUID().toString();
        return new Pair<>(tokenId, Jwts.builder()
                // 该JWT的签发者
                .setIssuer(type.toString())
                // 该JWT所面向的用户:放入用户信息（用户名）
                .setSubject(subject)
                // 接收该JWT的一方
                // .setAudience("")
                // 放入权限信息
                .claim(Constant.Auth.TOKEN_ID_KEY, tokenId)
                // 签名
                .signWith(this.signKey(subject), SignatureAlgorithm.HS256)
                // 过期时间
                .setExpiration(validity)
                // 生效开始时间
                .setNotBefore(new Date())
                // 在什么时候签发的
                .setIssuedAt(new Date())
                .compact());
    }


    /**
     * 校验token
     *
     * @param authToken
     * @return
     */
    public Token authentication(String authToken) {
        Token token = new Token(authToken);
        try {
            Key key = this.signKey(token.getUsername());
            if (key != null) {
                token.parser(key);
            } else {
                token.setError("Token Sign Key is null");
            }
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            token.setError("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: ", e);
        } catch (ExpiredJwtException e) {
            token.setError("Expired JWT token.");
            log.trace("Expired JWT token trace: ", e);
        } catch (UnsupportedJwtException e) {
            token.setError("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: ", e);
        } catch (IllegalArgumentException e) {
            token.setError("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: ", e);
        }
        return token;
    }


}

package org.siu.myboot.auth.handler;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.model.JsonWebToken;
import org.siu.myboot.auth.model.Token;
import org.siu.myboot.auth.model.AuthUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * token 生成器
 * 1、生成Json Web Token
 * 2、校验token
 * 3、刷新token
 * <p>
 * TODO 定期更新 token secret
 *
 * @Author Siu
 * @Date 2020/3/4 15:01
 * @Version 0.0.1
 */
@Slf4j
public class TokenProvider implements InitializingBean {

    public static final String TOKEN_PROVIDER = "Akagi Token Provider";
    public static final String REFRESH_TOKEN_PROVIDER = "Akagi Refresh Token Provider";


    /**
     * 刷新token权限标识
     */
    private String refreshPermit;


    /**
     * base64-secret
     * 设置默认值，也可以从配置文件中配置
     */
    private String base64Secret;

    /**
     * 默认token失效时间
     * 设置默认值，也可以从配置文件中配置
     */
    private long tokenValidityInSeconds;

    /**
     * 记住密码时token失效时间
     * 设置默认值，也可以从配置文件中配置
     */
    private long tokenValidityInSecondsForRememberMe;


    public TokenProvider(String refreshPermit, String base64Secret, long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe) {
        this.refreshPermit = refreshPermit;
        this.base64Secret = base64Secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
        this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
    }

    /**
     * 签名key
     */
    private Key key;


    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    public JsonWebToken buildJWTObject(Authentication authentication, boolean rememberMe) {
        String token = this.buildJWT(authentication, rememberMe);
        long now = (new Date()).getTime();
        String refreshToken = this.buildJWT(authentication, new Date(now + 60 * 60 * 24 * 7), REFRESH_TOKEN_PROVIDER);
        return new JsonWebToken(token, refreshToken);
    }

    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    public String buildJWT(Authentication authentication, boolean rememberMe) {
        // 过期时间处理
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInSecondsForRememberMe * 1000);
        } else {
            validity = new Date(now + this.tokenValidityInSeconds * 1000);
        }

        // 构建token信息
        return buildJWT(authentication, validity, TOKEN_PROVIDER);
    }


    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param validity
     * @return
     */
    public String buildJWT(Authentication authentication, Date validity, String provider) {
        String authorities;
        if (REFRESH_TOKEN_PROVIDER.equals(provider)) {
            authorities = this.refreshPermit;
        } else {
            authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(Constant.Auth.AUTHORITIES_SPLIT));
        }


        long version = -1;
        // 获取用户的版本信息
        if (authentication.getPrincipal() instanceof AuthUser) {
            version = ((AuthUser) authentication.getPrincipal()).getTokenVersion();
        }

        // 构建token信息
        return buildJWT(authentication.getName(), authorities, validity, version, provider);
    }


    /**
     * @param subject
     * @param authorities
     * @param validity
     * @return
     */
    public String buildJWT(String subject, String authorities, Date validity, long version, String provider) {
        // 构建token信息
        return Jwts.builder()
                // 该JWT的签发者
                .setIssuer(provider)
                // 该JWT所面向的用户:放入用户信息（用户名）
                .setSubject(subject)
                // 接收该JWT的一方
                // .setAudience("")
                // 放入权限信息
                .claim(Constant.Auth.AUTHORITIES_KEY, authorities)
                // 用户信息版本
                .claim(Constant.Auth.VERSION_KEY, version)
                // 签名
                .signWith(key, SignatureAlgorithm.HS512)
                // 过期时间
                .setExpiration(validity)
                // 生效开始时间
                .setNotBefore(new Date())
                // 在什么时候签发的
                .setIssuedAt(new Date())
                .compact();
    }


    /**
     * 给快过期的token续期
     *
     * @param token 原token
     * @return
     */
    public String refresh(Token token) {
        // 默认策略：续期为原有效时间的十分之一
        if (token.isAuthorized()) {
            Claims claims = token.getClaimsJws().getBody();
            // 如果快失效了半小时
            if ((claims.getExpiration().getTime() - System.currentTimeMillis()) < Constant.Auth.REFRESH_TOKEN_TIME_THRESHOLD_MS) {
                if (claims.getExpiration() != null && claims.getNotBefore() != null) {
                    long renewTime = claims.getExpiration().getTime() +
                            Math.max(Constant.Auth.REFRESH_TOKEN_RENEW_TIME_MS, ((claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 10));
                    Date validity = new Date(renewTime);
                    log.info("用户[{}]的token快失效了-原失效时间[{}],自动续期到[{}]", token.getClaimsJws().getBody().getSubject(), claims.getExpiration(), validity);
                    return buildJWT(claims.getSubject(), claims.get(Constant.Auth.AUTHORITIES_KEY).toString(), validity, Long.parseLong(claims.get(Constant.Auth.VERSION_KEY).toString()), TOKEN_PROVIDER);
                }
            }
        }

        return "";
    }


    /**
     * 校验token
     *
     * @param authToken
     * @return
     */
    public Token validate(String authToken) {
        Token token = new Token(authToken);
        try {
            token.parser(key);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: ", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: ", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: ", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: ", e);
        }
        return token;
    }

}

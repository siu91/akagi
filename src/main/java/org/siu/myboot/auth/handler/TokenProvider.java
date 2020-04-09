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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
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
    public JsonWebToken buildJsonWebToken(Authentication authentication, boolean rememberMe) {
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
    private String buildJWT(Authentication authentication, boolean rememberMe) {
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
    private String buildJWT(Authentication authentication, Date validity, String provider) {
        String authorities;
        String originAuthorities = null;
        if (REFRESH_TOKEN_PROVIDER.equals(provider)) {
            authorities = this.refreshPermit;
            originAuthorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(Constant.Auth.AUTHORITIES_SPLIT));
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
        return buildJWT(authentication.getName(), authorities, originAuthorities, validity, version, provider);
    }


    /**
     * @param subject
     * @param authorities       权限标识
     * @param originAuthorities 源权限标识
     * @param validity
     * @return
     */
    private String buildJWT(String subject, String authorities, String originAuthorities, Date validity, long version, String provider) {
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
                .claim(Constant.Auth.ORIGIN_AUTHORITIES_KEY, originAuthorities == null ? "" : originAuthorities)
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
     * 刷新token
     *
     * @return
     */
    public JsonWebToken refresh() {
        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        assert requestAttributes != null;
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        assert request != null;
        String bearerToken = request.getHeader(Constant.Auth.AUTHORIZATION_HEADER);
        bearerToken = bearerToken.substring(Constant.Auth.TOKEN_PREFIX.length());

        Token token = validate(bearerToken);
        Claims claims = token.getClaimsJws().getBody();

        long now = (new Date()).getTime();
        Date validity1 = new Date(now + this.tokenValidityInSecondsForRememberMe * 1000);
        Date validity2 = new Date(now + 60 * 60 * 24 * 7);
        String newToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), null, validity1, Long.parseLong(claims.get(Constant.Auth.VERSION_KEY).toString()), TOKEN_PROVIDER);
        String newRefreshToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.AUTHORITIES_KEY).toString(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), validity2, Long.parseLong(claims.get(Constant.Auth.VERSION_KEY).toString()), REFRESH_TOKEN_PROVIDER);

        return new JsonWebToken(newToken, newRefreshToken);
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

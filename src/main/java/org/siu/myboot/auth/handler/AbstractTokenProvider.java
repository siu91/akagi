package org.siu.myboot.auth.handler;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.model.JsonWebToken;
import org.siu.myboot.auth.model.Token;
import org.siu.myboot.auth.model.AuthUser;
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
 * TODO 定期更新 token secret 会存在所有用户的都用一个公用值，secret设计成与用户属性相关的值
 * 1、假定 用户与token相关的属性（pass，version（用户信息修改时的版本））
 * 2、登录 时将保存用户的 token secret（保存在redis）
 * 3、校验token时获取 token secret
 * 4、修改密码/修改权限/注销等时 更新 token secret
 *
 * @Author Siu
 * @Date 2020/3/4 15:01
 * @Version 0.0.1
 */
@Slf4j
public abstract class AbstractTokenProvider implements TokenProvider {

    public static final String TOKEN_PROVIDER = "Akagi Token Provider";
    public static final String REFRESH_TOKEN_PROVIDER = "Akagi Refresh Token Provider";


    /**
     * 刷新token权限标识
     */
    protected String refreshPermit;

    /**
     * 默认token失效时间
     * 设置默认值，也可以从配置文件中配置
     */
    protected long tokenValidityInSeconds;

    /**
     * 记住密码时token失效时间
     * 设置默认值，也可以从配置文件中配置
     */
    protected long tokenValidityInSecondsForRememberMe;


    public AbstractTokenProvider(String refreshPermit, long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe) {
        this.refreshPermit = refreshPermit;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
        this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
    }


    /**
     *
     * @param base64
     * @return
     */
    protected Key toKey(String base64) {
        byte[] keyBytes = Decoders.BASE64.decode(base64);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    @Override
    public JsonWebToken buildJsonWebToken(Authentication authentication, boolean rememberMe) {
        String token = this.buildJWT(authentication, rememberMe);
        long now = (new Date()).getTime();
        String refreshToken = this.buildJWT(authentication, new Date(now + 60 * 60 * 24 * 7), REFRESH_TOKEN_PROVIDER);
        return new JsonWebToken(token, refreshToken);
    }

    @Override
    public JsonWebToken buildJsonWebToken(Authentication authentication) {
        return this.buildJsonWebToken(authentication, false);
    }

    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    protected String buildJWT(Authentication authentication, boolean rememberMe) {
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
    protected String buildJWT(Authentication authentication, Date validity, String provider) {
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
    protected String buildJWT(String subject, String authorities, String originAuthorities, Date validity, String provider) {
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
                // 签名
                .signWith(getKey(), SignatureAlgorithm.HS512)
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
    @Override
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
        String newToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), null, validity1, TOKEN_PROVIDER);
        String newRefreshToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.AUTHORITIES_KEY).toString(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), validity2, REFRESH_TOKEN_PROVIDER);

        return new JsonWebToken(newToken, newRefreshToken);
    }


    /**
     * 校验token
     *
     * @param authToken
     * @return
     */
    @Override
    public Token validate(String authToken) {
        Token token = new Token(authToken);
        try {
            token.parser(getKey());
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

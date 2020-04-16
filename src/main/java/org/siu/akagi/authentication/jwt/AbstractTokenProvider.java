package org.siu.akagi.authentication.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.context.AkagiSecurityContextHolder;
import org.siu.akagi.model.User;
import org.siu.akagi.constant.Constant;
import org.siu.akagi.model.JWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
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

    public static final String TOKEN_PROVIDER = "Akagi Token Provider";
    public static final String REFRESH_TOKEN_PROVIDER = "Akagi Refresh Token Provider";


    /**
     * 刷新token权限标识
     */
    protected String refreshPermit;

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


    public AbstractTokenProvider(String refreshPermit, long expire, long expire4Remember) {
        this.refreshPermit = refreshPermit;
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
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    @Override
    public JWT create(Authentication authentication, boolean rememberMe) {
        String token = this.buildJWT(authentication, rememberMe);
        long now = (new Date()).getTime();
        String refreshToken = this.buildJWT(authentication, new Date(now + Constant.Auth.DEFAULT_REFRESH_TOKEN_EXPIRE_MS), REFRESH_TOKEN_PROVIDER);
        String user = ((User) authentication.getPrincipal()).getUsername();
        return new JWT(user, token, refreshToken);
    }

    @Override
    public JWT create(Authentication authentication) {
        return this.create(authentication, false);
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
                .signWith(this.get(subject), SignatureAlgorithm.HS512)
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
    public JWT refresh() {
        Optional<User> authUser = AkagiSecurityContextHolder.getCurrentUser();
        if (authUser.isPresent()) {
            this.store();
            Claims claims = authUser.get().getClaimsJws().getBody();
            long now = (new Date()).getTime();
            Date validity1 = new Date(now + this.expire4Remember * 1000);
            Date validity2 = new Date(now + Constant.Auth.DEFAULT_REFRESH_TOKEN_EXPIRE_MS);
            String newToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), null, validity1, TOKEN_PROVIDER);
            String newRefreshToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.AUTHORITIES_KEY).toString(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), validity2, REFRESH_TOKEN_PROVIDER);

            return new JWT(claims.getSubject(), newToken, newRefreshToken);
        }

        return null;
    }



    /**
     * 校验token
     *
     * @param authToken
     * @return
     */
    @Override
    public Token authentication(String authToken) {
        Token token = new Token(authToken);
        try {
            Key key = this.get(token.getUsername());
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

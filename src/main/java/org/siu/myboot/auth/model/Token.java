package org.siu.myboot.auth.model;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
import org.siu.myboot.auth.constant.Constant;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * token 对象
 *
 * @Author Siu
 * @Date 2020/3/5 21:12
 * @Version 0.0.1
 */
@Getter
public class Token {

    private String provider;
    /**
     * 是否合法
     */
    private boolean authorized;

    /**
     * 错误信息
     */
    @Setter
    private String error;

    /**
     * token 字符串
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 解析后的token
     */
    private Jws<Claims> claimsJws;

    /**
     * 权限标识
     */
    private Authentication authenticationToken;


    public Token(String token) {
        this.token = token;
    }

    /**
     * 解析
     *
     * @param key
     */
    public void parser(Key key) {
        if (StringUtils.hasText(this.token)) {
            this.claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(this.token);
            this.provider = this.claimsJws.getBody().getIssuer();
            this.username = this.claimsJws.getBody().getSubject();
            this.authorized = true;
        }
    }


    /**
     * 获取token中的权限标识
     *
     * @return
     */
    public Authentication authentication() {
        if (this.authenticationToken == null) {
            Assert.notNull(this.claimsJws, "必须先解析token");
            Claims claims = this.claimsJws.getBody();
            this.authenticationToken = getAuthentication(claims, token);
        }
        return this.authenticationToken;
    }

    /**
     * 获取token中的权限标识
     *
     * @param claims
     * @param token
     * @return
     */
    private Authentication getAuthentication(Claims claims, String token) {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(Constant.Auth.AUTHORITIES_KEY).toString().split(Constant.Auth.AUTHORITIES_SPLIT))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        AuthUser principal = new AuthUser(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}

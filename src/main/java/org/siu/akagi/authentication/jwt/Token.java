package org.siu.akagi.authentication.jwt;

import com.google.common.base.Joiner;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;
import org.siu.akagi.constant.Constant;
import org.siu.akagi.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
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

    protected TokenType type;
    /**
     * 是否合法
     */
    protected boolean authorized;

    /**
     * 错误信息
     */
    @Setter
    protected String error;

    /**
     * token 字符串
     */
    protected String token;

    /**
     * 用户名
     */
    protected String username;

    /**
     * 解析后的token
     */
    protected Jws<Claims> claimsJws;

    /**
     * 认证信息
     */
    @Setter
    protected Authentication authentication;


    public Token(String token) {
        if (StringUtils.hasText(token)) {
            String[] ts = token.split(Constant.Auth.TOKEN_SPLIT_REGEX);
            if (ts.length == Constant.Auth.TOKEN_SPLIT_LENGTH) {
                try {
                    this.username = new String(Base64.getDecoder().decode(ts[0]));
                    this.token = Joiner.on(Constant.Auth.TOKEN_SPLIT).skipNulls().join(ts[1], ts[2], ts[3]);
                } catch (IllegalArgumentException e) {
                    this.error = "Token has been tampered";
                }
            } else {
                this.error = "Token has been tampered";
            }
        } else {
            this.error = "Token is null";
        }

    }

    /**
     * 解析
     *
     * @param key
     */
    public void parser(Key key) {
        this.claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(this.token);
        this.type = TokenType.COMMON.toString().equals(this.claimsJws.getBody().getIssuer()) ? TokenType.COMMON : TokenType.REFRESH;
        this.username = this.claimsJws.getBody().getSubject();
        if (this.claimsJws.getBody().getSubject().equals(this.username)) {
            this.authorized = true;
        } else {
            this.error = "Token has been tampered";
        }

    }


    /**
     * 获取token中的认证信息
     *
     * @return
     */
    public Authentication toAuthentication() {
        if (this.authentication == null) {
            Assert.notNull(this.claimsJws, "必须先解析token");
            Claims claims = this.claimsJws.getBody();

            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(Constant.Auth.AUTHORITIES_KEY).toString().split(Constant.Auth.AUTHORITIES_SPLIT))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            User principal = new User(claims.getSubject(), "", authorities);
            principal.setClaimsJws(this.claimsJws);

            this.authentication = new UsernamePasswordAuthenticationToken(principal, token, authorities);
        }
        return this.authentication;
    }



}

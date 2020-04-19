package org.siu.akagi.authentication.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.siu.akagi.constant.Constant;
import org.siu.akagi.context.AkagiSecurityContextHolder;
import org.siu.akagi.model.JWT;
import org.siu.akagi.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 默认Token Sign 服务
 *
 * @Author Siu
 * @Date 2020/2/21 16:13
 * @Version 0.0.1
 */
public class DefaultTokenProvider extends AbstractTokenProvider {

    private Key key;

    public DefaultTokenProvider(long tokenValidityInSeconds, long tokenValidityInSecondsForRememberMe, String secret) {
        super(tokenValidityInSeconds, tokenValidityInSecondsForRememberMe);
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * 生成 JSON Web Token
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    @Override
    public JWT createToken(Authentication authentication, boolean rememberMe) {
        String token = this.buildJWT(authentication, rememberMe);
        long now = (new Date()).getTime();
        String refreshToken = this.buildJWT(authentication, new Date(now + Constant.Auth.DEFAULT_REFRESH_TOKEN_EXPIRE_MS), TokenType.REFRESH);
        String user = ((User) authentication.getPrincipal()).getUsername();

        return new JWT(user, token, refreshToken);
    }


    /**
     * 刷新token
     *
     * @return
     */
    @Override
    public JWT refreshToken() {
        Optional<User> authUser = AkagiSecurityContextHolder.getCurrentUser();
        if (authUser.isPresent()) {
            Claims claims = authUser.get().getClaimsJws().getBody();
            long now = (new Date()).getTime();
            Date validity1 = new Date(now + this.expire4Remember * 1000);
            Date validity2 = new Date(now + Constant.Auth.DEFAULT_REFRESH_TOKEN_EXPIRE_MS);
            String newToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), null, validity1, TokenType.COMMON);
            String newRefreshToken = buildJWT(claims.getSubject(), claims.get(Constant.Auth.AUTHORITIES_KEY).toString(), claims.get(Constant.Auth.ORIGIN_AUTHORITIES_KEY).toString(), validity2, TokenType.REFRESH);

            return new JWT(claims.getSubject(), newToken, newRefreshToken);
        }

        return null;
    }

    @Override
    public Key signKey(String user) {
        return this.key;
    }


    @Override
    public Token parseToken(String jwt) {
        Token token = super.authentication(jwt);

        Claims claims = token.getClaimsJws().getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(Constant.Auth.AUTHORITIES_KEY).toString().split(Constant.Auth.AUTHORITIES_SPLIT))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        principal.setClaimsJws(token.getClaimsJws());

        token.setAuthentication(new UsernamePasswordAuthenticationToken(principal, token, authorities));

        return token;
    }

    @Override
    public void removeSignKey() {
        // do nothing
    }



}

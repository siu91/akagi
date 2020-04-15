package org.siu.akagi.model;

import com.google.common.base.Joiner;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.io.Encoders;
import lombok.Getter;
import lombok.Setter;
import org.siu.akagi.constant.Constant;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证的用户
 *
 * @Author Siu
 * @Date 2020/3/7 14:18
 * @Version 0.0.1
 */
public class User extends org.springframework.security.core.userdetails.User {


    /**
     * 用户属性
     */
    private List<Object> v = new LinkedList<>();

    /**
     * 解析后的token
     */
    @Setter
    @Getter
    private Jws<Claims> claimsJws;


    public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.v.add(UUID.randomUUID().toString());
        this.v.add(Constant.Auth.JSON_WEB_TOKEN_BASE64_SECRET);
        this.v.addAll(this.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().collect(Collectors.toList()));
        this.v.add(username);
    }


    /**
     * 转成base64
     *
     * @return
     */
    public String toBase64() {
        String base64Str = Joiner.on(Constant.Auth.BASE64_SECRET_SPLIT).skipNulls().join(this.v);
        return Encoders.BASE64.encode(base64Str.getBytes());
    }
}

package org.siu.myboot.auth.model;

import com.google.common.base.Joiner;
import io.jsonwebtoken.io.Encoders;
import org.siu.myboot.auth.constant.Constant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证的用户
 *
 * @Author Siu
 * @Date 2020/3/7 14:18
 * @Version 0.0.1
 */
public class AuthUser extends User {


    /**
     * 用户属性
     */
    private List<Object> v = new LinkedList<>();


    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Object... v) {
        super(username, password, authorities);
        this.v.add(Constant.Auth.JSON_WEB_TOKEN_BASE64_SECRET);
        this.v.addAll(this.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().collect(Collectors.toList()));
        this.v.add(username);
        if (v != null) {
            this.v.addAll(Arrays.asList(v));
        }
    }

    /**
     * 添加用户属性
     *
     * @param v
     */
    public void v(Object v) {
        this.v.add(v);

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

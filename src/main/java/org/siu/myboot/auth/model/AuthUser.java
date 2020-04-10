package org.siu.myboot.auth.model;

import com.google.common.base.Joiner;
import io.jsonwebtoken.io.Encoders;
import lombok.Setter;
import org.siu.myboot.auth.constant.Constant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

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
    @Setter
    private Object[] v;


    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Object... v) {
        super(username, password, authorities);
        this.v = v;
    }


    public Object[] getV() {
        return v;
    }

    /**
     * 转成base64
     *
     * @return
     */
    public String toBase64() {
        List<String> properties = new LinkedList<>();
        this.getAuthorities().forEach(a -> {
            properties.add(a.getAuthority());
        });
        Collections.sort(properties);
        properties.add(getUsername());
        for (Object o : v) {
            properties.add(o.toString());
        }
        properties.add(Constant.Auth.JSON_WEB_TOKEN_BASE64_SECRET);


        String base64Str = Joiner.on(Constant.Auth.BASE64_SECRET_SPLIT).skipNulls().join(properties);

        return Encoders.BASE64.encode(base64Str.getBytes());
    }
}

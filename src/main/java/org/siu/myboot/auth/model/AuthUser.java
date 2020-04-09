package org.siu.myboot.auth.model;

import com.google.common.base.Joiner;
import io.jsonwebtoken.io.Encoders;
import org.siu.myboot.auth.constant.Constant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 认证的用户
 *
 * @Author Siu
 * @Date 2020/3/7 14:18
 * @Version 0.0.1
 */
public class AuthUser extends User {


    /**
     * 版本
     */
    private Object version;


    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Object version) {
        super(username, password, authorities);
        this.version = version;
    }


    public Object getVersion() {
        return version;
    }

    /**
     * 转成base64
     *
     * @return
     */
    public String toBase64() {
        List<String> properties = new ArrayList<>();
        this.getAuthorities().forEach(a -> {
            properties.add(a.getAuthority());
        });
        Collections.sort(properties);

        properties.add(getUsername());
        properties.add(getPassword());
        properties.add(this.version.toString());

        String base64Str = Joiner.on(Constant.Auth.BASE64_SECRET_SPLIT).skipNulls().join(properties);

        return Encoders.BASE64.encode(base64Str.getBytes());
    }
}

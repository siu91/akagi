package org.siu.myboot.auth.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

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

}

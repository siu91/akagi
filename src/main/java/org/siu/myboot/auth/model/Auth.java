package org.siu.myboot.auth.model;

import lombok.Data;

import java.util.List;

/**
 * 认证&授权实体对象
 *
 * @Author Siu
 * @Date 2020/4/3 22:06
 * @Version 0.0.1
 */
@Data
public class Auth {

    private LoginUser user;
    private List<UserAuthorities> authorities;

}

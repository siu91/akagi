package org.siu.akagi.model;

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

    /**
     * 用户
     */
    private LoginUser user;

    /**
     * 权限
     */
    private List<Authorities> authorities;

}

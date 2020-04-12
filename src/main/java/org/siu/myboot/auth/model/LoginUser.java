package org.siu.myboot.auth.model;

import lombok.Data;

/**
 * 登录用户信息
 *
 * @Author Siu
 * @Date 2020/4/3 22:00
 * @Version 0.0.1
 */
@Data
public class LoginUser {

    /**
     * 用户的标识（必须唯一）
     * <p>
     * 如：用户ID、手机、用户名
     */
    private String id;

    /**
     * 密码
     */
    private String pass;

    /**
     * 标识用户认证授权相关的属性或动作发生时，变更的状态/版本
     * <p>
     * 如用户属性-密码修改，v值改变
     * 如用户注销，v值改变
     * 通常可以是一个自增版本号/唯一ID
     */
    private Object v;
}

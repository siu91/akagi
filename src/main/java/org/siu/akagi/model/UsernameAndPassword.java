package org.siu.akagi.model;

import lombok.Data;

/**
 * 登录用户信息
 *
 * @Author Siu
 * @Date 2020/4/3 22:00
 * @Version 0.0.1
 */
@Data
public class UsernameAndPassword {

    /**
     * 用户的标识（必须唯一）
     * <p>
     * 如：用户ID、手机、用户名
     */
    private String name;

    /**
     * 密码
     */
    private String pass;

}

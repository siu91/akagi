package org.siu.myboot.auth.model;

import lombok.Data;

/**
 * @Author Siu
 * @Date 2020/4/3 22:00
 * @Version 0.0.1
 */
@Data
public class LoginUser {

    private String id;
    private String pass;
    private long tokenVersion;
}

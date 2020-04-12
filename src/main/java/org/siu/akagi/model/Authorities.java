package org.siu.akagi.model;

import lombok.Data;

/**
 * 用户的角色、权限信息
 *
 * @Author Siu
 * @Date 2020/3/8 14:08
 * @Version 0.0.1
 */
@Data
public class Authorities {

    /**
     * 角色标识
     */
    private String role;

    /**
     * 权限标识
     */
    private String permit;

}

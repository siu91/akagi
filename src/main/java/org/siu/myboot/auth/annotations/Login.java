package org.siu.myboot.auth.annotations;

import java.lang.annotation.*;


/**
 * 登录
 * <p>
 * 保存 token secret key
 *
 * @Author Siu
 * @Date 2020/3/19 21:07
 * @Version 0.0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Login {
}

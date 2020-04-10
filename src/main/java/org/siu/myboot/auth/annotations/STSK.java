package org.siu.myboot.auth.annotations;

import java.lang.annotation.*;


/**
 * token secret key 更新
 *
 * @Author Siu
 * @Date 2020/3/19 21:07
 * @Version 0.0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface STSK {
}

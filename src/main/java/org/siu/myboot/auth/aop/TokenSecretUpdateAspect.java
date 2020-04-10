package org.siu.myboot.auth.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.siu.myboot.auth.handler.TokenProvider;

import javax.annotation.Resource;


/**
 * @author siu
 * @version v0.1 2019/10/23
 * @see
 * @since JDK1.8
 */
@Slf4j
@Aspect
public class TokenSecretUpdateAspect {

    @Resource
    private TokenProvider TokenProvider;

    /**
     * 定义切入点:匹配带Log注解的方法
     */
    @Pointcut("@annotation(org.siu.myboot.auth.annotations.TokenSecretUpdate)")
    public void annotation() {}


    @Around("annotation()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        TokenProvider.setSecret();
        return point.proceed();
    }


}

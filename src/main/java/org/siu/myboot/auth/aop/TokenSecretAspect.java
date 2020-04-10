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
public class TokenSecretAspect {

    @Resource
    private TokenProvider TokenProvider;

    /**
     * 更新/注销/黑名单
     */
    @Pointcut("@annotation(org.siu.myboot.auth.annotations.UTSK) || @annotation(org.siu.myboot.auth.annotations.Logout) || @annotation(org.siu.myboot.auth.annotations.Black)")
    public void utsk() {
    }


    /**
     * 更新/注销/黑名单
     */
    @Pointcut("@annotation(org.siu.myboot.auth.annotations.STSK) || @annotation(org.siu.myboot.auth.annotations.Login)")
    public void stsk() {
    }


    @Around("utsk()")
    public Object removeTokenSecretKey(ProceedingJoinPoint point) throws Throwable {
        TokenProvider.removeKey();
        return point.proceed();
    }


    @Around("stsk()")
    public Object setTokenSecretKey(ProceedingJoinPoint point) throws Throwable {
        TokenProvider.setKey();
        return point.proceed();
    }


}

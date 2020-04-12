package org.siu.myboot.auth.aop;


import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.siu.myboot.auth.handler.jwt.TokenProvider;


/**
 * 处理更新token
 *
 * @Author Siu
 * @Date 2020/3/20 15:30
 * @Version 0.0.1
 */
@Slf4j
public class UTSKAnnotationInterceptor implements MethodInterceptor {

    private TokenProvider tokenProvider;

    public UTSKAnnotationInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        tokenProvider.removeKey();
        return invocation.proceed();
    }

}

package org.siu.myboot.auth.aop;

import lombok.NonNull;
import org.siu.myboot.auth.annotations.Black;
import org.siu.myboot.auth.annotations.Logout;
import org.siu.myboot.auth.annotations.UTSK;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * AOP 处理更新token secret key
 *
 * @Author Siu
 * @Date 2020/3/20 16:02
 * @Version 0.0.1
 */
public class UTSKAnnotationAdvisor extends AbstractAkagiPointcutAdvisor {


    public UTSKAnnotationAdvisor(@NonNull UTSKAnnotationInterceptor interceptor) {
        super(interceptor);
        this.advice = interceptor;
    }


    @Override
    public Pointcut buildPointcut() {
        // 设置切点
        Pointcut classLevelPointCut = new AnnotationMatchingPointcut(UTSK.class, true);
        Pointcut methodLevelPointCut = AnnotationMatchingPointcut.forMethodAnnotation(UTSK.class);
        Pointcut p0 = AnnotationMatchingPointcut.forMethodAnnotation(Black.class);
        Pointcut p1 = AnnotationMatchingPointcut.forMethodAnnotation(Logout.class);

        return new ComposablePointcut(classLevelPointCut).union(methodLevelPointCut).union(p0).union(p1);
    }
}

package org.siu.myboot.auth.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


/**
 * AOP 处理DataSource注解
 *
 * @Author Siu
 * @Date 2020/3/20 16:02
 * @Version 0.0.1
 */
public abstract class AbstractAkagiPointcutAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {


    protected Advice advice;

    protected Pointcut pointcut;

    public AbstractAkagiPointcutAdvisor(MethodInterceptor interceptor) {
        this.advice = interceptor;
        // 设置切点
        this.pointcut = this.buildPointcut();

    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    /**
     * 实现BeanFactoryAware的bean中获取beanFactory
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    /**
     * 构建切面
     *
     * @return
     */
    public abstract Pointcut buildPointcut();

}

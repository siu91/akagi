package org.siu.akagi.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

/**
 * @Author Siu
 * @Date 2020/4/14 15:22
 * @Version 0.0.1
 */
public class InheritableThreadLocalAkagiContextHolderStrategy implements SecurityContextHolderStrategy {

    private static final ThreadLocal<AkagiContextImpl> contextHolder = new InheritableThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public AkagiContextImpl getContext() {
        AkagiContextImpl context = contextHolder.get();
        if (context != null) {
            return context;
        }
        return createEmptyContext();
    }

    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set((AkagiContextImpl) context);
    }


    @Override
    public AkagiContextImpl createEmptyContext() {
        return new AkagiContextImpl();
    }
}

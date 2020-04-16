package org.siu.akagi.context;

import org.siu.akagi.autoconfigure.AkagiProperties;
import org.siu.akagi.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;


/**
 * SecurityContextHolder 增强
 *
 * @Author Siu
 * @Date 2020/4/14 14:08
 * @Version 0.0.1
 */
public class AkagiSecurityContextHolder extends SecurityContextHolder {

    private static AkagiProperties akagiGlobalProperties;

    public static final String DEFAULT_STRATEGY = "org.siu.akagi.context.InheritableThreadLocalAkagiContextHolderStrategy";


    public static void init(AkagiProperties properties) {
        AkagiSecurityContextHolder.akagiGlobalProperties = properties;
        SecurityContextHolder.setStrategyName(DEFAULT_STRATEGY);
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Optional<Set<String>> getAuthorities() {
        if (SecurityContextHolder.getContext() instanceof AkagiContextImpl) {
            return ((AkagiContextImpl) SecurityContextHolder.getContext()).getAuthorities();
        }
        return null;
    }

    public static Optional<String> getCurrentUserName() {
        if (SecurityContextHolder.getContext() instanceof AkagiContextImpl) {
            return ((AkagiContextImpl) SecurityContextHolder.getContext()).getCurrentUserName();
        }
        return Optional.empty();
    }

    public static Optional<User> getCurrentUser() {
        if (SecurityContextHolder.getContext() instanceof AkagiContextImpl) {
            return ((AkagiContextImpl) SecurityContextHolder.getContext()).getCurrentUser();
        }
        return Optional.empty();
    }

    public static String getCurrentError() {
        if (SecurityContextHolder.getContext() instanceof AkagiContextImpl) {
            return ((AkagiContextImpl) SecurityContextHolder.getContext()).getError();
        }
        return null;
    }

    public static void setCurrentError(String error) {
        if (SecurityContextHolder.getContext() instanceof AkagiContextImpl) {
            ((AkagiContextImpl) SecurityContextHolder.getContext()).setError(error);
        }
    }


    public static AkagiProperties getAkagiGlobalProperties() {
        return akagiGlobalProperties;
    }


    public static void main(String[] args) {
    }
}

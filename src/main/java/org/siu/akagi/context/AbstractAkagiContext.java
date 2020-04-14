package org.siu.akagi.context;

import org.siu.akagi.model.User;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;
import java.util.Set;

/**
 * @Author Siu
 * @Date 2020/4/14 14:07
 * @Version 0.0.1
 */
public abstract class AbstractAkagiContext implements SecurityContext {


    abstract Set<String> getAuthorities();

    abstract Optional<String> getCurrentUserName();

    abstract Optional<User> getCurrentUser();



}

package org.siu.akagi.context;

import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security Context 增强
 *
 * @Author Siu
 * @Date 2020/4/14 14:47
 * @Version 0.0.1
 */
@Slf4j
public class AkagiContextImpl implements SecurityContext {

    private Authentication authentication;

    private String currentUserName;

    private User currentUser;

    private Set<String> authorities;

    private String error;

    public AkagiContextImpl() {
    }

    @Override
    public Authentication getAuthentication() {
        return this.authentication;
    }

    public Optional<Set<String>> getAuthorities() {
        return Optional.ofNullable(this.authorities);
    }

    public Optional<String> getCurrentUserName() {
        return Optional.ofNullable(this.currentUserName);
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(this.currentUser);
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        if (authentication == null) {
            log.debug("no authentication in security context found");
        }
        this.authentication = authentication;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                this.currentUserName = user.getUsername();
                this.currentUser = user;
            }
            this.authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        }

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

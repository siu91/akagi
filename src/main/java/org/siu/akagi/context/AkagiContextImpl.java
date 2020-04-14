package org.siu.akagi.context;

import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Siu
 * @Date 2020/4/14 14:47
 * @Version 0.0.1
 */
@Slf4j
public class AkagiContextImpl extends AbstractAkagiContext {

    private Authentication authentication;

    private String currentUserName;

    private User currentUser;

    private Set<String> authorities;

    public AkagiContextImpl() {
    }

    @Override
    public Authentication getAuthentication() {
        return this.authentication;
    }

    @Override
    public Set<String> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Optional<String> getCurrentUserName() {
        return Optional.ofNullable(this.currentUserName);
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(this.currentUser);
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
        if (authentication == null) {
            log.debug("no authentication in security context found");
        }

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                this.currentUserName = user.getUsername();
                this.currentUser = user;
            }
            this.authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        }

    }
}

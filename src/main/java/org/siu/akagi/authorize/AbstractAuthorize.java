package org.siu.akagi.authorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Siu
 * @Date 2020/4/13 10:50
 * @Version 0.0.1
 */
public abstract class AbstractAuthorize implements Authorize {


    /**
     * 获取当前用户的权限列表
     *
     * @return
     */
    protected Set<String> getAuthoritySet() {
        // 获取用户权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new HashSet<>();
        }
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }


    /**
     * // 校验权限，可以在此自定义
     * Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
     * return authorities.stream()
     * .map(GrantedAuthority::getAuthority)
     * .filter(StringUtils::hasText)
     * .anyMatch(x -> PatternMatchUtils.simpleMatch(perm, x));
     *
     * @param any
     * @return
     */
    @Override
    public boolean hasAny(String... any) {
        Assert.isTrue(any != null && any.length > 0, "The verification authority input cannot be empty");
        Set<String> authorities = getAuthoritySet();

        for (String p : any) {
            if (authorities.contains(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean has() {
        return true;
    }
}

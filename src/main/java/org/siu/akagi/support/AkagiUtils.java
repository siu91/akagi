package org.siu.akagi.support;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * @Author Siu
 * @Date 2020/3/4 16:01
 * @Version 0.0.1
 */
@Slf4j
@UtilityClass
public class AkagiUtils {

    /**
     * 获取当前用户的登录ID（用户名、手机等）
     *
     * @return
     */
    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        }

        log.debug("found username '{}' in security context", username);

        return Optional.ofNullable(username);
    }


    /**
     * 获取当前用户的登录信息
     *
     * @return
     */
    public static Optional<User> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        User user = null;

        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        }


        return Optional.ofNullable(user);
    }
}


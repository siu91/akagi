package org.siu.akagi.authentication;

import lombok.SneakyThrows;
import org.siu.akagi.context.AkagiSecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DefaultAccessDeniedHandler 用来解决【认证过】的用户访问无权限资源时的异常
 *
 * @Author Siu
 * @Date 2020/3/4 16:09
 * @Version 0.0.1
 */
public class DefaultAccessDeniedHandler implements AccessDeniedHandler {

    @SneakyThrows
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase() + ":" + AkagiSecurityContextHolder.getCurrentError());
    }
}


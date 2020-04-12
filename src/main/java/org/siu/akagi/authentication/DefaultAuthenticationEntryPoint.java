package org.siu.akagi.authentication;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义认证入口点
 * <p>
 * AuthenticationEntryPoint 用来解决【匿名用户】访问无权限资源时的异常
 * <p>
 * 如：  response.sendRedirect("/login");
 * 前后端分离，可以让前端处理即可
 *
 * @Author Siu
 * @Date 2020/3/4 16:04
 * @Version 0.0.1
 */
@Slf4j
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @SneakyThrows
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {
        if (log.isDebugEnabled()) {
            log.debug("Pre-authenticated entry point called. Rejecting access");
        }

        response.sendError(403, authException.getMessage());

    }
}


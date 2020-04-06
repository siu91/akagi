package org.siu.myboot.auth.handler;


import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.model.Token;
import org.siu.myboot.auth.service.TokenStateful;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * token 认证过滤器
 *
 * @Author Siu
 * @Date 2020/3/4 15:15
 * @Version 0.0.1
 */
@Slf4j
public class TokenAuthenticationFilter extends GenericFilterBean {


    private TokenProvider tokenProvider;
    @Resource
    private TokenStateful tokenStateful;

    private List<AntPathRequestMatcher> matchers;

    public TokenAuthenticationFilter(TokenProvider tokenProvider, Set<String> ignorePathPattern) {
        this.tokenProvider = tokenProvider;
        this.matchers = buildAntPathRequestMatcher(ignorePathPattern);
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String jwt = getToken(httpServletRequest);

        if (!ignoreCheckTokenWhenUriAntMatch(httpServletRequest)) {
            // 验证token
            Token token = tokenProvider.validate(jwt);

            // 验证token版本
            Long oToken = tokenStateful == null ? null : tokenStateful.getTokenVersion(token.getUsername());
            if (oToken != null) {
                long currentAuthVersion = Long.parseLong(oToken.toString());
                if (currentAuthVersion == 0) {
                    log.warn("用户已退出登录，请重新登录");
                    httpServletResponse.sendError(601, "用户已退出登录，请重新登录");
                    return;
                }
                // token中存在auth 版本，但版本过期了
                if (token.getAuthVersion() >= 0 && token.getAuthVersion() < currentAuthVersion) {
                    log.warn("用户认证信息版本过期，请重新登录");
                    httpServletResponse.sendError(602, "用户认证信息版本过期，请重新登录");
                    return;
                }
            }

            if (token.isAuthorized()) {
                // 校验通过，如果token接近过期，可以在这里重新根据业务情况颁发新的token给客户端
                refreshToken(httpServletResponse, token);
                // token 验证通过
                // 1、提取token中携带的权限标识
                // 2、把token中携带的用户权限放入SecurityContextHolder交由  Spring Security管理
                Authentication authentication = token.authentication();
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("set Authentication to security context for '{}', uri: {}", authentication.getName(), httpServletRequest.getRequestURI());
                log.info("Authenticated user access:[{}]-[{}]", token.getClaimsJws().getBody().getSubject(), httpServletRequest.getRequestURI());
            } else {
                log.debug("no valid JWT token found, uri: {}", httpServletRequest.getRequestURI());
            }
        }

        filterChain.doFilter(servletRequest, httpServletResponse);
    }

    /**
     * 创建路径匹配器
     *
     * @param ignorePathPattern
     * @return
     */
    private List<AntPathRequestMatcher> buildAntPathRequestMatcher(Set<String> ignorePathPattern) {
        List<AntPathRequestMatcher> matchers = new ArrayList<>();
        ignorePathPattern.forEach(v -> {
            matchers.add(new AntPathRequestMatcher(v));
        });

        return matchers;

    }

    /**
     * 忽略token校验，当uri匹配时
     *
     * @param httpServletRequest
     * @return
     */
    private boolean ignoreCheckTokenWhenUriAntMatch(HttpServletRequest httpServletRequest) {
        for (AntPathRequestMatcher matcher : this.matchers) {
            if (matcher.matches(httpServletRequest)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 刷新token
     *
     * @param httpServletResponse
     * @param token
     */
    private void refreshToken(HttpServletResponse httpServletResponse, Token token) {
        String newJWT = tokenProvider.refresh(token);
        if (StringUtils.hasText(newJWT)) {
            httpServletResponse.setHeader(Constant.Auth.AUTHORIZATION_HEADER, Constant.Auth.TOKEN_PREFIX + newJWT);
        }

    }

    /**
     * 从请求头中获取token
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constant.Auth.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constant.Auth.TOKEN_PREFIX)) {
            return bearerToken.substring(Constant.Auth.TOKEN_PREFIX.length());
        }
        return null;
    }


}



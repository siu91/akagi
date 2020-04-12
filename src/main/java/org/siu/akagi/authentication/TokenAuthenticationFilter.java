package org.siu.akagi.authentication;


import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.constant.Constant;
import org.siu.akagi.authentication.jwt.TokenProvider;
import org.siu.akagi.authentication.jwt.Token;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private List<AntPathRequestMatcher> matchers;

    public TokenAuthenticationFilter(TokenProvider tokenProvider, Set<String> ignorePathPattern) {
        this.tokenProvider = tokenProvider;
        this.matchers = buildAntPathRequestMatcher(ignorePathPattern);
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (!ignoreCheckTokenWhenUriAntMatch(httpServletRequest)) {
            // 验证token
            String jwt = getToken(httpServletRequest);
            Token token = tokenProvider.authentication(jwt);

            if (token.isAuthorized()) {
                // token 验证通过
                // 1、提取token中携带的权限标识
                // 2、把token中携带的用户权限放入SecurityContextHolder交由  Spring Security管理
                Authentication authentication = token.toAuthentication();
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("set Authentication to security context for '{}', uri: {}", authentication.getName(), httpServletRequest.getRequestURI());
                log.info("Authenticated user access:[{}]-[{}]", token.getClaimsJws().getBody().getSubject(), httpServletRequest.getRequestURI());
            } else {
                log.error("Authentication Fail:{}, uri: {}", token.getError(), httpServletRequest.getRequestURI());
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
        return ignorePathPattern.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());

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



package org.siu.akagi;

import org.siu.akagi.autoconfigure.AkagiProperties;
import org.siu.akagi.authentication.DefaultAccessDeniedHandler;
import org.siu.akagi.authentication.DefaultAuthenticationEntryPoint;
import org.siu.akagi.authentication.jwt.TokenProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * WebSecurity 配置
 *
 * @Author Siu
 * @Date 2020/3/4 16:13
 * @Version 0.0.1
 */
public class AkagiWebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * token工具
     */
    private final TokenProvider tokenProvider;


    /**
     * 认证入口点
     */
    private final DefaultAuthenticationEntryPoint authenticationErrorHandler;

    /**
     * 认证用户无权限访问的处理
     */
    private final DefaultAccessDeniedHandler defaultAccessDeniedHandler;

    /**
     * 全局配置
     */
    private final AkagiProperties akagiProperties;

    public AkagiWebSecurityConfig(TokenProvider tokenProvider, DefaultAuthenticationEntryPoint authenticationErrorHandler, DefaultAccessDeniedHandler defaultAccessDeniedHandler, AkagiProperties akagiProperties) {
        this.tokenProvider = tokenProvider;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.defaultAccessDeniedHandler = defaultAccessDeniedHandler;
        this.akagiProperties = akagiProperties;
    }


    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")

                // allow anonymous resource requests
                .antMatchers(
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/h2-console/**",
                        "/swagger-ui.html/**",
                        "/swagger-resources/**"
                );
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        final int permitSize = akagiProperties.getPermitAllUri().size();
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                // .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                // 设置认证入口点
                .authenticationEntryPoint(authenticationErrorHandler)
                // 设置认证用户无权限访问的处理
                .accessDeniedHandler(defaultAccessDeniedHandler)

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // create no session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
             /*   .expressionHandler(new DefaultWebSecurityExpressionHandler() {
                    @Override
                    protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, FilterInvocation fi) {
                        WebSecurityExpressionRoot root = (WebSecurityExpressionRoot) super.createSecurityExpressionRoot(authentication, fi);
                        root.setDefaultRolePrefix(""); //remove the prefix ROLE_
                        root.setPermissionEvaluator(new AkagiPermissionEvaluator());
                        return root;
                    }
                })*/
                .antMatchers(akagiProperties.getPermitAllUri().toArray(new String[permitSize])).permitAll()
                // 要求所有进入应用的HTTP请求都要进行认证
                .anyRequest().authenticated()
                .and()
                .apply(securityConfigurerAdapter());

    }


    /**
     * 设置 SecurityConfigurerAdapter
     *
     * @return
     */
    private AkagiSecurityConfigurerAdapter securityConfigurerAdapter() {
        return new AkagiSecurityConfigurerAdapter(tokenProvider, akagiProperties.getPermitAllUri());
    }
}

/**
 *
 * 认证模块
 *
 * 1、认证过滤器
 *   @see org.siu.akagi.authentication.TokenAuthenticationFilter
 *
 * 2、认证失败&拒绝访问处理
 *  @see org.siu.akagi.authentication.DefaultAccessDeniedHandler
 *  @see org.siu.akagi.authentication.DefaultAuthenticationEntryPoint
 *
 * 3、Json Web Token 处理
 * @see org.siu.akagi.authentication.jwt.TokenProvider
 *
 * @Author Siu
 * @Date 2020/4/14 9:32
 * @Version 0.0.1
 */
package org.siu.akagi.authentication;
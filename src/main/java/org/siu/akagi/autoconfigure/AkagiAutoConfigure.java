package org.siu.akagi.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.AkagiWebSecurityConfig;
import org.siu.akagi.authentication.DefaultAccessDeniedHandler;
import org.siu.akagi.authentication.DefaultAuthenticationEntryPoint;
import org.siu.akagi.authentication.jwt.*;
import org.siu.akagi.aop.UTSKAnnotationAdvisor;
import org.siu.akagi.aop.UTSKAnnotationInterceptor;
import org.siu.akagi.autoconfigure.banner.AkagiBanner;
import org.siu.akagi.support.Authorize;
import org.siu.akagi.support.LoginService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 自动配置
 *
 * @Author Siu
 * @Date 2020/4/2 21:27
 * @Version 0.0.1
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableConfigurationProperties(AkagiProperties.class)
@ConditionalOnProperty(prefix = AkagiProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class AkagiAutoConfigure implements ApplicationRunner {

    private final AkagiProperties properties;


    /**
     * 默认情况下的模板只能支持RedisTemplate<String, String>，也就是只能存入字符串，因此支持序列化
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("'${akagi.security.token-secret-key-mode}'.toLowerCase().equals(T(org.siu.akagi.autoconfigure.AkagiTokenSignKeyMode).CUSTOM_REDIS.toString().toLowerCase())")
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        log.info("初始化-Redis Template");
        return template;
    }

    /**
     * 配置使用注解的时候缓存配置，默认是序列化反序列化的形式，加上此配置则为 json 形式
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("'${akagi.security.token-secret-key-mode}'.toLowerCase().equals(T(org.siu.akagi.autoconfigure.AkagiTokenSignKeyMode).CUSTOM_REDIS.toString().toLowerCase())")
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 配置序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheConfiguration redisCacheConfiguration = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        log.info("初始化-Redis CacheManager");
        return RedisCacheManager.builder(factory).cacheDefaults(redisCacheConfiguration).build();
    }


    /**
     * token 提供器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenProvider tokenProvider() {
        TokenProvider tokenProvider;
        if (this.properties.getTokenSignKeyMode().equals(AkagiTokenSignKeyMode.CUSTOM_LOCAL)) {
            tokenProvider = new LocalTokenProvider(this.properties.getJsonWebTokenRefreshPermit(), this.properties.getJsonWebTokenExpire(), this.properties.getJsonWebTokenExpireForRemember(), new TokenSignKeyCache());
        } else if (this.properties.getTokenSignKeyMode().equals(AkagiTokenSignKeyMode.CUSTOM_REDIS)) {
            tokenProvider = new RedisTokenProvider(this.properties.getJsonWebTokenRefreshPermit(), this.properties.getJsonWebTokenExpire(), this.properties.getJsonWebTokenExpireForRemember(), new TokenSignKeyCache());
        } else {
            tokenProvider = new DefaultTokenProvider(this.properties.getJsonWebTokenRefreshPermit(), this.properties.getJsonWebTokenExpire(), this.properties.getJsonWebTokenExpireForRemember(), this.properties.getJsonWebTokenBase64Secret());
        }
        log.info("初始化-Token Provider:[{}]", this.properties.getTokenSignKeyMode());
        return tokenProvider;
    }


    @Bean
    @ConditionalOnMissingBean
    public AkagiWebSecurityConfig akagiWebSecurityConfig(TokenProvider tokenProvider) {
        DefaultAccessDeniedHandler defaultAccessDeniedHandler = new DefaultAccessDeniedHandler();
        DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint = new DefaultAuthenticationEntryPoint();
        log.info("初始化-Akagi WebSecurity Config");
        return new AkagiWebSecurityConfig(tokenProvider, defaultAuthenticationEntryPoint, defaultAccessDeniedHandler, this.properties);
    }

    @Bean("auth")
    @ConditionalOnMissingBean
    public Authorize permitChecker() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        log.info("初始化-Authorize Service-[{}]", this.properties.getAuthService().getSimpleName());
        Constructor constructor = this.properties.getAuthService().getDeclaredConstructor(String.class, String.class);
        return (Authorize) constructor.newInstance(this.properties.getSuperUser(), this.properties.getJsonWebTokenRefreshPermit());
    }

    /**
     * 非CS_CLIENT模式才注入bean
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("!'${akagi.security.mode}'.toLowerCase().equals(T(org.siu.akagi.autoconfigure.AkagiMode).CS_CLIENT.toString().toLowerCase())")
    public LoginService loginService() {
        log.info("初始化-Login Service");
        return new LoginService();
    }

    /**
     * 加密
     *
     * @return
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() throws IllegalAccessException, InstantiationException {
        log.info("初始化-PasswordEncoder-[{}]", this.properties.getPasswordEncoder().getSimpleName());
        return this.properties.getPasswordEncoder().newInstance();
    }

    /**
     * aop
     *
     * @param tokenProvider
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public UTSKAnnotationAdvisor utskAnnotationAdvisor(TokenProvider tokenProvider) {
        UTSKAnnotationInterceptor interceptor = new UTSKAnnotationInterceptor(tokenProvider);
        log.info("初始化-UTSKAnnotationAdvisor");
        return new UTSKAnnotationAdvisor(interceptor);
    }


    @Override
    public void run(ApplicationArguments args) {
        AkagiBanner.printBanner();
    }
}

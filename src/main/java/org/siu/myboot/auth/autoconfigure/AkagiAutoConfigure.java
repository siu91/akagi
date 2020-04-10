package org.siu.myboot.auth.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.config.AkagiWebSecurityConfig;
import org.siu.myboot.auth.handler.*;
import org.siu.myboot.auth.service.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;

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
public class AkagiAutoConfigure {

    private final AkagiProperties properties;


    /**
     * 默认情况下的模板只能支持RedisTemplate<String, String>，也就是只能存入字符串，因此支持序列化
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("!'${akagi.security.secret-mode}'.toLowerCase().equals(T(org.siu.myboot.auth.autoconfigure.AkagiTokenSecretMode).CUSTOM_REDIS.toString().toLowerCase())")
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        log.info("初始化-RedisTemplate");
        return template;
    }

    /**
     * 配置使用注解的时候缓存配置，默认是序列化反序列化的形式，加上此配置则为 json 形式
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("!'${akagi.security.secret-mode}'.toLowerCase().equals(T(org.siu.myboot.auth.autoconfigure.AkagiTokenSecretMode).CUSTOM_REDIS.toString().toLowerCase())")
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 配置序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheConfiguration redisCacheConfiguration = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        log.info("初始化-RedisCacheManager");
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
        log.info("初始化-TokenProvider:[{}]", this.properties.getSecretMode());

        if (this.properties.getSecretMode().equals(AkagiTokenSecretMode.CUSTOM_LOCAL)) {
            return new LocalTokenProvider(this.properties.getJsonWebTokenRefreshPermit(), this.properties.getJsonWebTokenExpire(), this.properties.getJsonWebTokenExpireForRemember(), new SecretCache());
        } else if (this.properties.getSecretMode().equals(AkagiTokenSecretMode.CUSTOM_REDIS)) {
            return new RedisTokenProvider(this.properties.getJsonWebTokenRefreshPermit(), this.properties.getJsonWebTokenExpire(), this.properties.getJsonWebTokenExpireForRemember(), new SecretCache());
        } else {
            return new DefaultTokenProvider(this.properties.getJsonWebTokenRefreshPermit(), this.properties.getJsonWebTokenExpire(), this.properties.getJsonWebTokenExpireForRemember(), this.properties.getJsonWebTokenBase64Secret());
        }
    }


    @Bean
    @ConditionalOnMissingBean
    public AkagiWebSecurityConfig akagiWebSecurityConfig(TokenProvider tokenProvider) {
        DefaultAccessDeniedHandler defaultAccessDeniedHandler = new DefaultAccessDeniedHandler();
        DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint = new DefaultAuthenticationEntryPoint();
        log.info("初始化-AkagiWebSecurityConfig");
        return new AkagiWebSecurityConfig(tokenProvider, defaultAuthenticationEntryPoint, defaultAccessDeniedHandler, this.properties);
    }

    @Bean("pms")
    @ConditionalOnMissingBean
    public PermitService permitService() {
        PermitService permitService = new PermitService(this.properties.getSuperUser(), this.properties.getJsonWebTokenRefreshPermit());
        log.info("初始化-PermitService");
        return permitService;
    }

    /**
     * 非CS_CLIENT模式才注入bean
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("!'${akagi.security.mode}'.toLowerCase().equals(T(org.siu.myboot.auth.autoconfigure.AkagiMode).CS_CLIENT.toString().toLowerCase())")
    public LoginService loginService() {
        log.info("初始化-LoginService");
        return new LoginService();
    }

    /**
     * 加密
     *
     * @return
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        log.info("初始化-PasswordEncoder");
        return new BCryptPasswordEncoder();
    }


}

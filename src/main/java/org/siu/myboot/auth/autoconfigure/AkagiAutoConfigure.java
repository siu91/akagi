package org.siu.myboot.auth.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.config.AkagiWebSecurityConfig;
import org.siu.myboot.auth.handler.DefaultAccessDeniedHandler;
import org.siu.myboot.auth.handler.DefaultAuthenticationEntryPoint;
import org.siu.myboot.auth.jwt.TokenProvider;
import org.siu.myboot.auth.service.DefaultRedisTokenStatefulService;
import org.siu.myboot.auth.service.PermitService;
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
    @ConditionalOnProperty(prefix = AkagiProperties.PREFIX, name = "statefulToken", havingValue = "true", matchIfMissing = false)
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 配置使用注解的时候缓存配置，默认是序列化反序列化的形式，加上此配置则为 json 形式
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AkagiProperties.PREFIX, name = "statefulToken", havingValue = "true", matchIfMissing = false)
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 配置序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheConfiguration redisCacheConfiguration = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(redisCacheConfiguration).build();
    }



    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AkagiProperties.PREFIX, name = "statefulToken", havingValue = "true", matchIfMissing = false)
    public DefaultRedisTokenStatefulService akagiTokenStatefulService(RedisTemplate redisTemplate) {
        return new DefaultRedisTokenStatefulService(redisTemplate);
    }


    /**
     * token 提供器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenProvider tokenProvider() {
        return new TokenProvider(this.properties.getJsonWebTokenBase64Secret(), this.properties.getJsonWebTokenExpire(), this.properties.getJsonWebTokenExpireForRemember());
    }


    @Bean
    @ConditionalOnMissingBean
    public AkagiWebSecurityConfig akagiWebSecurityConfig(TokenProvider tokenProvider, DefaultRedisTokenStatefulService defaultRedisTokenStatefulService) {
        DefaultAccessDeniedHandler defaultAccessDeniedHandler = new DefaultAccessDeniedHandler();
        DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint = new DefaultAuthenticationEntryPoint();
        return new AkagiWebSecurityConfig(tokenProvider, defaultRedisTokenStatefulService, defaultAuthenticationEntryPoint, defaultAccessDeniedHandler, this.properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public PermitService permitService() {
        return new PermitService();
    }

    /**
     * 加密
     *
     * @return
     */
    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}

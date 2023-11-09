package com.vrv.vap.admin.config;

import com.vrv.vap.admin.common.condition.AdminConditional;
import com.vrv.vap.admin.model.BaseSecurityDomain;
import com.vrv.vap.admin.model.BaseSecurityDomainIpSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.Set;

@Configuration
@RefreshScope
@Conditional(AdminConditional.class)
public class RedisConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

//    /**
////     * 实例化 RedisTemplate 对象（保存权限访问的 Template）
////     */
////    @Bean
////    @Qualifier
////    public RedisTemplate<String, Set<String>> resourceTemplate() {
////        RedisTemplate<String, Set<String>> redisTemplate = new RedisTemplate();
////        redisTemplate.setKeySerializer(new StringRedisSerializer());
////        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
////        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
////        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
////        redisTemplate.setConnectionFactory(redisConnectionFactory);
////        return redisTemplate;
////    }
////    /**
////     * 实例化 RedisTemplate 对象（保存SSO TOKEN 的 Template）
////     */
////    @Bean
////    @Primary
////    public RedisTemplate<String, User> ssoTemplate() {
////        RedisTemplate<String, User> redisTemplate = new RedisTemplate();
////        redisTemplate.setKeySerializer(new StringRedisSerializer());
////        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
////        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
////        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
////        redisTemplate.setConnectionFactory(redisConnectionFactory);
////        return redisTemplate;
////    }

    /**
     * 实例化 RedisTemplate 对象（保存SSO TOKEN 的 Template）
     */
    @Bean
    public RedisTemplate<String, Map<String,BaseSecurityDomain>> securityDomainTemplate() {
        RedisTemplate<String, Map<String,BaseSecurityDomain>> redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


    @Bean
    public RedisTemplate<String, Map<String,BaseSecurityDomainIpSegment>> securityDomainIpSegmentTemplate() {
        RedisTemplate<String, Map<String,BaseSecurityDomainIpSegment>> redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }






//    @Bean
//    public RedisTemplate<String, String> securityDomainIpSegmentSortTemplate() {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        return redisTemplate;
//    }



    /**
     * 权限保存工具
     * */
    @Bean
    public ValueOperations<String, Set<String>> valueOperations(RedisTemplate<String, Set<String>> redisTemplate) {
        return redisTemplate.opsForValue();
    }




}
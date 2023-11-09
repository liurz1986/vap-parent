package com.vrv.vap.alarmdeal.frameworks.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class RedisConfig {
    @Value("${vap.common.session-base64:false}")
    private boolean sessionBase64;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Bean
    public RedisTemplate<String, String[]> assetTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String[]> redisTemplate = new RedisTemplate<String, String[]>();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    // 资产合入
    @Bean
    public RedisTemplate<String, String[]> assetTemplate() {
        RedisTemplate<String, String[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(sessionBase64);
        return cookieSerializer;
    }

    /**
     * 自定义Key为String类型Value为Object类型的Redis操作模板
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        RedisSerializer redisSerializer = new RedisSerializer() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                return JSONObject.toJSONBytes(o);
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                String obj = new String(bytes);
                return obj;
            }
        };

        // key采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // valuevalue采用jackson序列化方式
        redisTemplate.setValueSerializer(stringRedisSerializer);
        // hash的value采用jackson序列化方式
        redisTemplate.setHashValueSerializer(redisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }





}

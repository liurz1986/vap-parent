package com.vrv.vap.oauth2.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 认证服务器使用Redis存取令牌
 *
 * @author wh1107066
 */
@ConditionalOnProperty(prefix = "vap.oauth2.token.store", name = "type", havingValue = "redis", matchIfMissing = true)
public class AuthRedisTokenStore {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }
}

package com.vrv.vap.alarmdeal.frameworks.config;

import lombok.Data;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.config.Config;
import org.redisson.Redisson;

/**
 * @author: 梁国露
 * @since: 2022/12/8 17:57
 * @description:
 */

@Data
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedissonClient getRedisSon() {
        Config config = new Config();
        String address = new StringBuilder("redis://").append(host).append(":").append(port).toString();
        config.useSingleServer().setAddress(address);
        if (null != password && !"".equals(password.trim())) {
            config.useSingleServer().setPassword(password).setDatabase(1);
        }
        return Redisson.create(config);
    }
}

package com.vrv.vap.server.zuul;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

import javax.annotation.PostConstruct;

/**
 * @author lilang
 * @date 2020/12/10
 * @description 支持session超时时间可配置
 */
@Configuration
public class RedisSessionConfiguration extends RedisHttpSessionConfiguration {

    @Value("${spring.session.timeout:1800}")
    private int sessionTimeout;


    @PostConstruct
    @Override
    public void init() {
        super.init();
        //session超时时间
        super.setMaxInactiveIntervalInSeconds(sessionTimeout);
    }

}

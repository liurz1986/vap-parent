package com.vrv.vap.line.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fegin相关配置
 *
 * @author xw
 * @date 2018年5月3日
 */
@Configuration
public class FeignConfig {
    // /**
    // * 配置请求重试
    // *
    // */
    // @Bean
    // public Retryer feignRetryer() {
    // return new Retryer.Default(200, 2000, 3);
    // }
    //

    /**
     * 设置请求超时时间 默认 public Options() { this(10 * 1000, 60 * 1000); }
     */
    @Bean
    Request.Options feignOptions() {
        return new Request.Options(120 * 1000, 120 * 1000);
    }

    /**
     * 打印请求日志
     *
     * @return
     */
    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}

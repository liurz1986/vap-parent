package com.vrv.vap.common.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Feign统一配置，记录打印级别，全部打印
 *
 * @author liujinhui
 * date 2021/4/26 18:09
 */
@Configuration
public class FeignHeaderConfig {
    /**增加feign打印日志*/
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

    /**
     * 重写RequestInterceptor，实现客服端请求服务到微服务请求头一致
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor headerInterceptor() {
        return new HeaderInterceptor();
    }
}

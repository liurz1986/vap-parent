package com.vrv.vap.netflow.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wh1107066
 * @date 2023/9/7
 */

@Configuration
public class AccessFilterConfig {
    @Bean
    public FilterRegistrationBean myFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new AccessFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}

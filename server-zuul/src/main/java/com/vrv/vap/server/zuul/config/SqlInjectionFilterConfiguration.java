package com.vrv.vap.server.zuul.config;

import com.vrv.vap.server.zuul.filter.SqlInjectionFilter;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.DispatcherType;
import java.util.Map;

/**
 * @author huipei.x
 * @data 创建时间 2019/9/10
 * @description 类说明 :
 */
@Configuration
@EnableConfigurationProperties({SqlInjectionProperties.class})
public class SqlInjectionFilterConfiguration {
    @Autowired
    private SqlInjectionProperties sqlInjectionProperties;

    @Bean
    public FilterRegistrationBean parmsFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new SqlInjectionFilter());
        registration.addUrlPatterns(sqlInjectionProperties.getUrlPatterns());
        registration.setName(sqlInjectionProperties.getName());
        Map<String, String> initParameters = new HashedMap(2);
        if (StringUtils.hasText(sqlInjectionProperties.getExcludes())) {
            initParameters.put("excludes" , sqlInjectionProperties.getExcludes());
        }
        registration.setInitParameters(initParameters);
        registration.setOrder(Integer.MAX_VALUE - 2);
        return registration;
    }


}


package com.vrv.vap.toolkit.config;

import com.vrv.vap.toolkit.model.EnvironmentModel;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 保存当前请求生命周期的所需的类
 *
 * @author xw
 * @date 2018年5月3日
 */
@Configuration
public class EnviromentConfig {
    private static ThreadLocal<EnvironmentModel> threadLocal = new ThreadLocal<EnvironmentModel>();

    //去除tomcat依赖,否则无法在其他容器部署
/*    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }*/

    @Bean
    public FilterRegistrationBean registFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new EnvFilter());
        // 设置过滤路径,/*所有路径
        registration.addUrlPatterns("/*");
        // 设置优先级
        registration.setName("EnvFilter");
        registration.setOrder(1);
        return registration;
    }

    public static EnvironmentModel getEnvironment() {
        return threadLocal.get();
    }

    public static void removeEnvironment() {
        threadLocal.remove();
    }

    public class EnvFilter implements Filter {
        @Override
        public void destroy() {
        }

        @Override
        public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain filterChain)
                throws IOException, ServletException {
            EnvironmentModel environmentModel = new EnvironmentModel();
            HttpServletRequest request = (HttpServletRequest) srequest;
            environmentModel.setRequest(request);
            threadLocal.set(environmentModel);
            filterChain.doFilter(srequest, sresponse);
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }
    }
}

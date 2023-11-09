package com.vrv.vap.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author liujinhui
 * date 2021/4/16 17:37
 */

@Configuration
@EnableConfigurationProperties(DatasourceProperties.class)
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DruidAutoConfiguration {

    @Autowired
    private DatasourceProperties datasourceProperties;

    @Bean
    @Primary
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(datasourceProperties.getUrl());
        dataSource.setUsername(datasourceProperties.getUsername());
        dataSource.setPassword(datasourceProperties.getPassword());
        if (datasourceProperties.getInitialSize() > 0) {
            dataSource.setInitialSize(datasourceProperties.getInitialSize());
        }
        if (datasourceProperties.getMinIdle() > 0) {
            dataSource.setMinIdle(datasourceProperties.getMinIdle());
        }
        if (datasourceProperties.getMaxActive() > 0) {
            dataSource.setMaxActive(datasourceProperties.getMaxActive());
        }
        dataSource.setTestOnBorrow(datasourceProperties.isTestOnBorrow());
        try {
            dataSource.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        servletRegistrationBean.addInitParameter("loginUsername", datasourceProperties.getLoginUsername());
        servletRegistrationBean.addInitParameter("loginPassword", datasourceProperties.getLoginPassword());
        servletRegistrationBean.addInitParameter("resetEnable", "true");
        return servletRegistrationBean;

    }
}

//package com.vrv.vap.server.config;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//
///**
// * sql server数据源配置
// */
//@Configuration
//public class SqlServerDbConfig {
//
//    @Bean(name = "sqlServerDataSource")
//    @Qualifier("sqlServerDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.sqlServer")
//    public DataSource getMyDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "sqlServerJdbcTemplate")
//    public JdbcTemplate sqlServerJdbcTemplate(
//            @Qualifier("sqlServerDataSource") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
//
//}

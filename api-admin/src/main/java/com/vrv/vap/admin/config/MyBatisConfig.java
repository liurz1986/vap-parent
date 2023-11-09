package com.vrv.vap.admin.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MyBatisConfig {
    @Value("${database.dbType:}")
    private String dbType;

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider(){
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        if ("dm".equals(dbType)) {
            properties.setProperty("DM","dm");
        }
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}
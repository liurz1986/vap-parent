package com.vrv.vap.line.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by lizj on 2021/5/20
 */
@Configuration
public class DatabaseConfig {

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.put("KingbaseES","king");
        properties.put("MySQL","mysql");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}

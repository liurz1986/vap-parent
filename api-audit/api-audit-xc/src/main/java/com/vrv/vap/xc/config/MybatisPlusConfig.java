package com.vrv.vap.xc.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by lizj on 2021/5/14
 */
@Configuration
@MapperScan("com.vrv.vap.server.mapper.core")
public class MybatisPlusConfig {

    @Value("${database.type:mysql}")
    private String databaseType;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 向Mybatis过滤器链中添加分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.getDbType(databaseType.toLowerCase())));
        return interceptor;
    }

    /**
     * 当主键生成策略为INPUT时，可配置数据库支持的主键序列
     * @return
     */
//    @Bean
//    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
//        return plusProperties -> plusProperties.getGlobalConfig().getDbConfig().setKeyGenerator(new H2KeyGenerator());
//    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.put("KingbaseES","king");
        properties.put("MySQL","mysql");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}

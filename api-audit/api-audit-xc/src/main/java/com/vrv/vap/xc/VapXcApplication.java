package com.vrv.vap.xc;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.vrv.vap.toolkit.tools.SpringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, KafkaAutoConfiguration.class})
@MapperScan(basePackages = {"com.vrv.vap.xc.mapper", "com.vrv.vap.server.dao"})
@EnableFeignClients(basePackages = {"com.vrv.vap.xc.fegin"})
@ComponentScan(basePackages = {"com.vrv.vap.xc", "com.vrv.vap.toolkit"})
@EnableHystrix
@EnableEncryptableProperties
public class VapXcApplication extends SpringBootServletInitializer implements ApplicationContextAware {

    private static final Log log = LogFactory.getLog(VapXcApplication.class);

    private static ApplicationContext appCtx;

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(VapXcApplication.class, args);
        SpringUtil.setAppContext(run);
        //初始化已经迁移到com.vrv.vap.server.command.InitializedInitCommand,后续再把ApplicationContext迁移出去
    }

    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VapXcApplication.class);
    }

}

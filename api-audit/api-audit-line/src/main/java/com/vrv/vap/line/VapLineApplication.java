package com.vrv.vap.line;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, KafkaAutoConfiguration.class})
@MapperScan(basePackages = {"com.vrv.vap.line.mapper", "com.vrv.vap.server.mapper"})
@EnableFeignClients(basePackages = {"com.vrv.vap.line.fegin"})
@ComponentScan(basePackages = {"com.vrv.vap.line", "com.vrv.vap.toolkit"})
@EnableEncryptableProperties
public class VapLineApplication extends SpringBootServletInitializer implements ApplicationContextAware {

    /**
     * spring 上下文
     */
    private static ApplicationContext appCtx;

    public static void main(String[] args) {
        SpringApplication.run(VapLineApplication.class, args);

//		new TaskLoader().start();
    }

    /**
     * 获取spring上下文
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }
}

package com.vrv.vap.amonitor;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.vrv.vap.amonitor.fegin"})
@MapperScan(basePackages = {"com.vrv.vap.amonitor.mapper", "com.vrv.vap.amonitor.dao"})
@ComponentScan(basePackages = {"com.vrv.vap.amonitor", "com.vrv.vap.toolkit"})
@EnableEncryptableProperties
public class VapMonitorApplication implements ApplicationContextAware {

    /**
     * spring 上下文
     */
    private static ApplicationContext appCtx;


    public static void main(String[] args) {
        SpringApplication.run(VapMonitorApplication.class, args);
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

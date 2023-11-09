package com.vrv.vap.monitor.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@Configuration
@EnableSwagger2
@EnableAsync
public class AgentApplication extends SpringBootServletInitializer {

    private static ApplicationContext appCtx;
    public static void main(String[] args) {
        appCtx = SpringApplication.run(AgentApplication.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AgentApplication.class);
    }




    /**
     * 获取spring上下文
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }



}

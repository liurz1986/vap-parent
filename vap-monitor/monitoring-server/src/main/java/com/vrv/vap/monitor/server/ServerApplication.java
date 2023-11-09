package com.vrv.vap.monitor.server;

import com.purgeteam.dynamic.config.starter.annotation.EnableDynamicConfigEvent;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.web.http.DefaultCookieSerializer;


@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.vrv.vap","com.vrv.vap.syslog.*"})
@EnableEncryptableProperties
@EnableAsync
@EnableDynamicConfigEvent
public class ServerApplication {

    @Value("${vap.common.session-base64:false}")
    private Boolean sessionBase64;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(sessionBase64);
        return cookieSerializer;
    }
}

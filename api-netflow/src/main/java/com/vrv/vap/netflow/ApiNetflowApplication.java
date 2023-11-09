package com.vrv.vap.netflow;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.web.http.DefaultCookieSerializer;


/**
 * @author wh1107066
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.vrv.vap"})
@EnableEncryptableProperties
@EnableScheduling
public class ApiNetflowApplication {

    @Value("${vap.common.session-base64:false}")
    private Boolean sessionBase64;

    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(sessionBase64);
        return cookieSerializer;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ApiNetflowApplication.class, args);
    }
}

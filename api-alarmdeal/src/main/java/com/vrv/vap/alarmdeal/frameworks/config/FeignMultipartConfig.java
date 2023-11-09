package com.vrv.vap.alarmdeal.frameworks.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignMultipartConfig {


    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
    

 
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

 

}

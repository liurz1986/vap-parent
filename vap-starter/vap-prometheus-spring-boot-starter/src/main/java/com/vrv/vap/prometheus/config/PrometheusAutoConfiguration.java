package com.vrv.vap.prometheus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author wh1107066
 */
@Configuration
public class PrometheusAutoConfiguration {
    @Value("${spring.application.name:NO-APPLICATION-NAME}")
    private String applicationName;

    @Bean
    MeterRegistryCustomizer meterRegistryCustomizer() {
        return meterRegistry -> {
            meterRegistry.config().commonTags("application", applicationName);
        };
    }
}

package com.vrv.vap.toolkit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;


@Configuration
@ConfigurationProperties(prefix = "session.base64")
public class RedisConfig {

    private boolean enabled;

    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(enabled);
//        cookieSerializer.setCookieName("SESSION");
//        cookieSerializer.setUseHttpOnlyCookie(false);
//        cookieSerializer.setSameSite(null);
        return cookieSerializer;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
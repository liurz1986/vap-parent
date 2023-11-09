package com.vrv.vap.server.zuul;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.util.Set;

@EnableZuulServer
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.vrv.vap"})
@EnableEncryptableProperties
public class ServerZuulApplication {
    private static final String CACHE_ROLE = "_ROLE";

    @Value("${vap.common.session-base64:false}")
    private Boolean sessionBase64;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ServerZuulApplication.class);
        application.setAllowBeanDefinitionOverriding(true);
        application.run(args);
    }

    @Bean
    public Cache<String, Set<String>> roleCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CACHE_ROLE, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, (Class<Set<String>>) (Object) Set.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CACHE_ROLE, String.class, (Class<Set<String>>) (Object) Set.class);
    }

    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(sessionBase64);
        return cookieSerializer;
    }

}

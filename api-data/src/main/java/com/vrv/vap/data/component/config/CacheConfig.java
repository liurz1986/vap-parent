package com.vrv.vap.data.component.config;

import com.vrv.vap.data.model.*;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Configuration
public class CacheConfig {


    /**
     * 通用  -- 数据源
     */
    @Bean
    public Cache<Integer, Source> SOURCE_MAP() {
        final String SOURCE_MAP = "SOURCE_MAP";
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(SOURCE_MAP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, Source.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(SOURCE_MAP, Integer.class, Source.class);
    }

    @Bean
    public Cache<Integer, List<SourceField>> SOURCE_FIELDS_MAP() {
        Class clazz = new ArrayList<SourceField>().getClass();
        final String SOURCE_FIELDS_MAP = "SOURCE_FIELDS_MAP";
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(SOURCE_FIELDS_MAP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, clazz, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(SOURCE_FIELDS_MAP, Integer.class, clazz);
    }

    @Bean
    public Cache<Integer, HashMap<String, SourceField>> SOURCE_FIELDS_NAME_MAP() {
        Class clazz = new HashMap<String, SourceField>().getClass();
        final String SOURCE_FIELDS_NAME_MAP = "SOURCE_FIELDS_NAME_MAP";
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(SOURCE_FIELDS_NAME_MAP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, clazz, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(SOURCE_FIELDS_NAME_MAP, Integer.class, clazz);
    }


    /**
     * 数据管理
     */
    @Bean
    public Cache<Integer, Maintain> MAINTAIN_MAP() {
        final String MAINTAIN_MAP = "MAINTAIN_MAP";
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(MAINTAIN_MAP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, Maintain.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(MAINTAIN_MAP, Integer.class, Maintain.class);
    }

    /**
     * 探索
     */
    @Bean
    public Cache<Integer, DiscoverEntity> ENTITY_MAP() {
        final String ENTITY_MAP = "ENTITY_MAP";
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(ENTITY_MAP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, DiscoverEntity.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(ENTITY_MAP, Integer.class, DiscoverEntity.class);
    }

    @Bean
    public Cache<Integer, DiscoverEdge> EDGE_MAP() {
        final String EDGE_MAP = "EDGE_MAP";
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(EDGE_MAP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, DiscoverEdge.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(EDGE_MAP, Integer.class, DiscoverEdge.class);
    }


}

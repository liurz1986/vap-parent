package com.vrv.vap.admin.config;

import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.vo.AssetVo;
import com.vrv.vap.admin.vo.BaseKoalOrgVO;
import com.vrv.vap.admin.vo.Menu;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CacheConfig {

    private static final String CACHE_MENU = "_MENU";
    private static final String CACHE_CONF = "_CONF";
    private static final String CHCHE_KOAL_ORG = "_KOAL_ORG";

    public static final String CACHE_IP_KOAL_ORG = "_IP_KOAL_ORG";

    public static final String CACHE_IP_SEC = "_IP_SEC";
    public static final String CHCHE_IP_ASSET = "_IP_ASSET";
    public static final String CHCHE_IP_PERSON = "_IP_PERSON";
    public static final String CHCHE_IP_APP = "_IP_APP";
    public static final String CHCHE_APP_ASSET = "_APP_ASSET";
    public static final String CHCHE_URL_APP = "_URL_APP";
    @Bean
    public Cache<String, List<Menu>> menuCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CACHE_MENU, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, (Class<List<Menu>>) (Object) List.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CACHE_MENU, String.class, (Class<List<Menu>>) (Object) List.class);
    }


    @Bean
    public Cache<String, SystemConfig> confCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CACHE_CONF, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, SystemConfig.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CACHE_CONF, String.class, SystemConfig.class);
    }

    @Bean
    public Cache<String, List<BaseKoalOrgVO>> koalOrgCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CHCHE_KOAL_ORG, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, (Class<List<BaseKoalOrgVO>>)(Object) List.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CHCHE_KOAL_ORG, String.class, (Class<List<BaseKoalOrgVO>>)(Object) List.class);
    }

    @Bean
    public Cache<String, BaseKoalOrg> ipKoalOrgCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CACHE_IP_KOAL_ORG, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, BaseKoalOrg.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CACHE_IP_KOAL_ORG, String.class, BaseKoalOrg.class);
    }
    @Bean
    public Cache<String, AssetVo> ipAssetCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CHCHE_IP_ASSET, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, AssetVo.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CHCHE_IP_ASSET, String.class, AssetVo.class);
    }
    @Bean
    public Cache<String, BasePersonZjg> ipPersonCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CHCHE_IP_PERSON, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, BasePersonZjg.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CHCHE_IP_PERSON, String.class, BasePersonZjg.class);
    }
    @Bean
    public Cache<String, AppSysManager> ipAppCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CHCHE_IP_APP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, AppSysManager.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CHCHE_IP_APP, String.class, AppSysManager.class);
    }
    @Bean
    public Cache<String, AppSysManager> urlAppCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CHCHE_URL_APP, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, AppSysManager.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CHCHE_URL_APP, String.class, AppSysManager.class);
    }


    @Bean
    public Cache<String, AssetVo> appAssetCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CHCHE_APP_ASSET, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, AssetVo.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CHCHE_APP_ASSET, String.class, AssetVo.class);
    }
    @Bean
    public Cache<String, BaseSecurityDomain> ipSecCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CACHE_IP_SEC, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, BaseSecurityDomain.class, ResourcePoolsBuilder.heap(10))).build();
        cacheManager.init();
        return cacheManager.getCache(CACHE_IP_SEC, String.class, BaseSecurityDomain.class);
    }

}

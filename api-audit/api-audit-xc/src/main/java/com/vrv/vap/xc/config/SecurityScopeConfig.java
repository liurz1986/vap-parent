package com.vrv.vap.xc.config;

import com.vrv.vap.xc.interceptor.KingQuotationInterceptor;
import com.vrv.vap.xc.interceptor.KingQuotationUpdateInterceptor;
import com.vrv.vap.xc.interceptor.SecurityScopeInterceptor;

//@Configuration
public class SecurityScopeConfig {

//    @Bean
    public SecurityScopeInterceptor securityScope() {
        return new SecurityScopeInterceptor();
    }

//    @Bean
    public KingQuotationInterceptor kingBaseInterceptor() {
        return new KingQuotationInterceptor();
    }

//    @Bean
    public KingQuotationUpdateInterceptor kingBaseInterceptor2() {
        return new KingQuotationUpdateInterceptor();
    }
}

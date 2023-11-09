package com.vrv.vap.server.zuul.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author huipei.x
 * @data 创建时间 2019/9/10
 * @description 类说明 :
 */
@Configuration
@ConfigurationProperties(prefix = SqlInjectionProperties.SqlINJECTION_PREFIX )
public class SqlInjectionProperties {
    public final  static String SqlINJECTION_PREFIX="sqlinjection";
    private String urlPatterns="";
    private String name="sqlInjectionFilter";
    private String excludes="";

    public String getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }
}

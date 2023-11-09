package com.vrv.vap.xc.config;

import com.vrv.vap.toolkit.config.PathConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * 文件上传相关配置
 */
@Configuration
public class MultipartConfig {

    @Autowired
    private PathConfig config;

    /**
     * 重新设置文件上传temp目录(默认的temp目录会被系统自动清理而引发io异常)
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(config.getTmp());
        return factory.createMultipartConfig();
    }
}

package com.vrv.vap.admin.common.config;

import com.vrv.vap.admin.service.FileUploadInfoService;
import com.vrv.vap.admin.service.impl.FastdfsFileUploadInfoServiceImpl;
import com.vrv.vap.admin.service.impl.LocalFileUploadInfoServiceImpl;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "fileupload")
public class FileUploadConfig {
    private String type;
    private String path;    // 本地存储方案的配置路径

    @Bean
    @ConditionalOnProperty(prefix = "fileupload", name = "type", havingValue = "local", matchIfMissing = true)
    public FileUploadInfoService localFileUpload() {
        FileUploadInfoService fileUploadInfoService = new LocalFileUploadInfoServiceImpl();
        return fileUploadInfoService;
    }

    @Bean
    @ConditionalOnProperty(prefix = "fileupload", name = "type", havingValue = "fastdfs")
    public FileUploadInfoService fastdfsFileUpload() {
        FileUploadInfoService fileUploadInfoService = new FastdfsFileUploadInfoServiceImpl();
        return fileUploadInfoService;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

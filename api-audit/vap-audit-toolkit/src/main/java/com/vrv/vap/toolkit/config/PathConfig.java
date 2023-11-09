package com.vrv.vap.toolkit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件路径配置
 *
 * @author xw
 * @date 2018年4月3日
 */
@Component
@ConfigurationProperties(prefix = "dir")
public class PathConfig {

    private String base;
    private String upload;
    private String tmp;
    private String dataBackup;
    private String backup;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getDataBackup() {
        return dataBackup;
    }

    public void setDataBackup(String dataBackup) {
        this.dataBackup = dataBackup;
    }

    public String getBackup() {
        return backup;
    }

    public void setBackup(String backup) {
        this.backup = backup;
    }
}

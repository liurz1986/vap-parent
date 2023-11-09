package com.vrv.vap.xc.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 参数配置剑
 *
 * @author lil
 * @date 2019年6月13日
 */
@Component
@ConfigurationProperties(prefix = ConfigPrefix.VAP_BUSINESS_PARAM)
public class ParamConfig {

    private int netflowTime;

    private int insertMysqlSize;

    private String zipPassword;

    private String flumeDomainPath;

    public int getNetflowTime() {
        return netflowTime;
    }

    public void setNetflowTime(int netflowTime) {
        this.netflowTime = netflowTime;
    }

    public int getInsertMysqlSize() {
        return insertMysqlSize;
    }

    public void setInsertMysqlSize(int insertMysqlSize) {
        this.insertMysqlSize = insertMysqlSize;
    }

    public String getZipPassword() {
        return zipPassword;
    }

    public void setZipPassword(String zipPassword) {
        this.zipPassword = zipPassword;
    }

    public String getFlumeDomainPath() {
        return flumeDomainPath;
    }

    public void setFlumeDomainPath(String flumeDomainPath) {
        this.flumeDomainPath = flumeDomainPath;
    }
}

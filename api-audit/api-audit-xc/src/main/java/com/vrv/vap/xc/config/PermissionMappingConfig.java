package com.vrv.vap.xc.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限相关-es索引/mysql表与过滤字段名匹配
 * Created by lizj on 2019/10/23.
 */
@Component
@ConfigurationProperties(prefix = ConfigPrefix.VAP_PERMISSION_MAPPING)
public class PermissionMappingConfig {


    /**
     * es索引与过滤字段名匹配
     */
    private String[] es;

    /**
     * mysql表与过滤字段名匹配（ip段过滤）
     */
    private String[] mysqlIp;

    /**
     * mysql表与过滤字段名匹配（安全域过滤）
     */
    private String[] mysql;

    public String[] getMysqlIp() {
        return mysqlIp;
    }

    public void setMysqlIp(String[] mysqlIp) {
        this.mysqlIp = mysqlIp;
    }

    public String[] getMysql() {
        return mysql;
    }

    public void setMysql(String[] mysql) {
        this.mysql = mysql;
    }

    public Map<String, String[]> getMysqlIpMapping() {
        return getMapping(mysqlIp);
    }

    public Map<String, String[]> getMysqlMapping() {
        return getMapping(mysql);
    }

    public String[] getEs() {
        return es;
    }

    public void setEs(String[] es) {
        this.es = es;
    }

    public Map<String, String[]> getEsMapping() {
        return getMapping(es);
    }

    public Map<String, String[]> getMapping(String[] mapping) {
        Map<String, String[]> result = new HashMap<>();
        if (mapping != null && mapping.length > 0 && !"".equals(mapping[0])) {
            for (String m : mapping) {
                String[] strs = m.split(":");
                String[] fields = strs[1].split(",");
                result.put(strs[0], fields);
            }
        }
        return result;
    }
}

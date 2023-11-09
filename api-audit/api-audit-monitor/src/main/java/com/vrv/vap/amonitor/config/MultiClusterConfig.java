package com.vrv.vap.amonitor.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多集群es索引映射关系配置
 * Created by lizj on 2021/3/29.
 */
@Component
@ConfigurationProperties(prefix = ConfigPrefix.VAP_ES_MULTI_CLUSTER)
public class MultiClusterConfig {


    /**
     * es索引与过滤字段名匹配
     */
    private List<Config> configs;

    /**
     * 是否开启多集群模式
     */
    private boolean open = false;

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getClusterNameByIndex(String index) {
        String result = "";
        if (open) {
            for (Config c : configs) {
                if (c.getIndexMapping().contains(index)) {
                    result = c.getClusterName();
                    break;
                } else {
                    continue;
                }
            }
        }
        return result;
    }

    public static class Config {
        private String clusterName;
        private List<String> indexMapping;

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public List<String> getIndexMapping() {
            return indexMapping;
        }

        public void setIndexMapping(List<String> indexMapping) {
            this.indexMapping = indexMapping;
        }
    }
}

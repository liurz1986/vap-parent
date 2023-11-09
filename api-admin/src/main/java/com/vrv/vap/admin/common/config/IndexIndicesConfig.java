package com.vrv.vap.admin.common.config;

import org.apache.commons.collections.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lilang
 * @date 2021/7/13
 * @description
 */
@Component
@ConfigurationProperties(prefix = "index")
public class IndexIndicesConfig {

    private String[] indices;

    private String defaultTimeFormat = "yyyy.MM.dd";

    private Map<String,AliasConfig> aliasConfig = new HashMap<>();

    public synchronized Map<String, AliasConfig> getAliasConfig() {
        if(indices == null || indices.length == 0){
            return null;
        }
        if(MapUtils.isEmpty(aliasConfig)){
            //aliasConfig = new HashMap<>(indices.length);
            for (String index : indices) {
                String[] aliasAutoConfig = index.split(",");
                if(aliasAutoConfig.length>1){
                    aliasConfig.put(aliasAutoConfig[0], new AliasConfig(aliasAutoConfig[0],aliasAutoConfig[1]));
                } else {
                    aliasConfig.put(aliasAutoConfig[0], new AliasConfig(aliasAutoConfig[0],defaultTimeFormat));
                }
            }
        }
        return aliasConfig;
    }

    public String[] getIndices() {
        return indices;
    }

    public void setIndices(String[] indices) {
        this.indices = indices;
    }

    public static class AliasConfig{
        private String index;
        private String timeFormat;

        public AliasConfig(String index, String timeFormat) {
            this.index = index;
            this.timeFormat = timeFormat;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getTimeFormat() {
            return timeFormat;
        }

        public void setTimeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
        }
    }

    public String getDefaultTimeFormat() {
        return defaultTimeFormat;
    }

    public void setDefaultTimeFormat(String defaultTimeFormat) {
        this.defaultTimeFormat = defaultTimeFormat;
    }
}

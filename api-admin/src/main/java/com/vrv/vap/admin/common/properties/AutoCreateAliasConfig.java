package com.vrv.vap.admin.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 索引创建配置实体类
 *
 * @author cz
 *
 * @date 2019年12月09日
 */
@Component
@ConfigurationProperties(prefix = "auto-alias")
public class AutoCreateAliasConfig {

    /**
     * 默认时间字段
     */
    private String defaultTimeField;
    /**
     * 默认时间格式化
     */
    private String defaultTimeFormat;
    /**
     * 默认格式化类型
     */
    private String defaultType;
    /**
     * 只创建当月
     */
    private boolean currentMonth;
    /**
     * 配置需要创建按天别名的index
     */
    private String[] indices;

    private Map<String, AutoCreateAliasConfig.AliasConfig> aliasConfig;

    public synchronized Map<String, AutoCreateAliasConfig.AliasConfig> getAliasConfig() {
        if(indices == null || indices.length==0){
            return null;
        }
        if(aliasConfig==null){
            aliasConfig = new HashMap<>(indices.length);
            for (String index : indices) {
                String[] aliasAutoConfig = index.split(",");
                if (aliasAutoConfig.length == 1) {
                    aliasConfig.put(aliasAutoConfig[0], new AutoCreateAliasConfig.AliasConfig(aliasAutoConfig[0],defaultTimeField,defaultTimeFormat,defaultType));
                } else if (aliasAutoConfig.length == 2) {
                    aliasConfig.put(aliasAutoConfig[0], new AutoCreateAliasConfig.AliasConfig(aliasAutoConfig[0],aliasAutoConfig[1],defaultTimeFormat,defaultType));
                } else if (aliasAutoConfig.length == 3) {
                    aliasConfig.put(aliasAutoConfig[0], new AutoCreateAliasConfig.AliasConfig(aliasAutoConfig[0],aliasAutoConfig[1],aliasAutoConfig[2],defaultType));
                } else {
                    aliasConfig.put(aliasAutoConfig[0], new AutoCreateAliasConfig.AliasConfig(aliasAutoConfig[0],aliasAutoConfig[1],aliasAutoConfig[2],aliasAutoConfig[3]));
                }
            }
        }
        return aliasConfig;
    }

    public String getDefaultTimeField() {
        return defaultTimeField;
    }

    public void setDefaultTimeField(String defaultTimeField) {
        this.defaultTimeField = defaultTimeField;
    }

    public String getDefaultTimeFormat() {
        return defaultTimeFormat;
    }

    public void setDefaultTimeFormat(String defaultTimeFormat) {
        this.defaultTimeFormat = defaultTimeFormat;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        this.currentMonth = currentMonth;
    }

    public String[] getIndices() {
        return indices;
    }

    public void setIndices(String[] indices) {
        this.indices = indices;
    }

    public static class AliasConfig{
        private String index;
        private String timeField;
        private String timeFormat;
        private String type;

        public AliasConfig(String index, String timeField, String timeFormat, String type) {
            this.index = index;
            this.timeField = timeField;
            this.timeFormat = timeFormat;
            this.type = type;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getTimeField() {
            return timeField;
        }

        public void setTimeField(String timeField) {
            this.timeField = timeField;
        }

        public String getTimeFormat() {
            return timeFormat;
        }

        public void setTimeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

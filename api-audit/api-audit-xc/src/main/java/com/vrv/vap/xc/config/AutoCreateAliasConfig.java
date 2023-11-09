package com.vrv.vap.xc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 索引创建配置实体类
 *
 * @author cz
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
     * 只创建当月
     */
    private boolean currentMonth;
    /**
     * 配置需要创建按天别名的index
     */
    private String[] indices;

    private Map<String, AliasConfig> aliasConfig;

    public synchronized Map<String, AliasConfig> getAliasConfig() {
        if (indices.length == 0) {
            return null;
        }
        if (aliasConfig == null) {
            aliasConfig = new HashMap<>(indices.length);
            for (String index : indices) {
                String[] aliasAutoConfig = index.split(",");
                if (aliasAutoConfig.length > 1) {
                    aliasConfig.put(aliasAutoConfig[0], new AliasConfig(aliasAutoConfig[0], aliasAutoConfig[1], aliasAutoConfig[2]));
                } else {
                    aliasConfig.put(aliasAutoConfig[0], new AliasConfig(aliasAutoConfig[0], defaultTimeField, defaultTimeFormat));
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

    public static class AliasConfig {
        private String index;
        private String timeField;
        private String timeFormat;

        public AliasConfig(String index, String timeField, String timeFormat) {
            this.index = index;
            this.timeField = timeField;
            this.timeFormat = timeFormat;
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
    }
}

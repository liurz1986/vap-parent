package com.vrv.vap.monitor.model.es;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexTemplate {
    private String order;
    @JsonProperty(value = "index_patterns")
    @JSONField(name="index_patterns")
    private String[] indexPatterns;

    private String template;

    private Map<String, Object> settings;
    private Mappings mappings;
    private Map<String, Object> aliases;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Mappings {
        private Map<String, Map<String, Field>> logs;
        private Map<String, Field> properties;

        @JsonIgnore
        public Map<String, Field> getCompatibleProperties() {
            if (properties == null) {
                return logs.get("properties");
            }
            return properties;
        }


    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {
        private String type;
        private String format;
        private String index;

        public Field() {
        }

        public Field(String type) {
            this.type = type;
        }

        public Field(String type, String index) {
            this.type = type;
            this.index = index;
        }

        public Field(String type, String format, String index) {
            this.type = type;
            this.format = format;
            this.index = index;
        }
    }


}

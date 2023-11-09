package com.vrv.vap.xc.config;

import com.vrv.vap.toolkit.constant.ConfigPrefix;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = ConfigPrefix.OUT_THREAD_OUT_VISIT_TYPE)
public class OutThreadOutVisitTypeConfig {

    /**
     * es索引与过滤字段名匹配
     */
    private String[] outVisitType;


    public String[] getOutVisitType() {
        return outVisitType;
    }

    public void setOutVisitType(String[] outVisitType) {
        this.outVisitType = outVisitType;
    }

    public Map<String, String> getOutVisitTypeMapping() {
        return getMapping(outVisitType);
    }

    public Map<String, String> getMapping(String[] mapping) {
        Map<String, String> result = new HashMap<>();
        if (mapping != null && mapping.length > 0 && !"".equals(mapping[0])) {
            for (String m : mapping) {
                String[] strs = m.split(":");
                result.put(strs[0], strs[1]);
            }
        }
        return result;
    }
}

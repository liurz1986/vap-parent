package com.vrv.vap.xc.model;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsTemplate {
    private int order = 0;
    private String name;
    private List<String> index_patterns;
    private Map<String, Map<String,Object>> settings;
    private Map<String, Map<String,Object>> mappings;
    private List<EsColumns> properties;


    public EsTemplate(String name, List<EsColumns> properties) {
        this.name = name;
        this.properties = properties;
    }

    public EsTemplate() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<String> getIndex_patterns() {
        return index_patterns;
    }

    public void setIndex_patterns(List<String> index_patterns) {
        this.index_patterns = index_patterns;
    }

    public Map<String, Map<String, Object>> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Map<String, Object>> settings) {
        this.settings = settings;
    }

    public Map<String, Map<String, Object>> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Map<String, Object>> mappings) {
        this.mappings = mappings;
    }

    public List<EsColumns> getProperties() {
        return properties;
    }

    public void setProperties(List<EsColumns> properties) {
        this.properties = properties;
    }

    public String toTemplateJson(){
        this.index_patterns = new ArrayList<>();
        this.index_patterns.add(this.name+"-*");
        this.settings = new HashMap<>();
        this.mappings = new HashMap<>();
        Map<String,Object> index = new HashMap<>();
        index.put("number_of_shards","3");
        index.put("number_of_replicas","0");
        this.settings.put("index",index);
        if(CollectionUtils.isNotEmpty(this.properties)){
            Map<String,Object> propertiesMap = new HashMap<>();
            this.properties.forEach(p ->{
                Map<String,Object> colMap = new HashMap<>();
                colMap.put("type",p.getType());
                if(StringUtils.isNotEmpty(p.getFormat())){
                    colMap.put("format",p.getFormat());
                }
                propertiesMap.put(p.getKey(),colMap);
            });
            this.mappings.put("properties",propertiesMap);
        }
        this.properties = null;
        this.name = null;
        return JSON.toJSONString(this);
    }
}

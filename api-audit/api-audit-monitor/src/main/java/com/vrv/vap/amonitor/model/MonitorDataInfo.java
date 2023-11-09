package com.vrv.vap.amonitor.model;

import java.util.Map;

public class MonitorDataInfo {

    private String indexName;

    private String topic;

    private Map<String, Object> data;

    public MonitorDataInfo(String indexName, String topic, Map<String, Object> data) {
        this.indexName = indexName;
        this.topic = topic;
        this.data = data;
    }

    public MonitorDataInfo kv(String key, Object val) {
        this.data.put(key, val);
        return this;
    }

    public MonitorDataInfo kvAll(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}

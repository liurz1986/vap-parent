package com.vrv.vap.admin.vo;

import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2021/3/26
 * @description
 */
public class ReportStrategyVO {

    private String id;

    private String sid;

    private List<Map<String,Object>> strategy;

    private Map kafka;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public List<Map<String, Object>> getStrategy() {
        return strategy;
    }

    public void setStrategy(List<Map<String, Object>> strategy) {
        this.strategy = strategy;
    }

    public Map getKafka() {
        return kafka;
    }

    public void setKafka(Map kafka) {
        this.kafka = kafka;
    }
}

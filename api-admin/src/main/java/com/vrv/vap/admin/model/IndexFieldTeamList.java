package com.vrv.vap.admin.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2019/7/19
 * @description
 */
@Component
@ConfigurationProperties(prefix = "index.fieldteam")
public class IndexFieldTeamList {

    private List<Map<String, String>> list;

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "IndexFieldTeamList{" +
                "list=" + list +
                '}';
    }
}

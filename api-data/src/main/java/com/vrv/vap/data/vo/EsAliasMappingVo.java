package com.vrv.vap.data.vo;

import java.util.Map;

/**
 * @author lilang
 * @date 2021/11/8
 * @description
 */
public class EsAliasMappingVo {

    private String aliasName;

    private Map<String,Object> filter;

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }
}

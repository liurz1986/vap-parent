package com.vrv.vap.netflow.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;

/**
 * @author lilang
 * @date 2022/3/28
 * @description
 */
public class CollectorOfflineTemplateQuery extends Query {

    @QueryLike
    private String name;

    private Integer type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

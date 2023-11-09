package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
public class CollectorDataAccessQuery extends Query {

    @ApiModelProperty("任务名称")
    @QueryLike
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

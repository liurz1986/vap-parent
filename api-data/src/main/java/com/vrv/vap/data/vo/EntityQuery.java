package com.vrv.vap.data.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "查询实体对象请求参数")
public class EntityQuery extends Query {


    @QueryLike
    @ApiModelProperty("实体名称")
    private String name;

    @QueryLike
    @ApiModelProperty("实体描述")
    private String description;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.vrv.vap.data.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "查询实体关系请求参数")
public class EdgeQuery extends Query {


    @ApiModelProperty("关系名称")
    private String name;

    @ApiModelProperty("关系描述")
    private String description;

    @ApiModelProperty("数据源")
    private Integer sourceId;


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

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }
}

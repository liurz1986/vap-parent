package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 */
public class StatisticsModel {


    @ApiModelProperty("关系编号")
    private Integer edgeId;

    @ApiModelProperty("关系名称")
    private String edgeName;

    @ApiModelProperty("数量")
    private Long count;

    public Integer getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Integer edgeId) {
        this.edgeId = edgeId;
    }

    public String getEdgeName() {
        return edgeName;
    }

    public void setEdgeName(String edgeName) {
        this.edgeName = edgeName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
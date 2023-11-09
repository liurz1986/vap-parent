package com.vrv.vap.data.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;


public class EntityRelQuery extends Query {

    @ApiModelProperty("实体ID")
    private Integer entityId;

    @ApiModelProperty("数据源")
    private Integer sourceId;

    @QueryLike
    @ApiModelProperty("字段")
    private String field;

    @QueryLike
    @ApiModelProperty("字段描述")
    private String description;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

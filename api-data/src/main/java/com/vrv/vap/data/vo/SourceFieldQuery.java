package com.vrv.vap.data.vo;


import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@ApiModel(value = "根据数据源ID进行查询，用于数据源关联查询")
public class SourceFieldQuery extends Query {

    @ApiModelProperty(value = "数据源ID", hidden = true)
    private Integer sourceId;

    @QueryLike
    @ApiModelProperty("字段名")
    private String field;

    @QueryLike
    @ApiModelProperty("字段标题")
    private String name;

    @ApiModelProperty("类型，支持：keyword text long double date object json")
    private String type;

    @ApiModelProperty("是否显示")
    private Boolean show;

    @ApiModelProperty("是否标签")
    private Boolean tag;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getShow() {
        return show;
    }

    public Boolean isShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public Boolean getTag() {
        return tag;
    }

    public Boolean isTag() {
        return tag;
    }

    public void setTag(Boolean tag) {
        this.tag = tag;
    }

}

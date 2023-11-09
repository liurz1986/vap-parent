package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("地图查询对象")
public class VisualMapQuery extends Query {

    @ApiModelProperty("主键")
    private Integer id;

    @QueryLike
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("是否默认")
    private String mapDefault;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMapDefault() {
        return mapDefault;
    }

    public void setMapDefault(String mapDefault) {
        this.mapDefault = mapDefault;
    }
}

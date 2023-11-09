package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("图形配置查询对象")
public class VisualWidgetQuery extends Query {

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("类型")
    private String visualType;

    @ApiModelProperty("标题")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisualType() {
        return visualType;
    }

    public void setVisualType(String visualType) {
        this.visualType = visualType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("条件查询对象")
public class ConditionQuery extends Query {

    @ApiModelProperty("标题")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

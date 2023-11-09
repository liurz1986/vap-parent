package com.vrv.vap.data.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "查询保存的条件参数")
public class ConditionQuery extends Query {


    @QueryLike
    @ApiModelProperty("条件命名")
    private String name;

    @ApiModelProperty("用户")
    private Integer userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

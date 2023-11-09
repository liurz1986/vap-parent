package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("主题分组查询对象")
@Getter
@Setter
public class IndexTopicQuery extends Query {


    @ApiModelProperty("状态 01 可用，02 不可用")
    private String status;

    @ApiModelProperty("分组名称")
    private String name;

    @ApiModelProperty("父组ID，0表示根目录")
    private Integer parentId;

    @ApiModelProperty("主题id")
    private Integer id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

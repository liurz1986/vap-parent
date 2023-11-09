package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("查询机构下人员参数")
public class OrgLeaderQuery {

    @ApiModelProperty(value="组织机构code")
    private String code;
	@ApiModelProperty(value="人员id")
    private String userId;
	@ApiModelProperty(value="是否是领导")
    private Integer isLeader;



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Integer isLeader) {
        this.isLeader = isLeader;
    }
}

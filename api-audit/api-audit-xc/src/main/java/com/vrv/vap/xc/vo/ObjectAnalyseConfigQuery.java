package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.annotations.LogDict;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel
@SuppressWarnings("unused")
public class ObjectAnalyseConfigQuery extends Query {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 用户登录账号
     */
    @ApiModelProperty("用户登录账号")
    private String userAccount;

    /**
     * 1用户行为分析,2运维行为分析,3应用行为分析,4单位互联分析,5涉密信息情况
     */
    @ApiModelProperty("类型")
    @LogDict("f5a4ae5b-3cee-a84f-7471-8f23ezjg0200")
    private String type;

    /**
     * value
     */
    @ApiModelProperty("值")
    private String value;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
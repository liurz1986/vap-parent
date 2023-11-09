package com.vrv.vap.alarmdeal.business.appsys.vo;

import com.vrv.vap.jpa.web.page.PageReqVap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lps 2021/8/9
 */

@Data
@ApiModel(value = "应用系统管理查询VO")
public class AppSysManagerQueryVo extends PageReqVap {


    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "员工编号")
    private String personNo;
    @ApiModelProperty(value = "责任人")
    private String personName;

    /**
     * 组织机构名称
     */
    @ApiModelProperty(value = "组织机构名称")
    private String departmentName;

    /**
     * 涉密等级
     */
    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;

    @ApiModelProperty(value = "涉密厂商")
    private String secretCompany;

    @ApiModelProperty(value = "账号")
    private String appAccount;

    @ApiModelProperty(value="窃泄密值")
    private String beginValue;
    @ApiModelProperty(value="窃泄密值")
    private String endValue;
    @ApiModelProperty(value = "仅看关注")
    Boolean isJustAssetOfConcern;
    @ApiModelProperty(value = "")
    private String appIds;
}

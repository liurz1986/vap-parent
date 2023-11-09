package com.vrv.vap.admin.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.vrv.vap.common.annotation.LogColumn;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
public class BaseSecurityDomainInfo implements Serializable {

    private Integer id;
    private String code;
    @Excel(name = "边界名称" ,orderNum = "1")
    @ApiModelProperty(value = "边界名称")
    private String domainName;
    @Excel(name = "互联IP" ,orderNum = "2")
    @ApiModelProperty(value = "互联IP")
    private String rangIp;
    @Excel(name = "近一个月通信总包数" ,orderNum = "3")
    @ApiModelProperty(value = "近一个月通信总包数")
    private Integer packages=0;
    @Excel(name = "责任人" ,orderNum = "4")
    @ApiModelProperty(value = "责任人")
    private String responsibleName;
    @Excel(name = "部门" ,orderNum = "5")
    @ApiModelProperty(value = "部门")
    private String orgName;
    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;
    @Excel(name = "涉密等级" ,orderNum = "6")
    private String secretName;
    @Excel(name = "事件数量" ,orderNum = "7")
    @ApiModelProperty(value = "事件数量")
    private Integer countEvent=0;
    @Excel(name = "泄密" ,orderNum = "8")
    @ApiModelProperty(value = "窃泄密值")
    private Integer stealLeakValue;
    @ApiModelProperty(value = "仅看关注")
    Boolean isJustAssetOfConcern=false;
}

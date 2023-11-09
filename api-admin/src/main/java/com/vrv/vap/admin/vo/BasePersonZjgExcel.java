package com.vrv.vap.admin.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

@Data
public class BasePersonZjgExcel {

    //上级机构编码	机构编码	机构名称	结构缩写	 其他名称	机构类型	机构级别

    /**
     * 员工编号
     */
    @Excel(name = "员工编号" ,orderNum = "1")
    @ApiModelProperty("员工编号")
    private String userNo;

    /**
     * 姓名
     */
    @Excel(name = "姓名" ,orderNum = "2")
    @ApiModelProperty("姓名")
    private String userName;

    /**
     * 用户类型:1用户 2管理员
     */
    @ApiModelProperty("用户类型:1用户 2管理员")
    @Excel(name = "用户类型" ,orderNum = "3")
    private String personType;

    /**
     * 职务
     */
    @ApiModelProperty("职务")
    @Excel(name = "职务" ,orderNum = "4")
    private String personRank;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级 0绝密，1机密，2秘密，3内部")
    @Excel(name = "保密等级" ,orderNum = "5")
    private String secretLevel;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位编码")
    @Excel(name = "单位编码" ,orderNum = "6")
    private String orgCode;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称")
    @Excel(name = "单位名称" ,orderNum = "7")
    private String orgName;

    /**
     * 异常原因
     */
    private String reason;
}
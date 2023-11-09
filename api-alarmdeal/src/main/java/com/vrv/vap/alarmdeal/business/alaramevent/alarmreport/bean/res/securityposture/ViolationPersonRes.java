package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/27 10:06
 * @description:
 */
@Data
public class ViolationPersonRes {
    @ApiModelProperty("人员名称")
    private String staffName;

    @ApiModelProperty("部门名称")
    private String staffDepartment;

    @ApiModelProperty("总数")
    private long count;
}

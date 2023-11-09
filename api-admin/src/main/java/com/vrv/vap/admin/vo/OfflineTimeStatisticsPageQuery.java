package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel(value = "离线时长分页查询")
public class OfflineTimeStatisticsPageQuery extends Query {
    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("部门名称")
    private String departmentName;

    @ApiModelProperty(value = "用户编号")
    private String userNo;

    @ApiModelProperty("当前时间")
    private String currentDay;

    @QueryMoreThan
    @Column(name = "currentDay")
    @ApiModelProperty(value = "开始时间")
    private String beginTime;


    @Column(name = "currentDay")
    @ApiModelProperty(value = "结束时间")
    @QueryLessThan
    private String endTime;

    // 1登录 2注销
    @ApiModelProperty(value = "登录类型")
    private Integer loginType;
}

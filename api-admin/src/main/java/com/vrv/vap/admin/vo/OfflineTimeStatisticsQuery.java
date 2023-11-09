package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel(value = "离线时长比统计")
public class OfflineTimeStatisticsQuery {

    @ApiModelProperty(value = "用户编号", required = true)
    private String userNo;

    @QueryMoreThan
    @Column(name = "createTime")
    @ApiModelProperty(value = "开始时间", required = true)
    private String beginTime;


    @Column(name = "createTime")
    @ApiModelProperty(value = "结束时间", required = true)
    @QueryLessThan
    private String endTime;

    // 1登录 2注销
    @ApiModelProperty(value = "登录类型", required = true)
    private Integer loginType;
}

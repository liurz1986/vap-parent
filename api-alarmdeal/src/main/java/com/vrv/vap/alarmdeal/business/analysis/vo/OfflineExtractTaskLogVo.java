package com.vrv.vap.alarmdeal.business.analysis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.jpa.web.page.PageReqVap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lps 2021/8/26
 */
@Data
@ApiModel(value = "离线抽取任务记录Vo")
public class OfflineExtractTaskLogVo extends PageReqVap {


    @ApiModelProperty(value = "guid")
    private String guid;

    @ApiModelProperty(value = "数据源名称")
    private String dataConfigName;

    @ApiModelProperty(value = "执行时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String executeTime;

    @ApiModelProperty(value = "时间筛选范围")
    private String selectTimeRange;

    @ApiModelProperty(value = "时间筛选范围发送的条数")
    private Long selectCount;

    @ApiModelProperty(value = "执行结果")
    private Boolean executeResult;

    @ApiModelProperty(value = "失败原因")
    private String failedResult;

    @ApiModelProperty(value = "对应离线任务的ID")
    private String offlineConfigId;
}

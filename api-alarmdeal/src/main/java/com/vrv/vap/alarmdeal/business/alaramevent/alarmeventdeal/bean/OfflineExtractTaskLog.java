package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author lps 2021/8/26
 */
@Data
@Entity
@Table(name="offline_extract_task_log")
@ApiModel(value = "离线抽取任务记录实体")
public class OfflineExtractTaskLog {

    @ApiModelProperty(value = "guid")
    @Id
    private String guid;

    @ApiModelProperty(value = "数据源名称")
    @Column(name = "data_config_name")
    private String dataConfigName;

    @ApiModelProperty(value = "执行时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "execute_time")
    private String executeTime;

    @ApiModelProperty(value = "时间筛选范围")
    @Column(name = "select_time_range")
    private String selectTimeRange;

    @ApiModelProperty(value = "时间筛选范围发送的条数")
    @Column(name = "select_count")
    private Long selectCount;

    @ApiModelProperty(value = "执行结果")
    @Column(name = "execute_result")
    private Boolean executeResult;

    @ApiModelProperty(value = "失败原因")
    @Column(name = "failed_result")
    private String failedResult;

    @ApiModelProperty(value = "对应离线任务的ID")
    @Column(name = "offline_config_id")
    private String offlineConfigId;



}

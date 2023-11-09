package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lps 2021/8/25
 */

@Data
@Entity
@Table(name="offline_extract_task")
@ApiModel(value = "离线抽取任务实体")
public class OfflineExtractTask {

    @ApiModelProperty(value = "guid")
    @Id
    private String guid;

    @ApiModelProperty(value = "数据源配置名称")
    @Column(name = "data_config_name")
    private  String dataConfigName;

    @ApiModelProperty(value = "数据源(tableName/index)")
    @Column(name = "data_source_name")
    private String dateSourceName;

    @ApiModelProperty(value = "主题名称")
    @Column(name = "topic")
    private String topic;

    @ApiModelProperty(value = "时间过滤字段")
    @Column(name = "time_field")
    private String timeField;

    @ApiModelProperty(value = "事件表名称")
    @Column(name = "event_table_name")
    private String eventTableName;

    @ApiModelProperty(value = "事件表Id")
    @Column(name = "event_table_id")
    private String eventTableId;

    @ApiModelProperty(value = "发送频率")
    @Column(name = "send_frequency")
    private Integer sendFrequency;

    @ApiModelProperty(value = "是否启动")
    @Column(name = "status")
    private Boolean status;

    @ApiModelProperty(value = "备注信息")
    @Column(name = "note")
    private String note;

    @ApiModelProperty(value = "删除标识")
    @Column(name = "delete_flag")
    private Boolean deleteFlag;

    @ApiModelProperty(value = "任务执行周期")
    @Column(name = "task_period")
    private String taskPeriod;

    @ApiModelProperty(value = "定时任务设置")
    @Column(name = "cycle_params")
    private String cycleParams;


    @ApiModelProperty(value = "筛选查询条件(动态)")
    @Column(name = "filter_condition")
    private String filterCondition;

    @ApiModelProperty(value = "下次任务执行时间")
    @Column(name = "next_execute_time")
    private String nextExecuteTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "策略ID")
    @Column(name = "filter_code")
    private String filterCode;



}

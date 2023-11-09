package com.vrv.vap.alarmdeal.business.analysis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author lps 2021/8/26
 */

@Data
@ApiModel(value = "离线抽取任务VO")
public class OfflineExtractTaskVo {

    //一次
    public static final String ONCE="once";
    //每小时
    public static final String EVERY_HOUR="everyHour";
    //每天
    public static final String EVERY_DAY="everyDay";
    //每周
    public static final String EVERY_WEEK="everyWeek";
    //每月
    public static final String EVERY_MONTH="everyMonth";
    //自定义
    public static final String CUSTOM="custom";


    @ApiModelProperty(value = "guid")
    private String guid;

    @ApiModelProperty(value = "数据源配置名称")
    private  String dataConfigName;

    @ApiModelProperty(value = "数据源(tableName/index)")
    private String dateSourceName;

    @ApiModelProperty(value = "主题名称")
    private String topic;

    @ApiModelProperty(value = "时间过滤字段")
    @Column(name = "time_field")
    private String timeField;

    @ApiModelProperty(value = "事件表名称")
    private String eventTableName;

    @ApiModelProperty(value = "事件表Id")
    private String eventTableId;

    @ApiModelProperty(value = "发送频率")
    private Integer sendFrequency;

    @ApiModelProperty(value = "是否启动")
    private Boolean status;

    @ApiModelProperty(value = "备注信息")
    private String note;

    @ApiModelProperty(value = "任务执行周期")
    private String taskPeriod;

    @ApiModelProperty(value = "定时任务设置")
    private String cycleParams;

    @ApiModelProperty(value = "筛选查询条件")
    private String filterCondition;

    @ApiModelProperty(value = "执行记录")
    private Long  executeCount;

    @ApiModelProperty(value = "下次任务执行时间")
    private String nextExecuteTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;





}

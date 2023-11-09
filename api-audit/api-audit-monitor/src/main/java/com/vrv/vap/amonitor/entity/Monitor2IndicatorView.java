package com.vrv.vap.amonitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.amonitor.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * <p>
 * V2-指标展示面板
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "Monitor2IndicatorView对象", description = "V2-指标展示面板")
public class Monitor2IndicatorView extends PageModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "指标标识(多个逗号分割)")
    private String indicators;

    @ApiModelProperty(value = "指标名称(内存占比/cpu利用率/磁盘等)")
    private String indicatorName;

    @ApiModelProperty(value = "展示形式(val=值/trend=趋势/list=列表/disk=磁盘)")
    private String viewType;

    @ApiModelProperty(value = "展示标题")
    private String viewTitle;
//
//    @ApiModelProperty(value = "接口地址")
//    private String api;

    @ApiModelProperty(value = "参数说明")
    private String paramDesc;

    @ApiModelProperty(value = "数据示例")
    private String dataSample;

    @ApiModelProperty(value = "添加时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
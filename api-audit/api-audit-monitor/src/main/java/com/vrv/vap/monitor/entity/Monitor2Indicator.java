package com.vrv.vap.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.monitor.model.PageModel;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * <p>
 * V2-指标
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "Monitor2Indicator对象", description = "V2-指标")
public class Monitor2Indicator extends PageModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "指标名称(内存占比/cpu利用率/磁盘等)")
    private String indicatorName;

    @ApiModelProperty(value = "指标分类")
    private String indicatorType;

    @ApiModelProperty(value = "指标标识/存储字段")
    private String indicatorField;

    @ApiModelProperty(value = "es存储字段类型")
    private String esType;

    @ApiModelProperty(value = "数据类型")
    private String dataType;

    @ApiModelProperty(value = "支持的面板类型")
    private String supportView;

    @ApiModelProperty(value = "单位(例如G,%,个等,可为空)")
    private String unit;

    @ApiModelProperty(value = "字典转义")
    private String dict;

    @ApiModelProperty(value = "是否可用")
    private Integer available;

    @ApiModelProperty(value = "实时查询(1=实时查询,0=周期采集)")
    private int realQuery;

    @ApiModelProperty(value = "添加时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}

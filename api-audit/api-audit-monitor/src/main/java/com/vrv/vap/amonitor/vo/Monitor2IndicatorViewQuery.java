package com.vrv.vap.amonitor.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

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
public class Monitor2IndicatorViewQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "指标标识(多个逗号分割)")
    private String indicators;

    @ApiModelProperty(value = "指标名称(内存占比/cpu利用率/磁盘等)")
    private String indicatorName;

    @ApiModelProperty(value = "展示形式(数值/趋势/列表/磁盘)")
    private String viewType;

    @ApiModelProperty(value = "展示标题")
    private String viewTitle;

    @ApiModelProperty(value = "接口地址")
    private String api;

    @ApiModelProperty(value = "参数说明")
    private String paramDesc;

}

package com.vrv.vap.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.monitor.model.PageModel;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

/**
 * <p>
 * V2-指标算法
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "Monitor2AssetOidAlg对象", description = "V2-指标算法")
public class Monitor2AssetOidAlg extends PageModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "资产类别")
    private String assetType;

    @ApiModelProperty(value = "sno唯一编码")
    private String snoUnicode;

    @ApiModelProperty(value = "资产类别sno描述")
    private String snoUnicodeDesc;

    @ApiModelProperty(value = "oid(0到多个)")
    private String oid;

    @ApiModelProperty(value = "指标名称")
    private String indicatorName;

    @ApiModelProperty(value = "指标标识/存储字段")
    private String indicatorField;

    @ApiModelProperty(value = "指标分类")
    @TableField(exist = false)
    private String indicatorType;

    @ApiModelProperty(value = "算法类型(公式/正则/取值/列表)")
    private String algorithmType;

    @ApiModelProperty(value = "算法")
    private String algo;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "测试结果")
    private String testRes;

    @ApiModelProperty(value = "是否可用")
    private Integer available;

    @ApiModelProperty(value = "实时查询(1=实时查询,0=周期采集)")
    private Integer realQuery;

    @ApiModelProperty(value = "添加时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}

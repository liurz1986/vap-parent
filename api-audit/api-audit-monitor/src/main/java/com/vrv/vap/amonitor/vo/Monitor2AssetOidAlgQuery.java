package com.vrv.vap.amonitor.vo;

import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

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
public class Monitor2AssetOidAlgQuery extends Query {

    private Integer id;

    @ApiModelProperty(value = "资产类别")
    private String assetType;

    @ApiModelProperty(value = "sno唯一编码")
    private String snoUnicode;

    @ApiModelProperty(value = "资产类别sno描述")
    private String snoUnicodeDesc;

    @ApiModelProperty(value = "oid(0到多个)")
    private String oid;

    @ApiModelProperty(value = "指标id")
    private Integer indicatorId;

    @ApiModelProperty(value = "指标名称")
    private String indicatorName;

    @ApiModelProperty(value = "指标分类")
    private String indicatorType;

    @ApiModelProperty(value = "存储字段")
    private String indicatorField;

    @ApiModelProperty(value = "算法类型(公式/正则/取值/列表)")
    private String algorithmType;

    @ApiModelProperty(value = "算法")
    private String algo;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String remark;

    @ApiModelProperty(value = "是否可用")
    private Integer available;

    @ApiModelProperty(value = "实时查询(1=实时查询,0=周期采集)")
    private Integer realQuery;

}

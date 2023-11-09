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
@ApiModel(value = "Monitor2AssetTyp对象", description = "V2-指标算法分类")
public class Monitor2AssetTypeQuery extends Query {

    private String guid;

    @ApiModelProperty(value = "资产类别")
    private String assetType;

    @ApiModelProperty(value = "sno唯一编码")
    private String uniqueCode;


    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "treeCode")
    private String treeCode;

    @ApiModelProperty(value = "iconCls")
    private String iconCls;

    @ApiModelProperty(value = "parentTreeCode")
    private String parentTreeCode;


    @ApiModelProperty(value = "type_level")
    private String typeLevel;

}

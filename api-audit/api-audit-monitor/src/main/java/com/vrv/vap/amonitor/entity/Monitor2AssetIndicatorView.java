package com.vrv.vap.amonitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.amonitor.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * V2-资产类型关联展示指标
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "Monitor2AssetIndicatorView对象", description = "V2-资产类型关联展示指标")
public class Monitor2AssetIndicatorView extends PageModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "资产类别")
    private String assetType;

//    @ApiModelProperty(value = "资产类别-子类")
//    private String assetTypeSnoGuid;

    @ApiModelProperty(value = "sno唯一编码")
    private String snoUnicode;

    @ApiModelProperty(value = "页面布局配置json")
    private String layoutSetting;

    @ApiModelProperty(value = "页面展示配置json")
    private String viewSetting;

}

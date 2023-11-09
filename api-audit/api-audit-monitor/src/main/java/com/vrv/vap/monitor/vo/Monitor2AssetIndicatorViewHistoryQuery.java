package com.vrv.vap.monitor.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * V2-资产类型监控页面配置历史
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "Monitor2AssetIndicatorViewHistoryQuery查询对象", description = "V2-资产类型监控页面配置历史")
public class Monitor2AssetIndicatorViewHistoryQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer assetIndicatorViewId;

    @ApiModelProperty(value = "资产类别")
    private String assetType;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "sno唯一编码")
    private String snoUnicode;

    @ApiModelProperty(value = "页面布局配置json")
    private String layoutSetting;

    @ApiModelProperty(value = "页面展示配置json")
    private String viewSetting;

}

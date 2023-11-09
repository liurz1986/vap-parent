package com.vrv.vap.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.monitor.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

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
@ApiModel(value = "Monitor2AssetIndicatorViewHistory对象", description = "V2-资产类型监控页面配置历史")
public class Monitor2AssetIndicatorViewHistory extends PageModel {

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

    @ApiModelProperty(value = "添加时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}

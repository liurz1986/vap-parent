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
 * V2-资产监控表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "Monitor2AssetInfo对象", description = "V2-资产监控表")
public class Monitor2AssetInfoQuery extends Query {

    private Integer id;

    @ApiModelProperty(value = "设备id")
    private String devId;

    @ApiModelProperty(value = "设备名称")
    private String devName;

    @ApiModelProperty(value = "设备IP")
    private String devIp;

    @ApiModelProperty(value = "预留字段-设备其他信息")
    private String otherInfo;

    @ApiModelProperty(value = "资产类别")
    private String assetType;

//    @ApiModelProperty(value = "资产类别-子类")
//    private String assetTypeSnoGuid;

    @ApiModelProperty(value = "sno唯一编码")
    private String snoUnicode;

    @ApiModelProperty(value = "监控协议")
    private String monitorProtocol;

    @ApiModelProperty(value = "监控连接配置")
    private String monitorSetting;

    @ApiModelProperty(value = "页面展示配置json")
    private String viewSetting;

    @ApiModelProperty(value = "显示的指标及其展示形式")
    private String indicatorViews;

}

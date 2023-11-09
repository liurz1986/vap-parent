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

import java.time.LocalDateTime;
import java.util.Map;

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
public class Monitor2AssetInfo extends PageModel {

    @TableId(value = "id", type = IdType.AUTO)
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

    @ApiModelProperty(value = "启用状态:1启动监控")
    private Integer startupState;

    @ApiModelProperty(value = "连通状态:1=连通")
    private Integer connectState;

    @ApiModelProperty(value = "添加时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "实时监控信息(非表字段)")
    @TableField(exist = false)
    private Map<String, Object> realInfo;

}

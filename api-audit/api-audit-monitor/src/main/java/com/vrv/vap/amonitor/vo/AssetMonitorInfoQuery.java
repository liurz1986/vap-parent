package com.vrv.vap.amonitor.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-08-31
 */
@Data
@ApiModel(value = "AssetMonitorInfo对象", description = "")
public class AssetMonitorInfoQuery extends Query {

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

    @ApiModelProperty(value = "资产类别-子类")
    private String assetTypeSnoGuid;

    @ApiModelProperty(value = "监控协议")
    private String monitorProtocol;

    @ApiModelProperty(value = "团体名")
    private String communityName;

    @ApiModelProperty(value = "用户")
    private String user;

    private String authPassphrase;

    private String privacyPassphrase;

    @ApiModelProperty(value = "启用状态:1启动监控")
    private Integer startupState;

    @ApiModelProperty(value = "连通状态:1=连通")
    private Integer connectState;

    @ApiModelProperty(value = "添加时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

}

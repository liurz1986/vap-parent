package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 当前所有字段值名称与手动输入策略配置对比字段key是一致的，不要轻易更新里面的名称
 *  2023-4
 */
@Data
public class AssetBookDiffColumnsVO {
    @ApiModelProperty(value="类型")
    private String typeGuid; // 二级资产类型Guid，对应AssetVerify和Asset中的是assetType
    @ApiModelProperty(value="小类")
    private String  deviceDesc;// 设备类型(小类)
    @ApiModelProperty(value="设备编号")
    private String  assetNum;// 设备编号
    @ApiModelProperty(value="名称")
    private String name; //资产名称
    @ApiModelProperty(value="责任人")
    private String responsibleName; // 责任人名称
    @ApiModelProperty(value="IP地址")
    private String ip; //ip地址
    @ApiModelProperty(value="MAC地址")
    private String mac; //mac地址
    @ApiModelProperty(value="部门")
    private String orgName; // 组织机构名称(单位、部门)
    @ApiModelProperty(value="启用时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;// 启用时间
    @ApiModelProperty(value="设备序列号")
    private String serialNumber; // 序列号
    @ApiModelProperty(value="密级")
    private String equipmentIntensive;//  涉密等级
    @ApiModelProperty(value="位置")
    private String location;// 位置
    @ApiModelProperty(value="磁盘序列号")
    private String  extendDiskNumber; //磁盘序列号
    @ApiModelProperty(value="操作系统版本")
    private String osList;// 安装操作系统
    @ApiModelProperty(value="系统安装时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date osSetupTime;// 操作系统安装时间
    @ApiModelProperty(value="品牌型号")
    private String  typeSnoGuid;// 品牌型号
    @ApiModelProperty(value="备注")
    private String remarkInfo;// 备注
    @ApiModelProperty(value="架构")
    private String deviceArch;
}

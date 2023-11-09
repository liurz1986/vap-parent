package com.vrv.vap.alarmdeal.business.asset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FailedAssetVO {
    @ApiModelProperty("资产guid")
	private String guid;
    @ApiModelProperty("资产名称")
	private String name;
    @ApiModelProperty("资产英文名称")
	private String nameEn;
    
    @ApiModelProperty("资产IP")
	private String ip;
    
    @ApiModelProperty("应用系统名称")
	private String appName; 
    @ApiModelProperty("应用系统id")
	private String appId; 
    
    @ApiModelProperty("安全域Guid")
	private String securityGuid;
    @ApiModelProperty("安全域名称")
	private String securityName;
    
    @ApiModelProperty("失陷状态")
	private String failedStatus;
    @ApiModelProperty("失陷日期")
	private String updateTime;
    
}

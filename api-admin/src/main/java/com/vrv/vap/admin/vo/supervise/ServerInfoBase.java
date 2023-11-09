package com.vrv.vap.admin.vo.supervise;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ServerInfoBase {

	@ApiModelProperty("单位名称")
	private String orgName;

	@ApiModelProperty("单位类型")
	private Integer orgType;
	
	@ApiModelProperty("保密资质")
	private String secretQualification;

	@ApiModelProperty("涉密网运行许可证编号")
	String netLicenseNumber;

	@ApiModelProperty("涉密网综合审计监管系统证书编号")
	String zjgLicenseNumber;

	@ApiModelProperty("监测器证书编号")
	String jcqLicenseNumber;
	
	@ApiModelProperty("上级服务器ip")
	private String rootIp;
	
	@ApiModelProperty("上级服务器端口")
	private String rootPort;

	@ApiModelProperty("系统编码")
	String clientId;

	@ApiModelProperty("安全凭证")
	String clientSecret;

	public String getRootUrl() {

		return rootIp + ":" + rootPort;
	}
}

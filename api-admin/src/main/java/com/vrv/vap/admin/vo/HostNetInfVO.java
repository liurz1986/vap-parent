package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostNetInfVO {
	
	@NotBlank(message = "网卡名称必填")
	private String name;
	
	@NotBlank(message = "IP必填")
	private String ip;
	
	private String mac;
	
	@NotBlank(message = "子网掩码不可为空")
	private String submask;
	@NotBlank(message = "网关不可为空")
	private String gateway;
	
	@NotBlank(message = "是否默认路由必填（yes/no）")
	private String defroute;
	@NotBlank(message = "是否是主IP必填（true/false）")
	private String flag; //是否是主IP
	
	private String dns1;
	
	private String dns2;
}

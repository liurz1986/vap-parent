package com.vrv.vap.admin.vo.supervise;

import lombok.Data;

@Data
public class ServerInfo extends ServerInfoBase {

	
	private Boolean isRegister;
	private String registerDescript;
	
	private Integer registerType;//0 未注册 1 在线 2离线

}


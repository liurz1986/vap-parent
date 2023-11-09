package com.vrv.vap.admin.service;

import com.vrv.vap.admin.vo.supervise.*;

import javax.servlet.http.HttpServletRequest;

public interface SuperviseService {
	ServerRegisterResult serverRegister(ServerInfoBase info );
	
	PutServerStatusResult reportServerStatus(ServerStatus status);
	
	PutServerDataResult reportBusinessEventData(ServerData data );
	
	ServerInfo  getServerInfo(HttpServletRequest request);
	
	boolean  saveServerInfo(ServerInfo info);

	int updateRegister(ServerInfoBase baseInfo);

	void superviseAnnounce(AnnounceDataInfo info );

	OAuth2ClientKey getClientKey();
}

package com.vrv.vap.admin.vo.supervise;

import java.util.Date;

import com.vrv.vap.admin.model.SuperviseStatusSubmit;

import lombok.Data;

@Data
public class ServerStatus {

	Date updateTime;
	Integer ssaRunState;
	
	public static ServerStatus getServerStatus(SuperviseStatusSubmit status)
	{
		ServerStatus state=new ServerStatus();
		state.setUpdateTime(status.getUpdateTime());
		state.setSsaRunState(status.getRunState());
		return state;
	}
	
}

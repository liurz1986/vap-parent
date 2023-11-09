package com.vrv.vap.alarmdeal.business.analysis.server.tabInfo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.analysis.server.tabInfo.AlarmStatusChangeSubject;
import com.vrv.vap.alarmdeal.business.analysis.server.tabInfo.StatusChangeObserver;

import lombok.Getter;
import lombok.Setter;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月10日 下午4:24:25 
* 类说明 
*/
@Service
public class AlarmStatusChangeSubjectImpl implements AlarmStatusChangeSubject {

	@Getter
	@Setter
	private  List<StatusChangeObserver> list = new ArrayList<>();

	@Override
	public void addObserver(StatusChangeObserver statusChangeObserver) {
		list.add(statusChangeObserver);
	}

	@Override
	public void notice(String beforeStatus, String afterStatus, WarnResultLogTmpVO warnResult) {
		for (StatusChangeObserver statusChangeObserver : list) {
			statusChangeObserver.stateChgListener(beforeStatus, afterStatus, warnResult);
		}
	}
	
}

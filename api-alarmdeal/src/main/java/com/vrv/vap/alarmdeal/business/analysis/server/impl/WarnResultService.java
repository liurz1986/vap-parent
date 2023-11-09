package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmModel.model.WarnResult;
import com.vrv.vap.alarmdeal.business.analysis.repository.WarnResultRespository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;

/**
 * 告警数据业务类（数字政府专用）
 * @author wd-pc
 *
 */
@Service
public class WarnResultService extends BaseServiceImpl<WarnResult, String> {

	@Autowired
	private WarnResultRespository warnResultRespository;
	
	@Override
	public WarnResultRespository getRepository() {
		return warnResultRespository;
	}

	
	
	
	
	
	
	
	
}

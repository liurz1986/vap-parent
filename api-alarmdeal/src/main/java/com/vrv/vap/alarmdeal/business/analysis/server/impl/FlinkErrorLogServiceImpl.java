package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FlinkRunningTimeErrorLog;
import com.vrv.vap.alarmdeal.business.analysis.repository.FlinkErrorLogRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.FlinkErrorLogService;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
/**
 * flink错误日志记录
 * @author Administrator
 *
 */
@Service
public class FlinkErrorLogServiceImpl extends BaseServiceImpl<FlinkRunningTimeErrorLog, String> implements FlinkErrorLogService{

	@Autowired
	private FlinkErrorLogRespository flinkErrorLogRespository;
	
	@Override
	public FlinkErrorLogRespository getRepository() {
		return flinkErrorLogRespository;
	}

	

}

package com.vrv.vap.alarmdeal.business.analysis.server;

import com.vrv.vap.alarmdeal.business.analysis.model.DealCommonLog;
import com.vrv.vap.jpa.baseservice.BaseService;

public interface DealCommonLogService extends BaseService<DealCommonLog, String> {
     
	/**
	 * 添加告警处置日志
	 * @param alarmItemGuid
	 * @param instanceId
	 * @param jsonInfo
	 * @param type
	 */
	public void addDealLog(String alarmItemGuid, String instanceId,String jsonInfo, String type); 
}

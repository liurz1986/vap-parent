package com.vrv.vap.alarmdeal.business.analysis.server.core.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmCommandVO;

public interface DealCommand {
    
	/**
	 * 通过告警处置Id来执行对应的行为
	 * @param alarmCommandVO
	 */
	public void executeCommand(AlarmCommandVO alarmCommandVO);
	
	
}

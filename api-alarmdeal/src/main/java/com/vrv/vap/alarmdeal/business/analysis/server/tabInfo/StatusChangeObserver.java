package com.vrv.vap.alarmdeal.business.analysis.server.tabInfo;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月10日 下午4:14:30 
* 类说明   标签观察者
*/
public interface StatusChangeObserver {

	/**
	 * 状态变更接口
	 * @param beforeState
	 * @param afterState
	 * @param warnResult
	 */
	public void stateChgListener(String beforeState,String afterState,WarnResultLogTmpVO warnResult);
}

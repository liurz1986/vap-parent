package com.vrv.vap.alarmdeal.business.analysis.server.tabInfo;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月10日 下午4:13:38 
* 类说明 
*/
public interface AlarmStatusChangeSubject {
   
	/**
	 * 添加观察者
	 * @param statusChangeObserver
	 */
	public void addObserver(StatusChangeObserver statusChangeObserver);
	
	/**
	 * 通知对应观察者
	 * @param beforeStatus
	 * @param afterStatus
	 * @param warnResult
	 */
    public void  notice(String beforeStatus,String afterStatus,WarnResultLogTmpVO warnResult);
	
	
}

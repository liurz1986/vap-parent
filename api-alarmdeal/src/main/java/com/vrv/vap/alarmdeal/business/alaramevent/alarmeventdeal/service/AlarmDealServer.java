package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service;

import java.util.List;
import java.util.Map;

import com.vrv.vap.alarmdeal.business.analysis.vo.UpdateAlarmDealVO;
import org.springframework.data.domain.Pageable;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmDeal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmCommandVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmDealVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealInfoVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealTaskstaticVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealWayVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;

public interface AlarmDealServer extends BaseService<AlarmDeal, String> {
     
	/**
	 * 获得告警处置对应的
	 * @param alarmDealVO
	 * @param pageable
	 * @return
	 */
	public PageRes<AlarmDeal> getAlarmDealPager(AlarmDealVO alarmDealVO,Pageable pageable);
	
	/**
	 * 判断下发状态
	 * @param alarmDealId
	 * @return
	 */
	public String judgeAlarmStatus(String alarmDealId); 
	/**
	 * 更新AlarmDeal
	 * @param updateAlarmDealVO
	 */
	public void updateAlarmDeal(UpdateAlarmDealVO updateAlarmDealVO);
	
	/**
	 * 下发处置
	 * @param alarmCommandVO
	 * @return
	 */
	public Result<Boolean> issueAlarm(AlarmCommandVO alarmCommandVO);
	
	/**
	 * 重复处置下发
	 * @param list
	 * @return
	 */
	public Result<Boolean> issueRepeatAlarm(List<AlarmCommandVO> list);
	
	
	public Result<String> saveAlarmDeal(DealWayVO dealWayVO);
	
	/**
	 * 获得时间段已处置的告警
	 * @param riskEventName
	 * @param nowDay
	 * @param beforeMouthDay
	 * @return
	 */
	public List<Map<String, Object>> getDealedAlarm(String riskEventName, String nowDay, String beforeMouthDay); 
	
	
	public List<Map<String, Object>> getDealedAlarmByUser(String userId,String riskEventName, String nowDay, String beforeMouthDay); 
	
	
	
	/**
	 * 获得处置人员
	 * @param beforesixMouthDays
	 * @param nowDays
	 * @return
	 */
	public List<Map<String, Object>> getDealitemPeople(String beforesixMouthDays, String nowDays);
	
	/**
	 * 通过状态获得处置的告警数
	 * @return
	 */
	public List<Map<String, Object>> getDealAlarmCountByStatus(String beforeSixMouth,String nowdayMouth,String peopleId,String status);

	/**
	 * 添加告警处置
	 * @param dealInfo
	 * @return
	 */
	Result<String> alarmDeal(DealInfoVO dealInfo);
	
	/**
	 * 自动转工单
	 * @param warnResult
	 * @return
	 */
	public WarnResultLogTmpVO alarmDealByAuto(WarnResultLogTmpVO warnResult);
	 /**
	  * 状态改变监听
	  * @param warnResult
	  */
	public void statusChangeListener(WarnResultLogTmpVO warnResult,String afterStatus);
	
	/**
	 * 获得告警处置人信息
	 * @return
	 */
	public List<DealTaskstaticVO> getDealTaskAssignList();
	
	
}

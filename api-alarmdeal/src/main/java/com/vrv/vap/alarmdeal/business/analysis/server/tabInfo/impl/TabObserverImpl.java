package com.vrv.vap.alarmdeal.business.analysis.server.tabInfo.impl;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealTypeEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.analysis.server.tabInfo.StatusChangeObserver;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.LabelConf;
import com.vrv.vap.alarmdeal.frameworks.feign.AuditFeign;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月10日 下午4:57:49 
* 类说明  打便签实现类
*/
@Service("tabObserver")
public class TabObserverImpl implements StatusChangeObserver {

	private static final Integer attack_label = 97; //攻击者标签编号
	private static final Integer be_attacked_label = 98; //被攻击者标签编号
	private static Logger logger = LoggerFactory.getLogger(TabObserverImpl.class);
	
	@Autowired
	private AuditFeign auditFeign;
	@Autowired
	private RiskEventRuleService riskEventRuleService;
	@Autowired
	private EventCategoryService eventCategoryService;
	
	@Override
	public void stateChgListener(String beforeState, String afterState, WarnResultLogTmpVO warnResult) {
       		if(afterState.equals(String.valueOf(AlarmDealTypeEnum.ALREADY_DEAL.getIndex()))) {
       			markEventLabel(warnResult); 
       		}
	}

	/**
	 * 打事件标签
	 * @param warnResult
	 */
	private void markEventLabel(WarnResultLogTmpVO warnResult) {
		try {
			ResultObjVO<List<LabelConf>> resultObjVO = getLableList(warnResult);
			List<LabelConf> list = resultObjVO.getList();
			if(list==null || list.size()==0){
				logger.warn(warnResult.getRiskEventName()+"标签为空");
			}else{
				labelMark(warnResult, list);
				markAttackLabel(warnResult);
			}       				
		}catch(Exception e) {
			logger.error("label调用feign接口失败", e);
		}
	}
    
	/**
	 * 打标签
	 * @param warnResult
	 * @param list
	 */
	private void labelMark(WarnResultLogTmpVO warnResult, List<LabelConf> list) {
		for (LabelConf labelConf : list){
			Integer id = labelConf.getId();
			Map<String,Object> map = new HashMap<>();
			map.put("labelConfId",id);
			map.put("objKey", warnResult.getRelatedIps());
			map.put("objType", 1);
			map.put("type", 1);
			Result labelmark = auditFeign.labelmark(map);
			logger.info(warnResult.getRiskEventName()+":"+labelmark.getMessage());				
		}
	}

	
	/**
	 * 打攻击者标签
	 * @param warnResult
	 */
	private void markAttackLabel(WarnResultLogTmpVO warnResult) {
		String rulecode = warnResult.getRuleCode();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ruleCode", rulecode));
		List<RiskEventRule> list = riskEventRuleService.findAll(conditions);
		if(list.size()>0){
			Boolean result = list.get(0).getAttack_event();
			if(result){
				String src_ips = warnResult.getSrc_ips(); //源目标IP
				String dstIps = warnResult.getDstIps(); //目的IP
				markAttackAction(src_ips,attack_label);
				markAttackAction(dstIps, be_attacked_label);
			}
			
		}
	}

	/**
	 * 攻击事件操作
	 * @param ips
	 */
	private void markAttackAction(String ip,Integer label_type) {
		if(StringUtils.isEmpty(ip)){
			Map<String,Object> map = new HashMap<>();
			map.put("labelConfId",label_type);
			map.put("objKey", ip);
			map.put("objType", 1);
			map.put("type", 1);
			Result labelmark = auditFeign.labelmark(map);
			logger.info("label_type:"+label_type+":"+labelmark.getMessage());	
		}
	}
	
	
	/**
	 * 获得便签集合
	 * @param warnResult
	 * @return
	 */
	private ResultObjVO<List<LabelConf>> getLableList(WarnResultLogTmpVO warnResult) {
		ResultObjVO<List<LabelConf>> resultObjVO = new ResultObjVO<>();
		String ruleCode = warnResult.getRuleCode();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ruleCode", ruleCode));
	    List<RiskEventRule> list = riskEventRuleService.findAll(conditions);
		if(list.size()>0){
			String riskEventId = list.get(0).getRiskEventId();
			EventCategory eventCategory = eventCategoryService.getOne(riskEventId);
			if(eventCategory!=null){
				String title = eventCategory.getTitle();
				String parentId = eventCategory.getParentId();
				EventCategory parentEventCategory = eventCategoryService.getOne(parentId);
				if(parentEventCategory!=null){
					String parentTitle = parentEventCategory.getTitle();
					Map<String,Object> map = new HashMap<>();
					map.put("name", parentTitle+"-"+title);
					resultObjVO = auditFeign.label(map);       										
				}
			}
		}
		return resultObjVO;
	}

}

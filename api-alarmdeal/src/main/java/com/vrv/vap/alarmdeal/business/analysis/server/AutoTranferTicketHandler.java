package com.vrv.vap.alarmdeal.business.analysis.server;

import java.util.ArrayList;
import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmDealServer;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.vo.AlarmEventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.AlarmDealSenderMQ;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.jpa.common.ArrayUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月9日 下午6:43:07 
* 类说明  自动转工单
*/
@Component
public class AutoTranferTicketHandler {

	public static final String ILLEAGE_RELATE = "违规外联"; //事件分类
	public static final String SPREAD_VIRUS = "计算机病毒";//
	private static Logger logger = LoggerFactory.getLogger(AutoTranferTicketHandler.class);
	
     @Autowired
     private RiskEventRuleService riskEventRuleService;
     @Autowired
     private EventCategoryService eventCategoryService;
	 @Autowired
	 private AlarmDealSenderMQ  alarmDealSenderMQ;
	 @Autowired
	 private AlarmDealServer alarmDealServer;
	 @Autowired
	 private TbConfService tbService;
	 private static final String VIRUS_RULE_INFO = "virus_rule_info"; //病毒规则 
	 private static final String ILLEDGE_RULE_INFO = "illedge_rule_info"; //违规外联规则
	/**
	 * 自动转工单 
	 * @param warnResult
	 */
	public void tranferTicket(WarnResultLogTmpVO warnResult) {
		String ruleCode = warnResult.getRuleCode();
		TbConf virus_tbConf = tbService.getOne(VIRUS_RULE_INFO);
		TbConf illedge_tbConf = tbService.getOne(ILLEDGE_RULE_INFO);
		String virus_value = virus_tbConf.getValue();
		String illedge_value = illedge_tbConf.getValue();
		List<String> virus_value_list = ArrayUtil.strToList(virus_value, ",");
		List<String> illedge_value_list = ArrayUtil.strToList(illedge_value, ",");
		//String eventTypeName = getEventTypeName(warnResult);       
		if(virus_value_list.contains(ruleCode) || illedge_value_list.contains(ruleCode)){
			alarmEventSender(warnResult);
			warnResult = alarmDealServer.alarmDealByAuto(warnResult);
		}
		
	}
	/**
	 * 发送消息
	 * @param warnResult
	 */
	private void alarmEventSender(WarnResultLogTmpVO warnResult){
		String eventTypeName = getEventTypeName(warnResult);
		AlarmEventVO alarmEventVO = new AlarmEventVO();
		alarmEventVO.setAlarmId(warnResult.getId());
		alarmEventVO.setAlarmIp(warnResult.getRelatedIps());
		alarmEventVO.setEventType(eventTypeName);
		alarmEventVO.setName(warnResult.getRiskEventName());
		alarmEventVO.setTicketNum(UUIDUtils.get32UUID());
		alarmDealSenderMQ.push(alarmEventVO);
	}
	
	/**
	 * 获得事件类型名称
	 * @return
	 */
	private String getEventTypeName(WarnResultLogTmpVO warnResult){
		String title = null;
		String ruleCode = warnResult.getRuleCode();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ruleCode", ruleCode)); // ruleCode是唯一的
		List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(conditions);
		if(riskEventRuleList.size()==1){
			RiskEventRule riskEventRule = riskEventRuleList.get(0);
			String risk_id = riskEventRule.getRiskEventId();
			EventCategory eventCategory = eventCategoryService.getOne(risk_id);
			if(eventCategory!=null){
				title = eventCategory.getTitle();
			}
		}
		return title;
	}



	
	
}

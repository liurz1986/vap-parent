package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.dao.impl.AlarmAnalysisDao;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.SrcIpScore;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.repository.SrcIpScoreRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AlarmScoreVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月22日 上午10:36:31 
* 类说明    告警评分
*/
@Service
public class SrcIpScoreService extends BaseServiceImpl<SrcIpScore, String> {

	private static final Logger logger = LoggerFactory.getLogger(SrcIpScoreService.class);
	
	@Autowired
	private SrcIpScoreRespository srcIpScoreRespository;
	
	@Autowired
	private WarnResultForESService warnResultForESService;
	@Autowired
	private RiskEventRuleService riskEventRuleService;
	@Autowired
	private AlarmAnalysisDao alarmAnalysisDao;
	
	
	@Override
	public SrcIpScoreRespository getRepository() {
		return srcIpScoreRespository;
	}
	
	
	/**
	 * 计算每个IP对应规则的计算分值
	 * @param map
	 * @return
	 */
	public List<AlarmScoreVO> calculateAlarmScore(Map<String,Object> map){
		List<AlarmScoreVO> list = new ArrayList<>();
		List<Map<String,Object>> alarmScoreBySrcIp = warnResultForESService.getAlarmScoreBySrcIp(map);
		for (Map<String, Object> map2 : alarmScoreBySrcIp) {
			Object ruleId_obj = map2.get("ruleId");
			Object count_obj = map2.get("doc_count");
			if(ruleId_obj!=null&&count_obj!=null){
				AlarmScoreVO alarmScoreVO = new AlarmScoreVO();
				String ruleId = ruleId_obj.toString();
				Long doc_count = (Long)count_obj;
				RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
				String name = riskEventRule.getName();
				Float unitScore = riskEventRule.getUnitScore();
				Float maxScore = riskEventRule.getMaxScore();
				Float rule_score=unitScore*doc_count>maxScore ? maxScore:unitScore*doc_count;
				alarmScoreVO.setRuleId(ruleId);
				alarmScoreVO.setRuleName(name);
				alarmScoreVO.setScore(rule_score);
			    list.add(alarmScoreVO);	
			}
		}
		return list;
	}
	
	
	/**
	 * 计算昨日SrcIp产生的分值
	 */
    public void calculateSrcIpScore(){
    	Date pre_date = DateUtil.addDay(new Date(), -1);
    	String date_str = DateUtil.format(pre_date, DateUtil.Year_Mouth_Day);
    	String start_time = date_str+" 00:00:00";
    	String end_time = date_str+" 23:59:59";
    	Map<String,Object> map = new HashMap<>();
    	map.put("start_time", start_time);
    	map.put("end_time", end_time);
    	List<Map<String,Object>> list = warnResultForESService.getAlarmInfoBySrcIpAndRuleId(map);
    	for (Map<String, Object> src_map : list) {
    		Object scr_ip_obj = src_map.get("src_ips");
    		Object ruleId_obj = src_map.get("ruleId");
    		if(scr_ip_obj!=null&&ruleId_obj!=null){
    			String src_ips = scr_ip_obj.toString();  //源IP
    			List<Map<String,Object>> ruleId_list = (List<Map<String,Object>>)ruleId_obj; //规则Id
    			float rule_score_total = (float) 0.0;
    			for (Map<String, Object> rule_map : ruleId_list) {
    				Object doc_count_obj=rule_map.get("doc_count");
					Object rule_obj=rule_map.get("ruleId");
					if(doc_count_obj!=null&&rule_obj!=null){
						String rule = rule_obj.toString();
						Long doc_count = (Long)doc_count_obj;
						RiskEventRule riskEventRule = riskEventRuleService.getOne(rule);
						Float unitScore = riskEventRule.getUnitScore();
						Float maxScore = riskEventRule.getMaxScore();
						Float rule_score=unitScore*doc_count>maxScore ? maxScore:unitScore*doc_count;
						rule_score_total+=rule_score;
					}
				}
    			SrcIpScore srcIpScore = new SrcIpScore();
    			srcIpScore.setGuid(UUID.randomUUID().toString());
    			srcIpScore.setScore_value(rule_score_total);
    			logger.info(src_ips+":"+"告警规则总评分："+rule_score_total);
    			srcIpScore.setSrcIp(src_ips);
    			srcIpScore.setCreate_date(new Date());
    			save(srcIpScore);
    			
    		}
		}
    	
    }
	
    /**
     * 获得告警的最新分数
     * @return
     */
	public Integer getSumAlarmScore(String ip) {
    	String date_str = DateUtil.format(new Date(), DateUtil.Year_Mouth_Day);
    	String start_time = date_str+" 00:00:00";
    	String end_time = date_str+" 23:59:59";
    	Integer sum = alarmAnalysisDao.getSumAlarmScore(ip,start_time, end_time);
		return sum;
	}
	
	/**
	 * 告警评分一周趋势图
	 * @return
	 */
	public List<Map<String, Object>> getAlarmScoreTrend(String ip) {
		Date pre_date = DateUtil.addDay(new Date(), -7);
    	String start_time = DateUtil.format(pre_date, DateUtil.Year_Mouth_Day);
    	String end_time = DateUtil.format(new Date(), DateUtil.Year_Mouth_Day);
    	List<Map<String, Object>> list = alarmAnalysisDao.getAlarmScoreTrend(ip,start_time, end_time);
		List<String> list2 = DateUtil.getDatesBetweenDays(start_time, end_time, DateUtil.Year_Mouth_Day);
		SocUtil.completionUtil(list, list2, "create_date", "sum");
    	return list;
	}
	
	
	
}

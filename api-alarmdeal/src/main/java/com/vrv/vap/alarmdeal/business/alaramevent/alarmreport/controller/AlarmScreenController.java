package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.req.RequestBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.AlarmDistributionRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.AlarmEventRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.AlarmEventRankRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.EventLevelTotalRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.EventTrendRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res.securityposture.ViolationPersonRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AlarmScreenService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.BaseSysinfoService;
import com.vrv.vap.alarmdeal.business.analysis.vo.DomainWideVulVO;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.controller.AnalysisResultController;
import com.vrv.vap.alarmdeal.frameworks.config.EventConfig;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.WideVulVO;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alarmScreen")
@Api(description="告警大屏")
public class AlarmScreenController extends BaseController {

	
	private Logger logger = LoggerFactory.getLogger(AnalysisResultController.class);

	@Autowired
	private WarnResultForESService warnResultForEsService;

	@Autowired
	BaseSysinfoService baseSysinfoService;
	
	@Autowired
	RiskEventRuleService riskEventRuleService;

	@Autowired
	private AssetService assetService;

	@Autowired
	private AlarmScreenService alarmScreenService;

	/**
	 * 根据预警处置guid获得预警处置的值
	 * @return
	 */
	@Ignore
	@GetMapping("/getSystemTime")
	@ApiOperation(value="获取系统时间",notes="")
	@SysRequestLog(description = "告警大屏-获取系统时间", actionType = ActionType.SELECT, manually = false)
	public Result<String> getSystemTime( ){
		String  time=DateUtil.format(new Date());
		return  ResultUtil.success(time);
	}

	private void addData(String nameStr,Object value ,List<Map<String, Object>> result) {
		Map map=new HashMap<>();
		map.put("name", nameStr);
		map.put("value", value);
		result.add(map);
	}
	
	private void addData(String nameStr,Object value ,List<Map<String, Object>> result,int index) {
		Map map=new HashMap<>();
		map.put("name", nameStr);
		map.put("value", value);
		result.add(index,map);
	}
	
	
	@GetMapping("/getAlarmStatistics/{timeType}")
	@ApiOperation(value="获取告警统计",notes="返回结果  total、unhandle、handled")
	@SysRequestLog(description = "告警大屏-获取告警统计", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String,Object>> getAlarmStatistics(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
//		private String stime; //开始时间
//		private String etime; //结束时间
//		private Integer statusEnum; //已处置：5  戴处置：0，全部不传
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
	
		 
//		analysisVO.setStime(DateUtil.format(new Date(), "yyyy-MM-dd 00:00:00"));
//		analysisVO.setEtime(DateUtil.format(new Date()));
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		
		
		
		Long total=0L;
		Long unhandle=0L;
		Long handled=0L;
		
		SearchField searchField=new SearchField("statusEnum",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForEsService.eventAlarmStatistics(analysisVO, searchField);
		for(Map<String, Object> map : eventAlarmStatistics) {
			 String statusEnum = map.get("statusEnum").toString();
			 long count = Long.parseLong( map.get("doc_count").toString());
			 if("0".equals(statusEnum)) {
				 unhandle+=count;
			 }
			 if("5".equals(statusEnum)) {
				 handled+=count;
			 }
			 total+=count;
		}
		Map<String,Object> result=new HashMap<>();
		result.put("total", Long.toString(total));
		result.put("handled", Long.toString(handled));
		result.put("unhandle", Long.toString(unhandle));
 
		return ResultUtil.success(result);
	}

	
	//受影响资产情况
	@GetMapping("/getAffectedAssetStatistics/{timeType}")
	@ApiOperation(value="受影响资产情况 受影响资产比例（待处理告警影响的资产）",notes="返回结果  percentage、total、count")
	@SysRequestLog(description = "告警大屏-受影响资产情况 受影响资产比例（待处理告警影响的资产）", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String,Object>> getAffectedAssetStatistics(@PathVariable("timeType") String timeType){
		
		
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		analysisVO.setStatusEnum("0");
		//求受影响的资产数量
		Integer count = warnResultForEsService.getAffectedAssetCount(analysisVO);
		//求资产总数
		Integer total =0;
		Long assetCount = assetService.getAssetCount();
		if(assetCount!=null) {
			total=assetCount.intValue();
		}
		//求受影响资产比例
		double percentage=total>0? ( (double) Math.round((count*100.00/total) * 100) / 100 ):0.00;
		
		Map<String,Object> result=new HashMap<>();
		result.put("count", count);
		result.put("total", total);
		result.put("percentage", percentage);
		return ResultUtil.success(result);
		
	}
	
	
	@GetMapping("/getAffectedAssetStatistics/detail/{top}/{timeType}")
	@ApiOperation(value="受影响资产情况 受影响资产比例（待处理告警影响的资产）",notes="")
	@SysRequestLog(description = "告警大屏-受影响资产情况 受影响资产比例（待处理告警影响的资产）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAffectedAssetStatisticsDetail(@PathVariable("top") Integer top,@PathVariable("timeType") String timeType){
		
		
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		analysisVO.setStatusEnum("0");
		
		//求受影响的资产详情
		  List<Map<String, Object>> affectedAssetDetail = warnResultForEsService.getAffectedAssetDetail(analysisVO, top);
		  List<Map<String, Object>> result=new ArrayList<>();
		  
		  for(Map<String, Object> map :affectedAssetDetail) {
			  addData(map.get("dstIps").toString(), map.get("doc_count"), result);
		  }
		 
		return ResultUtil.success(result);
		
	}
	
	
	@GetMapping(value="/getAlarmEventLevel/{timeType}")
	@ApiOperation(value="获得安全等级统计",notes="")
	@SysRequestLog(description = "告警大屏-获得安全等级统计", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String, Object>> getAlarmEventLevel(@PathVariable("timeType") String timeType){
		 AnalysisVO analysisVO=new  AnalysisVO();
			try {
				baseSysinfoService.addUserPermissions (analysisVO);
			}catch (Exception e) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
			}
//		Date now=new Date();
//		analysisVO.setStime(DateUtil.format(DateUtils.addDays(now, -29), "yyyy-MM-dd 00:00:00"));//前推29天
//		analysisVO.setEtime(DateUtil.format(now));

			try {
				baseSysinfoService.setTimes(timeType, analysisVO);
			} catch (Exception e1) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
			}
	 
		SearchField searchField = new SearchField("weight", FieldType.String, null);
		
		List<Map<String, Object>> eventAlarmStatistics = warnResultForEsService.eventAlarmStatistics(analysisVO,searchField);
		 
		 //List<Map<String, Object>> result=new  ArrayList<>();
		Map<String, Object> result=new HashMap<>();
		 for(int i=1;i<=5;i++) {
			 Object docCount=0;
			 for(Map<String, Object> map : eventAlarmStatistics) {
				 if(map.get("weight").toString().equals(Integer.toString(i))) {
					 docCount= map.get("doc_count");
					 					 break;
				 }
			 }
			 //addData(Integer.toString(i),doc_count, result);
			 result.put(Integer.toString(i), docCount);
		 }
		 return ResultUtil.success(result);
 
	}
	
	@PostMapping(value="/getWideAlarmEventLevel")
	@ApiOperation(value="获得全网风险安全等级统计",notes="")
	@SysRequestLog(description = "告警大屏-获得全网风险安全等级统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getWideAlarmEventLevel(@RequestBody WideVulVO wideVulVO){
		
		 AnalysisVO analysisVO=new  AnalysisVO();
			try {
				baseSysinfoService.addUserPermissions (analysisVO);
			}catch (Exception e) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
			}
			
			return getWideAlarmEventLevel(wideVulVO, analysisVO);
 
	}

	private Result<List<Map<String, Object>>> getWideAlarmEventLevel(WideVulVO wideVulVO, AnalysisVO analysisVO) {
		
		try {
			//当传参为空时，默认{startTime=昨天当前时间，endTime=当前时间}
			wideVulVoInit(wideVulVO, analysisVO);
			
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		List<Map<String, Object>> res = new ArrayList<>();
		SearchField searchField = new SearchField("weight", FieldType.String, null);

		List<Map<String, Object>> eventAlarmStatistics = warnResultForEsService.eventAlarmStatistics(analysisVO,
				searchField);
		Map<String, Object> startData = new HashMap<>();
		for (int i = 1; i <= 5; i++) {
			Object docCount = 0;
			for (Map<String, Object> map : eventAlarmStatistics) {
				if (map.get("weight").toString().equals(Integer.toString(i))) {
					docCount = map.get("doc_count");
					break;
				}
			}
			startData.put(Integer.toString(i), docCount);
		}
		//计算往前推一个时间范围的风险值
		String startTime = wideVulVO.getStartTime();
		String endTime = wideVulVO.getEndTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = format.parse(startTime);
			Date endDate = format.parse(endTime);

			long preStart = startDate.getTime() - (endDate.getTime() - startDate.getTime());
			Calendar ca = Calendar.getInstance();
			ca.setTimeInMillis(preStart);
			Date preStartDate = ca.getTime();

			analysisVO.setStime(format.format(preStartDate));
			analysisVO.setEtime(startTime);
			List<Map<String, Object>> eventAlarmStatistics2 = warnResultForEsService.eventAlarmStatistics(analysisVO,
					searchField);
			Map<String, Object> endData = new HashMap<>();
			for (int i = 1; i <= 5; i++) {
				Object docCount = 0;
				for (Map<String, Object> map : eventAlarmStatistics2) {
					if (map.get("weight").toString().equals(Integer.toString(i))) {
						docCount = map.get("doc_count");
						break;
					}
				}
				endData.put(Integer.toString(i), docCount);
			}
			Map<String, Object> map0 = new HashMap<>();
			map0.put("name", "lowEvent");
			map0.put("title", "较低事件数");
			map0.put("startValue", startData.get("1"));
			map0.put("endValue", endData.get("1"));
			res.add(map0);
			Map<String, Object> map1 = new HashMap<>();
			map1.put("name", "infoEvent");
			map1.put("title", "一般事件数");
			map1.put("startValue", startData.get("2"));
			map1.put("endValue", endData.get("2"));
			res.add(map1);
			Map<String, Object> map2 = new HashMap<>();
			map2.put("name", "impEvent");
			map2.put("title", "重要事件数");
			map2.put("startValue", startData.get("3"));
			map2.put("endValue", endData.get("3"));
			res.add(map2);
			Map<String, Object> map3 = new HashMap<>();
			map3.put("name", "serEvent");
			map3.put("title", "严重事件数");
			map3.put("startValue", startData.get("4"));
			map3.put("endValue", endData.get("4"));
			res.add(map3);
			Map<String, Object> map4 = new HashMap<>();
			map4.put("name", "merEvent");
			map4.put("title", "紧急事件数");
			map4.put("startValue", startData.get("5"));
			map4.put("endValue", endData.get("5"));
			res.add(map4);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		return ResultUtil.success(res);
	}
	
	@PostMapping(value="/getDomainWideAlarmEventLevel")
	@ApiOperation(value="获得全网风险安全等级统计(传参实现权限)",notes="")
	@SysRequestLog(description = "告警大屏-获得全网风险安全等级统计(传参实现权限)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getDomainWideAlarmEventLevel(@RequestBody DomainWideVulVO wideVulVO){
		List<Map<String,Object>> res = new ArrayList<>();
		 AnalysisVO analysisVO=new  AnalysisVO();
			try {
				//baseSysinfoService.addUserPermissions (analysisVO);
				if(wideVulVO.getDomainCode()!=null&& "all".equals(wideVulVO.getDomainCode())) {
					
				}else {
					analysisVO.setDstAreaCode(wideVulVO.getDomainCode()); 
				}
			}catch (Exception e) {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
			}
			
			return getWideAlarmEventLevel(wideVulVO, analysisVO);
 
	}
/**
 * 当传参为空时，默认{startTime=昨天当前时间，endTime=当前时间}
 * @param wideVulVO
 * @param analysisVO
 */
	private void wideVulVoInit(WideVulVO wideVulVO, AnalysisVO analysisVO) {
		if (StringUtils.isBlank(wideVulVO.getStartTime()) || StringUtils.isBlank(wideVulVO.getEndTime())) {
			Calendar ca = Calendar.getInstance();
			Date today = ca.getTime();
			ca.add(Calendar.DAY_OF_MONTH, -1);
			Date yesterday = ca.getTime();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String todayStr = format.format(today);
			String yesterdayStr = format.format(yesterday);

			analysisVO.setStime(todayStr);
			analysisVO.setEtime(yesterdayStr);
		} else {
			analysisVO.setStime(wideVulVO.getStartTime());
			analysisVO.setEtime(wideVulVO.getEndTime());
		}
	}
	
	
	@GetMapping("/getAlarmGroupStatistics/{timeType}")
	@ApiOperation(value="获取告警分组统计",notes="统计出木马病毒，入侵攻击，脚本注入，尝试破解，漏洞攻击，信息篡改，信息探测，DDOS攻击")
	@SysRequestLog(description = "告警大屏-获取告警分组统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmGroupStatistics(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
//		Date now=new Date();
//		analysisVO.setStime(DateUtil.format(DateUtils.addDays(now, -29), "yyyy-MM-dd 00:00:00"));//前推29天
//		analysisVO.setEtime(DateUtil.format(now));
		
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		
 
		String[]   items= {"木马病毒","入侵攻击","脚本注入","尝试破解","漏洞攻击","信息篡改","信息探测","DDOS攻击"};
 
		Map<String, List<String> > eventConfigs= EventConfig.eventConfigList;
		Map<String, String > eventNames=EventConfig.eventNames;
		
		List<String>  codes=new ArrayList<>();
		for (String item : items) {
			if(eventConfigs.containsKey(item)) {
			  List<String> list = eventConfigs.get(item);
			  codes.addAll(list);
			}else {
				logger.info("出现异常的分类名："+item);
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"出现异常的分类名："+item);
			}
		}
		analysisVO.setRiskEventCodeArr(codes.toArray(new String[codes.size()]));
		
		SearchField searchField=new SearchField("riskEventCode",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForEsService.eventAlarmStatistics(analysisVO, searchField);
		
		
		List<Map<String, Object>>  result=new ArrayList<>();
		for (String item : items) {
			Long count=0L;
			
			if(eventNames.containsKey(item)) {
			
			  List<String> list = eventConfigs.get(item);
			   for(Map<String, Object> statistics : eventAlarmStatistics) {
				   String riskEventCode = statistics.get("riskEventCode").toString();
				   if(list.contains(riskEventCode)) {
					   Object object = statistics.get("doc_count");
					   if(object!=null) {
						   count+=Long.parseLong(object.toString()); 
					   }
				   }
			   }
			   Map<String, Object> map=new HashMap<String, Object>();
			   map.put("name", item);
			   map.put("value", count);
			   map.put("group", eventNames.get(item)); 
			   result.add(map);
			}else {
				logger.info("出现异常的分类名："+item);
			}
		}
		return ResultUtil.success(result);
	}
	
	@GetMapping("/getAlarmGroupStatisticsForSeaoil/{timeType}")
	@ApiOperation(value="获取告警分组统计(海油定制)",notes="统计出全部告警类型")
	@SysRequestLog(description = "告警大屏-获取告警分组统计(海油定制)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmGroupStatisticsForSeaoil(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		List<Map<String, Object>>  result=new ArrayList<>();
		SearchField searchField=new SearchField("ruleCode",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForEsService.eventAlarmStatistics(analysisVO, searchField);
		for (Map<String, Object> statistics : eventAlarmStatistics) {
			 String ruleCode = statistics.get("ruleCode").toString();
			 
			 String ruleName = getRuleNameByCode(ruleCode);
			 Map<String, Object> map=new HashMap<String, Object>();
			 map.put("name", ruleName);
			 map.put("value", statistics.get("doc_count"));
			 map.put("group", ruleCode); 
			 result.add(map);
		}
		return ResultUtil.success(result);
	}
	
	@GetMapping("/getAppAlarmGroupStatisticsForSeaoil/{timeType}")
	@ApiOperation(value="获取告警分组统计(海油定制)",notes="统计出应用受攻击告警类型")
	@SysRequestLog(description = "告警大屏-获取告警分组统计(海油定制)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAppAlarmGroupStatisticsForSeaoil(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		List<Map<String, Object>>  result=new ArrayList<>();
		SearchField searchField=new SearchField("ruleCode",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForEsService.eventAlarmStatistics(analysisVO, searchField);
		for (Map<String, Object> statistics : eventAlarmStatistics) {
			 String ruleCode = statistics.get("ruleCode").toString();
			 
			 String ruleName = getRuleNameByCode(ruleCode);
			 
			 Map<String, Object> map=new HashMap<String, Object>();
			 map.put("name", ruleName);
			 map.put("value", statistics.get("doc_count"));
			 map.put("group", ruleCode); 
			 result.add(map);
		}
		return ResultUtil.success(result);
	}

	private String getRuleNameByCode(String ruleCode) {
		String ruleName = "";
		List<QueryCondition> conns = new ArrayList<>();
		 conns.add(QueryCondition.eq("ruleCode", ruleCode));
		 List<RiskEventRule> findAll = riskEventRuleService.findAll(conns);
		 if(findAll!=null && findAll.size()>0){
			 ruleName = findAll.get(0).getName();
		 }
		 return ruleName;
	}
	
	
	@GetMapping(value="/getAlarmTrendBy24H")
	@ApiOperation(value="24小时告警趋势",notes="")
	@SysRequestLog(description = "告警大屏-24小时告警趋势", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmTrendBy24H( ){
		
		AnalysisVO analysisVO=new AnalysisVO();
		analysisVO.setFlag("day");
		Date now=new Date();
//		analysisVO.setStime(DateUtil.format(now, "yyyy-MM-dd 00:00:00"));
//		analysisVO.setEtime(DateUtil.format(DateUtils.addDays(now, 1), "yyyy-MM-dd 00:00:00"));
		
		try {
			baseSysinfoService.setTimes("24h", analysisVO);
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		
		Result<List<Map<String,Object>>> result = warnResultForEsService.getAlarmTrendBy7Days(analysisVO);
		
		
		String today = DateUtil.format(new Date(), "yyyy-MM-dd ");
		String yesterday = DateUtil.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd ");
		
		List<Map<String,Object>> list=new LinkedList<>();
		int hore=(new Date()).getHours();
		String str = String.format("%02d", hore);
		Boolean asc=true;
		int index=0;
		for(Map<String,Object> map :result.getData()) {
			String triggerTime = map.get("triggerTime").toString();
			
			String  time=triggerTime;
			if(Integer.parseInt(triggerTime)>=0&&Integer.parseInt(triggerTime)<=hore) {
				time=today+triggerTime;
			}else {
				time=yesterday+triggerTime;
			}
			if(asc) {
				//list.add(map);
				addData(time, map.get("doc_count"), list);
			}else {
				//list.add(index,map);
				addData(time, map.get("doc_count"), list,index);
				index++;
			}
			if(triggerTime.equals(str)) {
				asc=!asc;
			}
		}
		result.setData(list);
		return result;
	}
	
	
	@GetMapping(value="/getAlarmTrend/{timeType}")
	@ApiOperation(value="告警趋势(海油定制)",notes="")
	@SysRequestLog(description = "告警大屏-告警趋势(海油定制)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmTrend(@PathVariable("timeType") String timeType ){
		
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		//analysisVO.setFlag(timeType);
		String timeFormat = getTimeFormat(timeType, analysisVO);
		Result<List<Map<String,Object>>> result = warnResultForEsService.getAlarmTrendBy7Days(analysisVO);
		List<Map<String, Object>> list = new LinkedList<>();

		String str = DateUtil.format(new Date(), timeFormat);
		Boolean asc = true;
		int index = 0;
		for (Map<String, Object> map : result.getData()) {
			String triggerTime = map.get("triggerTime").toString();

			triggerTime = getWholeTimeType(timeType, triggerTime);
			map.put("triggerTime", triggerTime);
			
			if (asc) {
				list.add(map);
			} else {
				list.add(index, map);
				index++;
			}
			if (triggerTime.equals(str)) {
				asc = !asc;
			}
		}
		result.setData(list);
		return result;
	}
	
	private String getWholeTimeType(String timeType, String triggerTime) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatYear = new SimpleDateFormat("yyyy");
		switch (timeType) {
		case "24h":
		case "today":
			triggerTime = format.format(new Date()) + " " +triggerTime;
			break;
		default:
			triggerTime = formatYear.format(new Date()) + "-" +triggerTime;
			break;
		}
		return triggerTime;
	}
	
	private String getTimeFormat(String timeType, AnalysisVO analysisVO) {
		String timeFormat = "HH";

		switch (timeType) {
		case "today":
		case "24h":
			analysisVO.setFlag("day");
			timeFormat = "HH";
			break;
		case "week":
		case "7day":
			analysisVO.setFlag("week");
			timeFormat = "MM-dd";
			break;
		case "month":
		case "30day":
		case "15day":
			analysisVO.setFlag("month");
			timeFormat = "MM-dd";
			break;
		case "all":// 查询所有数据，无时间限定
			analysisVO.setFlag("year");
			timeFormat = "MM";
			break;
		default:
			break;
		}
		return timeFormat;
	}
	
	@GetMapping(value="/getMapData/{mapType}/{timeType}")
	@ApiOperation(value="大屏地图数据",notes="mapType 传参意义：1州  2国家 3省份  4城市")
	@SysRequestLog(description = "告警大屏-大屏地图数据", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getMapData(@PathVariable("mapType") Integer mapType,@PathVariable("timeType") String timeType){
		
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
 
		String nameStr="";
		switch (mapType) {
		case 1:
			nameStr=".continent";
			break;
		case 2:
			nameStr=".country";
			break;
		case 3:
			nameStr=".province";
			break;
		case 4:
			nameStr=".city";
			break;
		default:
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "地图显示范围传参异常");
		}
		
		SearchField child=new SearchField("dstWorldMapName"+nameStr, FieldType.String, null);
		SearchField searchField=new SearchField("srcWorldMapName"+nameStr, FieldType.String, child);
		
		List<Map<String, Object>> eventAlarmStatistics = warnResultForEsService.eventAlarmStatistics(analysisVO, searchField);
		logger.info("地图结果记录：");
		//logger.info(eventAlarmStatistics);
		List<Map<String,Object>>  result=new  ArrayList<>();
		for(Map<String, Object> map : eventAlarmStatistics)
		{
			if(map.get("srcWorldMapName"+nameStr)==null||map.get("dstWorldMapName"+nameStr)==null) {
				continue;
			}

			List<Map<String, Object>> childList=(List<Map<String, Object>>)map.get("dstWorldMapName"+nameStr);
			for(Map<String, Object> childMap : childList)
			{
				Map<String, Object>  item=new HashMap<String, Object>();
				item.put("from", map.get("srcWorldMapName"+nameStr));
				item.put("to", childMap.get("dstWorldMapName"+nameStr));
				item.put("count", childMap.get("doc_count"));
				result.add(item);
			}
		}
	  
		return ResultUtil.success(result);
	}
	
	
	
	@GetMapping(value="/getAnalysisInfoByArea/{timeType}")
	@ApiOperation(value="30天内告警按区域统计",notes="")
	@SysRequestLog(description = "告警大屏-30天内告警按区域统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAnalysisInfoByArea(@PathVariable("timeType") String timeType){
		
		AnalysisVO analysisVO=new AnalysisVO();
//		Date now=new Date();
//		analysisVO.setStime(DateUtil.format(DateUtils.addDays(now, -29), "yyyy-MM-dd 00:00:00"));//前推29天
//		analysisVO.setEtime(DateUtil.format(now));
		
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
	    List<Map<String, Object>> analysisInfoByArea = warnResultForEsService.getAnalysisInfoBySrcArea(analysisVO);
		return ResultUtil.success(analysisInfoByArea);
	}

	@PostMapping(value="/getAssetAlarmDistribution")
	@ApiOperation(value="获取资产告警分布",notes="")
	@SysRequestLog(description = "告警大屏-获取资产告警分布", actionType = ActionType.SELECT, manually = false)
	public Result<List<AlarmDistributionRes>> getAssetAlarmDistribution(@RequestBody RequestBean req){
		List<AlarmDistributionRes> result =alarmScreenService.getAssetAlarmDistribution(req);
		return ResultUtil.successList(result);
	}

	@PostMapping(value="/getAssetAlarmEvent")
	@ApiOperation(value="获取资产告警事件",notes="")
	@SysRequestLog(description = "告警大屏-获取资产告警事件", actionType = ActionType.SELECT, manually = false)
	public Result<List<AlarmEventRes>> getAssetAlarmEvent(@RequestBody RequestBean req){
		List<AlarmEventRes> result = alarmScreenService.getAssetAlarmEvent(req);
		return ResultUtil.successList(result);
	}

	@PostMapping(value="/getViolationPersonTotal/{top}")
	@ApiOperation(value="违规人员统计top5",notes="")
	@SysRequestLog(description = "告警大屏-违规人员统计top5", actionType = ActionType.SELECT, manually = false)
	public Result<List<ViolationPersonRes>> getViolationPersonTotal(@RequestBody RequestBean req, @PathVariable("top") Integer top){
		List<ViolationPersonRes> result = alarmScreenService.getViolationPersonTotal(req, top);
		return ResultUtil.successList(result);
	}

	@PostMapping(value="/getAlarmEventRank/{top}")
	@ApiOperation(value="威胁事件排名top5",notes="")
	@SysRequestLog(description = "告警大屏-威胁事件排名top5", actionType = ActionType.SELECT, manually = false)
	public Result<List<AlarmEventRankRes>> getAlarmEventRank(@RequestBody RequestBean req, @PathVariable("top") Integer top){
		List<AlarmEventRankRes> result = alarmScreenService.getAlarmEventRank(req, top);
		return ResultUtil.successList(result);
	}

	@PostMapping(value="/getViolations")
	@ApiOperation(value="违规行为",notes="")
	@SysRequestLog(description = "告警大屏-违规行为", actionType = ActionType.SELECT, manually = false)
	public Result<List<String>> getViolations(@RequestBody RequestBean req){
		List<String> result = alarmScreenService.getViolations(req);
		return ResultUtil.successList(result);
	}

	@PostMapping(value="/getAlarmEventLevelTotal")
	@ApiOperation(value="告警统计",notes="")
	@SysRequestLog(description = "告警大屏-告警统计", actionType = ActionType.SELECT, manually = false)
	public Result<EventLevelTotalRes> getAlarmEventLevelTotal(@RequestBody RequestBean req){
		EventLevelTotalRes result = alarmScreenService.getAlarmEventLevelTotal(req);
		return ResultUtil.success(result);
	}

	@PostMapping(value="/getAlarmEventTrend")
	@ApiOperation(value="安全事件趋势",notes="")
	@SysRequestLog(description = "告警大屏-安全事件趋势", actionType = ActionType.SELECT, manually = false)
	public Result<List<EventTrendRes>> getAlarmEventTrend(@RequestBody RequestBean req){
		List<EventTrendRes> result = alarmScreenService.getAlarmEventTrend(req);
		return ResultUtil.successList(result);
	}

	@PostMapping(value="/getAssetCountByAlarm")
	@ApiOperation(value="统计告警资产数",notes="")
	@SysRequestLog(description = "告警大屏-统计告警资产数", actionType = ActionType.SELECT, manually = false)
	public Result<Integer> getAssetCountByAlarm(@RequestBody RequestBean req){
		Integer result = alarmScreenService.getAssetCountByAlarm(req);
		return ResultUtil.success(result);
	}

}

package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.BaseSysinfoService;
import com.vrv.vap.alarmdeal.frameworks.config.EventConfig;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.analysis.server.IpTableService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assetAlarmScreen")
@Api(description="资产告警大屏")
public class AssetAlarmScreenController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(AssetAlarmScreenController.class);

	@Autowired
	private WarnResultForESService warnResultForESService;
	
	@Autowired
	private BaseSysinfoService baseSysinfoService;

	@Autowired
	private RiskEventRuleService riskEventRuleService;

	@Autowired
	private IpTableService ipTableService;
	
	Gson  gson=new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
			.create();
	
	@GetMapping("/getAlarmHandleStatistics/{timeType}")
	@ApiOperation(value="获取告警处理情况",notes="返回结果  total、unhandle、handled")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String,Object>> getAlarmHandleStatistics(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		
		Long total=0L;
		Long unhandle=0L;
		Long handled=0L;

		SearchField searchField=new SearchField("statusEnum",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
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
	
	@GetMapping("/getAlarmHandleStatisticsForSeaoil/{timeType}")
	@ApiOperation(value="获取告警处理情况(海油定制)",notes="返回结果  total、unhandle、handled")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String,Object>> getAlarmHandleStatisticsForSeaoil(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}

		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		
		Long total=0L;
		Long unhandle=0L;
		Long handled=0L;
		int failAsset=0;
		
		//APT7类告警
		String[] ruleCodeArr = warnResultForESService.getRuleCodeArr();
		analysisVO.setRuleCodeArr(ruleCodeArr);
		SearchField searchField=new SearchField("statusEnum",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
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
		
		//受影响资产
		SearchField searchField2 = new SearchField("dstIps",FieldType.String,null);
		analysisVO.setStatusEnum("0");
		List<Map<String, Object>> failAssetStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField2);
		if(failAssetStatistics!=null){
			failAsset = failAssetStatistics.size();
		}
		
		Map<String,Object> result=new HashMap<>();
		result.put("total", Long.toString(total));
		result.put("handled", Long.toString(handled));
		result.put("unhandle", Long.toString(unhandle));
		result.put("failAsset", Long.toString(failAsset));
 
		return ResultUtil.success(result);
	}

	
	
	
	@GetMapping("/getAlarmHandleStatistics/{statusEnum}/{timeType}")
	@ApiOperation(value="获取今天告警统计",notes="返回结果  total、unhandle、handled")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String,Object>> getAlarmHandleStatistics(@PathVariable("statusEnum") String statusEnum,@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		if(!"all".equals(statusEnum)) {
			analysisVO.setStatusEnum(statusEnum);
		}else {
			analysisVO.setEsQuery(QueryCondition_ES.notNull("statusEnum"));
		}

		SearchField searchField = new SearchField("weight", FieldType.String, null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		Map<String, Object> result=new HashMap<>();
		 for(int i=1;i<=5;i++) {
			 Object doc_count=0;
			 for(Map<String, Object> map : eventAlarmStatistics) {
				 if(map.get("weight").toString().equals(Integer.toString(i))) {
					 doc_count= map.get("doc_count");
					 					 break;
				 }
			 }
			 result.put(Integer.toString(i), doc_count);
		 }
 
		return ResultUtil.success(result);
	}
	
	
	
	
	@GetMapping("/getAlarmSrcIpStatistics/{timeType}")
	@ApiOperation(value="攻击租户资产最多的IP TOP5",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmSrcIpStatistics(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		SearchField searchField=new SearchField("src_ips",FieldType.String,0,5,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		  List<Map<String, Object>> result=new ArrayList<>();
		  
		  for(Map<String, Object> map :eventAlarmStatistics) {
			  addData(map.get("src_ips").toString(), map.get("doc_count"), result);
		  }
		 
		return ResultUtil.success(result);
 
 
	}
	
	@GetMapping("/getAlarmSrcIpStatisticsForSeaoil/{timeType}/{top}")
	@ApiOperation(value="攻击租户资产最多的IP(海油定制)",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmSrcIpStatisticsForSeaoil(@PathVariable("timeType") String timeType,@PathVariable("top") int top){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		//APT7类告警
		String[] ruleCodeArr = warnResultForESService.getRuleCodeArr();
		analysisVO.setRuleCodeArr(ruleCodeArr);
		SearchField searchField=new SearchField("src_ips",FieldType.String,0,top,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		  List<Map<String, Object>> result=new ArrayList<>();
		  
		  for(Map<String, Object> map :eventAlarmStatistics) {
			  addData(map.get("src_ips").toString(), map.get("doc_count"), result);
		  }
		 
		return ResultUtil.success(result);
 
 
	}
	
	
	@GetMapping("/getAlarmSrcIpStatistics/{ip:.+}/{timeType}")
	@ApiOperation(value="该设备攻击租户资产的次数趋势",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmSrcIpStatistics(@PathVariable("ip") String ip,@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		analysisVO.setSrcIp_eq(ip);;//relatedIps

		
		String timeFormat = getTimeFormat(timeType, analysisVO);
		
		Result<List<Map<String,Object>>> result = warnResultForESService.getAlarmTrendBy7Days(analysisVO);
 
		List<Map<String, Object>> list = new LinkedList<>();

		String str = DateUtil.format(new Date(), timeFormat);
		Boolean asc = true;
		int index = 0;
		for (Map<String, Object> map : result.getData()) {
			String triggerTime = map.get("triggerTime").toString();

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
	//
	@GetMapping("/getAlarmDstIpStatistics/{timeType}")
	@ApiOperation(value="租户下受攻击最多的IP TOP5",notes="大屏左下角")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmDstIpStatistics(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		SearchField searchField=new SearchField("dstIps",FieldType.String,0,5,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		  List<Map<String, Object>> result=new ArrayList<>();
		  
		  for(Map<String, Object> map :eventAlarmStatistics) {
			  addData(map.get("dstIps").toString(), map.get("doc_count"), result);
		  }
		 
		return ResultUtil.success(result);
	}
	
	@GetMapping("/getAlarmDstIpStatisticsForSeaoil/{timeType}/{top}")
	@ApiOperation(value="租户下受攻击最多的IP(海油定制)",notes="大屏左下角")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmDstIpStatisticsForSeaoil(@PathVariable("timeType") String timeType, @PathVariable("top") int top){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		//APT7类告警
		String[] ruleCodeArr = warnResultForESService.getRuleCodeArr();
		analysisVO.setRuleCodeArr(ruleCodeArr);
		SearchField searchField=new SearchField("dstIps",FieldType.String,0,top,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		  List<Map<String, Object>> result=new ArrayList<>();
		  
		  for(Map<String, Object> map :eventAlarmStatistics) {
			  addData(map.get("dstIps").toString(), map.get("doc_count"), result);
		  }
		 
		return ResultUtil.success(result);
	}
	
	
	
	@GetMapping("/getAlarmDstIpStatistics/{ip:.+}/{timeType}")
	@ApiOperation(value="近七天该设备被攻击的次数趋势",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmDstIpStatistics(@PathVariable("ip") String ip,
			@PathVariable("timeType") String timeType) {
		AnalysisVO analysisVO = new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e1.getMessage());
		}

		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		} catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		analysisVO.setDstIps(ip);

		String timeFormat = getTimeFormat(timeType, analysisVO);
		Result<List<Map<String, Object>>> result = warnResultForESService.getAlarmTrendBy7Days(analysisVO);

		List<Map<String, Object>> list = new LinkedList<>();

		String str = DateUtil.format(new Date(), timeFormat);
		Boolean asc = true;
		int index = 0;
		for (Map<String, Object> map : result.getData()) {
			String triggerTime = map.get("triggerTime").toString();

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
	
	
	
	@GetMapping("/getAlarmEventGroupStatistics/{timeType}")
	@ApiOperation(value="取攻击事件数最多的攻击类型TOP10",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmEventGroupStatistics(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		SearchField searchField=new SearchField("riskEventCode",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		Map<String, List<String> > eventConfigs=EventConfig.eventConfigList;
		Map<String, String> eventNames = EventConfig.eventNames;
		

		Map<String, Long>  map=new  HashMap<>();
		
 
		for(String key : eventConfigs.keySet()) {
				map.put(key, 0L);
		}
		for(Map<String, Object> item : eventAlarmStatistics) {
			String riskEventCode = item.get("riskEventCode").toString();
			for(String key : eventConfigs.keySet()) {
				List<String> list = eventConfigs.get(key);
				if(list.contains(riskEventCode)) {
					Long count=0L;
					if (map.containsKey(key)) {
						count = map.get(key);
						map.remove(key);
					}
					count+=Long.parseLong(item.get("doc_count").toString());
					map.put(key, count);
					break;
				}
			}
		}
		logger.info("map组装完毕："+gson.toJson(map));
		
		//排序
		List<Map<String, Object>> result=new LinkedList<>();
		
		for(String key :map.keySet()) { 
			Long count = map.get(key);
			int index=0;
			for(Map<String, Object> item :result) {
				Long cc= (Long)item.get("value");
				if(cc>=count) {
					index++;
				}else {
					break;
				}
			}
			//addData(key,count,result,index);
			Map item=new HashMap<>();
			item.put("name", key);
			item.put("value", count);
			item.put("group", eventNames.get(key));
			
			result.add(index,item);
		}
		
		if(result.size()>10) {
			result=	result.subList(0, 10);
		}  
		
		return ResultUtil.success(result);
	}
	
	@GetMapping("/getAlarmEventGroupStatisticsForSeaoil/{timeType}")
	@ApiOperation(value="取攻击事件数最多的攻击类型TOP10(海油定制)",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmEventGroupStatisticsForSeaoil(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		List<Map<String, Object>>  result=new ArrayList<>();
		//APT7类告警
		String[] ruleCodeArr = warnResultForESService.getRuleCodeArr();
		analysisVO.setRuleCodeArr(ruleCodeArr);
		//SearchField child=new SearchField("dstIps",FieldType.String,null);
		SearchField searchField=new SearchField("ruleCode",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		for (String string : ruleCodeArr) {
			int value = 0;
			Map<String, Object> map=new HashMap<String, Object>();
			for (Map<String, Object> statistics : eventAlarmStatistics) {
				String ruleCode = statistics.get("ruleCode").toString();
				if(string.equals(ruleCode)){
					value += (long)statistics.get("doc_count");
					
					String ruleName = getRuleNameByCode(ruleCode);
					map.put("name", ruleName);
					map.put("value", value);
					map.put("group", string); 
					result.add(map);
				}
			}
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
	
	@GetMapping("/getAlarmEventGroupStatistics/{eventGroup}/{timeType}")
	@ApiOperation(value="近七天该设备被攻击的次数趋势",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmEventGroupStatistics(@PathVariable("eventGroup") String eventGroup,@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		Map<String, String> eventNames = EventConfig.eventNames;
		Map<String, String[]> eventConfigArr = EventConfig.eventConfigArr;
		for(String key : eventNames.keySet())
		{		 
			if(eventNames.get(key).equals(eventGroup)) {
				analysisVO.setRiskEventCodeArr(eventConfigArr.get(key));
				break;
			}
		}
		
		
		String timeFormat = getTimeFormat(timeType, analysisVO);
		
		Result<List<Map<String,Object>>> result = warnResultForESService.getAlarmTrendBy7Days(analysisVO);
 
		
		List<Map<String, Object>> list = new LinkedList<>();

		String str = DateUtil.format(new Date(), timeFormat);
		Boolean asc = true;
		int index = 0;
		for (Map<String, Object> map : result.getData()) {
			String triggerTime = map.get("triggerTime").toString();

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
	
	
	@GetMapping("/getAlarmTrendStatistics/{timeType}")
	@ApiOperation(value="告警趋势统计",notes="")
	@SysRequestLog(description = "告警趋势统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmTrendStatistics( @PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		String timeFormat = getTimeFormat(timeType, analysisVO);
 
		Result<List<Map<String,Object>>> result = warnResultForESService.getAlarmTrendBy7Days(analysisVO);
		List<Map<String, Object>> list = new LinkedList<>();

		String str = DateUtil.format(new Date(), timeFormat);
		Boolean asc = true;
		int index = 0;
		for (Map<String, Object> map : result.getData()) {
			String triggerTime = map.get("triggerTime").toString();

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
	
	@GetMapping("/getAlarmTrendStatisticsForSeaoil/{timeType}")
	@ApiOperation(value="告警趋势统计(海油定制)",notes="")
	@SysRequestLog(description = "告警趋势统计(海油定制)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmTrendStatisticsForSeaoil( @PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}


		try {
			baseSysinfoService.addUserAssetPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		String timeFormat = getTimeFormat(timeType, analysisVO);
		//APT7类告警
		String[] ruleCodeArr = warnResultForESService.getRuleCodeArr();
		analysisVO.setRuleCodeArr(ruleCodeArr);
		Result<List<Map<String,Object>>> result = warnResultForESService.getAlarmTrendBy7Days(analysisVO);
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
	
	@GetMapping(value="/getMapData/{mapType}/{timeType}")
	@ApiOperation(value="大屏地图数据",notes="mapType 传参意义：1州  2国家 3省份  4城市")
	@SysRequestLog(description = "大屏地图数据", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getMapData(@PathVariable("mapType") Integer mapType,@PathVariable("timeType") String timeType){
		
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}

		try {
			baseSysinfoService.addUserAssetPermissions (analysisVO);
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
		
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
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
	
	@GetMapping(value="/getMapDataForSeaoil/{mapType}/{timeType}")
	@ApiOperation(value="大屏地图数据(海油定制)",notes="mapType 传参意义：1州  2国家 3省份  4城市")
	@SysRequestLog(description = "大屏地图数据(海油定制)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getMapDataForSeaoil(@PathVariable("mapType") Integer mapType,@PathVariable("timeType") String timeType){
		
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}

		try {
			baseSysinfoService.addUserAssetPermissions (analysisVO);
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
		//APT7类告警
		String[] ruleCodeArr = warnResultForESService.getRuleCodeArr();
		analysisVO.setRuleCodeArr(ruleCodeArr);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
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

	@PostMapping("/getAnalysisPager/{timeType}")
	@ApiOperation(value="获得告警分页信息",notes="")
	@SysRequestLog(description = "获得告警分页信息", actionType = ActionType.SELECT, manually = false)
	public PageRes_ES<WarnResultLogTmpVO> getAlarmPager(@PathVariable("timeType") String timeType, @RequestBody AnalysisVO analysisVO, PageReq_ES pageReq) {
		try{
 
			try {
				baseSysinfoService.setTimes(timeType, analysisVO);
			} catch (Exception e1) {
				throw e1;
			}
 
			//analysisVO.setLinkAsset(true);
			try {
				baseSysinfoService.addUserAssetPermissions (analysisVO);
			}catch (Exception e2) {
				throw e2;
			}
			
			PageRes_ES<WarnResultLogTmpVO> pageRes = warnResultForESService.getAlarmPager(analysisVO, pageReq);
			return pageRes;			
		}catch(Exception e) {
			PageRes_ES<WarnResultLogTmpVO> pageRes = new PageRes_ES<>();
			pageRes.setCode("0");
			pageRes.setTotal(Long.valueOf("0"));
			pageRes.setMessage("告警为空");
			return pageRes;
		}
		
	}
	
	@PostMapping("/getAnalysisPagerForSeaoil/{timeType}")
	@ApiOperation(value="获得告警分页信息(海油定制)",notes="")
	@SysRequestLog(description = "获得告警分页信息(海油定制)", actionType = ActionType.SELECT, manually = false)
	public PageRes_ES<WarnResultLogTmpVO> getAnalysisPagerForSeaoil(@PathVariable("timeType") String timeType, @RequestBody AnalysisVO analysisVO, PageReq_ES pageReq) {
		try{
 
			try {
				baseSysinfoService.setTimes(timeType, analysisVO);
			} catch (Exception e1) {
				throw e1;
			}
 
			//analysisVO.setLinkAsset(true);
			try {
				baseSysinfoService.addUserAssetPermissions (analysisVO);
			}catch (Exception e2) {
				throw e2;
			}
			//APT7类告警
			String[] ruleCodeArr = warnResultForESService.getRuleCodeArr();
			analysisVO.setRuleCodeArr(ruleCodeArr);
			PageRes_ES<WarnResultLogTmpVO> pageRes = warnResultForESService.getAlarmPager(analysisVO, pageReq);
			return pageRes;			
		}catch(Exception e) {
			PageRes_ES<WarnResultLogTmpVO> pageRes = new PageRes_ES<>();
			pageRes.setCode("0");
			pageRes.setTotal(Long.valueOf("0"));
			pageRes.setMessage("告警为空");
			return pageRes;
		}
		
	}
	
	
	
	private void addData(String key,Object value ,List<Map<String, Object>> result,int index) {
		Map map=new HashMap<>();
		map.put("name", key);
		map.put("value", value);
		result.add(index,map);
	}
	private void addData(String key,Object value ,List<Map<String, Object>> result) {
		Map map=new HashMap<>();
		map.put("name", key);
		map.put("value", value);
		result.add(map);
	}
	
}

package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.controller.AnalysisResultController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.BaseSysinfoService;
import com.vrv.vap.alarmdeal.frameworks.config.EventConfig;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseSysinfo;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AppSystemService;
import com.vrv.vap.alarmdeal.frameworks.util.GwParamsUtil;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.EventTypeSearchVO;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applicationAlarmScreen")
@Api(description="应用系统告警大屏")
public class ApplicationAlarmScreenController extends BaseController {

	
	private static Logger logger = LoggerFactory.getLogger(AnalysisResultController.class);

	@Autowired
	private WarnResultForESService warnResultForESService;

	@Autowired AppSystemService appSystemService;
	
	@Autowired
	BaseSysinfoService baseSysinfoService;

	@Autowired
	private MapperUtil mapperUtil;

	@GetMapping("/getAllBaseSysinfo")
	@ApiOperation(value="获取所有应用系统（用户相关）",notes="")
	@SysRequestLog(description = "获取所有应用系统（用户相关）", actionType = ActionType.SELECT, manually = false)
	public  Result<List<BaseSysinfo>> getAllBaseSysinfo() {
		User currentUser = baseSysinfoService.getCurrentUser();
		if(currentUser==null) {
	    	return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取用户登陆信息失败！");
		}
		return ResultUtil.success(baseSysinfoService.getAllApplication());
	}
	
	
	
	private  List<BaseSysinfo> getAllApplication() {
		return baseSysinfoService.getAllApplication();
	}
	
	
	@GetMapping("/getLoginUser")
	@ApiOperation(value="获取当前登陆用户",notes="")
	@SysRequestLog(description = "获取当前登陆用户", actionType = ActionType.SELECT, manually = false)
	public Result<User> getLoginUser(){

		User currentUser = baseSysinfoService.getCurrentUser();
		if(currentUser==null) {
	    	return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "获取用户登陆信息失败！");
		}
		return  ResultUtil.success(currentUser);
	}
	
	
	
	
	
	/**
	 * 根据预警处置guid获得预警处置的值
	 * @return
	 */
	@Ignore
	@GetMapping("/getSystemTime")
	@ApiOperation(value="获取系统时间",notes="")
	@SysRequestLog(description = "获取系统时间", actionType = ActionType.SELECT, manually = false)
	public Result<String> getSystemTime( ){
		 String  time=DateUtil.format(new Date());
		return  ResultUtil.success(time);
	}
	

	@GetMapping("/getAlarmStatistics/{timeType}")
	@ApiOperation(value="获取告警统计",notes="返回结果  total、unhandle、handled")
	@SysRequestLog(description = "获取告警统计", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String,Object>> getTodayAlarmStatistics(@PathVariable("timeType") String timeType){
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
	
	@GetMapping("/getAlarmStatistics/{systemId}/{timeType}")
	@ApiOperation(value="获取告警统计",notes="返回结果  total、unhandle、handled")
	@SysRequestLog(description = "获取告警统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmStatistics(@PathVariable("systemId") String systemId
			,@PathVariable("timeType") String timeType
			){
		AnalysisVO analysisVO=new AnalysisVO();

		//analysisVO.setStime(DateUtil.format(new Date(), "yyyy-MM-dd 00:00:00"));
		//analysisVO.setEtime(DateUtil.format(new Date()));
		try {
			baseSysinfoService.setTimes("today", analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		analysisVO.setSystemId(systemId);
		
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		
		
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
		
		
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
 
		addData("total", Long.toString(total),result);
		addData("handled", Long.toString(handled),result);
		addData("unhandle", Long.toString(unhandle),result);
 
		return ResultUtil.success(result);
	}
	
	
	private  List<String> getWeightEnum(){
		List<String> list = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			list.add(String.valueOf(i));
		}
		return list;
	}
	
 
	
	@GetMapping(value = "/getAlarmEventLevel/{timeType}")
	@ApiOperation(value = "获得安全等级统计", notes = "")
	@SysRequestLog(description = "获得安全等级统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmEventLevel(@PathVariable("timeType") String timeType ) {
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}

		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}

		SearchField searchField=new SearchField("weight",FieldType.String,null);
 
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		Long one=0L,two=0L,three=0L,four=0L,five=0L;
		 

		
		for(Map<String, Object> map : eventAlarmStatistics) {
			 String weight = map.get("weight").toString();
			 long count = Long.parseLong( map.get("doc_count").toString());
			switch (weight) {
			case "1":
				one += count;
				break;
			case "2":
				two += count;
				break;
			case "3":
				three += count;
				break;
			case "4":
				four += count;
				break;
			case "5":
				five += count;
				break;
			default:
				break;
			}
		 }
		
		List<Map<String, Object>> result = new ArrayList<>();

		addData("1", one, result);
		addData("2", two, result);
		addData("3", three, result);
		addData("4", four, result);
		addData("5", five, result);
		return ResultUtil.success(result);
	}
	
	@GetMapping(value = "/getAlarmEventLevel/{systemId}/{timeType}")
	@ApiOperation(value = "获得安全等级统计", notes = "")
	@SysRequestLog(description = "获得安全等级统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmEventLevel(@PathVariable("systemId") String systemId,@PathVariable("timeType") String timeType) {

		AnalysisVO analysisVO = new AnalysisVO();
		analysisVO.setSystemId(systemId);

		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		} catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}

		SearchField searchField = new SearchField("weight", FieldType.String, null);

		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO,
				searchField);

		List<Map<String, Object>> result = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			Object doc_count = 0;
			for (Map<String, Object> map : eventAlarmStatistics) {
				if (map.get("weight").toString().equals(Integer.toString(i))) {
					doc_count = map.get("doc_count");
					break;
				}
			}
			addData(Integer.toString(i), doc_count, result);
		}
		return ResultUtil.success(result);
	}
	
	
	@GetMapping("/getAlarmGroupStatistics/{timeType}")
	@ApiOperation(value="获取告警统计",notes="统计出木马病毒，入侵攻击，脚本注入，尝试破解，漏洞攻击，信息篡改，信息探测，DDOS攻击")
	@SysRequestLog(description = "获取告警统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmStatistics(@PathVariable("timeType") String timeType){
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
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		
		List<Map<String, Object>>  result=new ArrayList<>();
		for (String item : items) {
			Long count=0L;
			
			if(eventNames.containsKey(item)) {
			
			  List<String> list = eventConfigs.get(item);
			   for(Map<String, Object> statistics : eventAlarmStatistics) {
				   String riskEventCode = statistics.get("riskEventCode").toString();
				   if(list.contains(riskEventCode)) {
						Object object = statistics.get("doc_count");
						if (object != null) {
							count += Long.parseLong(object.toString());
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
	@GetMapping("/getAlarmGroupStatistics/{systemId}/{timeType}")
	@ApiOperation(value="获取告警统计",notes="统计出木马病毒，入侵攻击，脚本注入，尝试破解，漏洞攻击，信息篡改，信息探测，DDOS攻击")
	@SysRequestLog(description = "获取告警统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String, Object>>> getAlarmGroupStatistics(@PathVariable("systemId") String systemId,@PathVariable("timeType") String timeType){
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

		String[]   items= {"木马病毒","入侵攻击","脚本注入","尝试破解","漏洞攻击","信息篡改","信息探测","DDOS攻击"};

		Map<String, List<String> > eventConfigs=EventConfig.eventConfigList;
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
		analysisVO.setSystemId(systemId);
 
		SearchField searchField=new SearchField("riskEventCode",FieldType.String,null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		
		List<Map<String, Object>>  result=new ArrayList<>();
		for (String item : items) {
			Long count=0L;
			
			if(eventNames.containsKey(item)) {
			
			  List<String> list = eventConfigs.get(item);
			   for(Map<String, Object> statistics : eventAlarmStatistics) {
				   String riskEventCode = statistics.get("riskEventCode").toString();
				   if(list.contains(riskEventCode)) {
						Object object = statistics.get("doc_count");
						if (object != null) {
							count += Long.parseLong(object.toString());
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
	
	
	
	@GetMapping(value="/getAlarmTrendBy24H")
	@ApiOperation(value="24小时告警趋势",notes="")
	@SysRequestLog(description = "24小时告警趋势", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmTrendBy24H( ){
		
		AnalysisVO analysisVO=new AnalysisVO();
		analysisVO.setFlag("day");

		try {
			baseSysinfoService.setTimes("24h", analysisVO);
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		String groupBy = "triggerTime";
		String format = "HH";
		DateHistogramInterval timeInterval =   DateHistogramInterval.HOUR;

		SearchField triggerTime_day = new SearchField(groupBy, FieldType.Date, format, timeInterval, null,0,50); 
		
			
		List<Map<String, Object>> dstIpsData = warnResultForESService.eventAlarmStatistics(analysisVO,triggerTime_day);
 
		SocUtil.dealAnalysisResult(dstIpsData, GwParamsUtil.getAllElement(0,24),null,"triggerTime");
		 
		List<Map<String,Object>> list=new LinkedList<>();
		int hore=(new Date()).getHours();
		String str = String.format("%02d", hore);
		
		String today = DateUtil.format(new Date(), "yyyy-MM-dd ");
		String yesterday = DateUtil.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd ");
		
		Boolean asc=true;
		int index=0;
		for(Map<String,Object> map :dstIpsData) {
			String triggerTime = map.get("triggerTime").toString();

			
			String  time=triggerTime;
			if(Integer.parseInt(triggerTime)>=0&&Integer.parseInt(triggerTime)<=hore) {
				time=today+triggerTime;
			}else {
				time=yesterday+triggerTime;
			}
			if(asc) {
				addData(time, map.get("doc_count"), list);
			}else {
				addData(time, map.get("doc_count"), list,index);
				index++;
			}
			if(triggerTime.equals(str)) {
				asc=!asc;
			}
			
		}
 
		
		
		return ResultUtil.success(list);
	}
	
	
	@GetMapping(value="/getAlarmTrend/{timeType}")
	@ApiOperation(value="告警趋势(海油定制)",notes="")
	@SysRequestLog(description = "告警趋势(海油定制)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmTrend(@PathVariable("timeType") String timeType ){
		
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

		String timeFormat = getTimeFormat(timeType, analysisVO);
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
	
	private void addData(String key,Object value ,List<Map<String, Object>> result,int index) {
		Map map=new HashMap<>();
		map.put("name", key);
		map.put("value", value);
		result.add(index,map);
	}
	
	@GetMapping(value="/getAlarmTrendBy24H/{systemId}")
	@ApiOperation(value="24小时告警趋势",notes="")
	@SysRequestLog(description = "24小时告警趋势", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmTrendBy24H(@PathVariable("systemId") String systemId){
		
		AnalysisVO analysisVO=new AnalysisVO();
		analysisVO.setFlag("day");
		analysisVO.setSystemId(systemId);
//		Date now=new Date();
//		analysisVO.setStime(DateUtil.format(DateUtils.addDays(now, -1), "yyyy-MM-dd HH:mm:ss"));//前推1天
//		analysisVO.setEtime(DateUtil.format(now));
		
		try {
			baseSysinfoService.setTimes("24h", analysisVO);
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		
		Result<List<Map<String,Object>>> result = warnResultForESService.getAlarmTrendBy7Days(analysisVO);
		
		
		List<Map<String,Object>> list=new LinkedList<>();
		int hore=(new Date()).getHours();
		String str = String.format("%02d", hore);
		Boolean asc=true;
		int index=0;
		for(Map<String,Object> map :result.getData()) {
			String triggerTime = map.get("triggerTime").toString();
			if(asc) {
				list.add(map);
			}else {
				list.add(index,map);
				index++;
			}
			if(triggerTime.equals(str)) {
				asc=!asc;
			}
		}
		result.setData(list);
		return result;
	}
	
	
	@GetMapping(value="/getAnalysisInfoByArea/{timeType}")
	@ApiOperation(value="告警按区域统计",notes="")
	@SysRequestLog(description = "告警按区域统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAnalysisInfoByArea(@PathVariable("timeType") String timeType){
		
		AnalysisVO analysisVO=new AnalysisVO();
		//Date now=new Date();
		//analysisVO.setStime(DateUtil.format(DateUtils.addDays(now, -29), "yyyy-MM-dd 00:00:00"));//前推29天
		//analysisVO.setEtime(DateUtil.format(now));
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
	    //List<Map<String, Object>> analysisInfoByArea = warnResultForESService.getAnalysisInfoByArea(analysisVO);
		
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		SearchField searchField = new SearchField("srcAreaName", FieldType.String, null);
		 
		List<Map<String, Object>> dstIpsData = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		return ResultUtil.success(dstIpsData);
	}
	
	@GetMapping(value="/getAnalysisInfoByArea/{systemId}/{timeType}")
	@ApiOperation(value="告警按区域统计",notes="")
	@SysRequestLog(description = "告警按区域统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAnalysisInfoByArea(@PathVariable("systemId") String systemId
			,@PathVariable("timeType") String timeType){
		
		AnalysisVO analysisVO=new AnalysisVO();
		//Date now=new Date();
		//analysisVO.setStime(DateUtil.format(DateUtils.addDays(now, -29), "yyyy-MM-dd 00:00:00"));//前推29天
		//analysisVO.setEtime(DateUtil.format(now));
		
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		analysisVO.setSystemId(systemId);
		
		
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		SearchField searchField = new SearchField("srcAreaName", FieldType.String, null);
		
		
	    List<Map<String, Object>> analysisInfoByArea = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		return ResultUtil.success(analysisInfoByArea);
	}
	
	@GetMapping("/getAlarmGroupStatistics2/{timeType}")
	@ApiOperation(value="获取告警分组统计",notes="统计出爬虫，跨域脚本，恶意文件，DDOS攻击，信息探测，信息篡改，sql注入，其他")
	@SysRequestLog(description = "获取告警分组统计", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAlarmGroupStatistics2(@PathVariable("timeType") String timeType){
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
		
		
		
		String[]   items= {"爬虫","跨域脚本","恶意文件","DDOS攻击","信息探测","信息篡改","sql注入","其他"};
		 
		Map<String, List<String> > eventConfigs=EventConfig.eventConfigList;
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
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO, searchField);
		
		
		List<Map<String, Object>>  result=new ArrayList<>();
		for (String item : items) {
			Long count = 0L;

			List<String> list = eventConfigs.get(item);
			for (Map<String, Object> statistics : eventAlarmStatistics) {

				String riskEventCode = statistics.get("riskEventCode").toString();

				if (list.contains(riskEventCode)) {
					Object object = statistics.get("doc_count");
					if (object != null) {
						count += Long.parseLong(object.toString());
					}
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", item);
			map.put("value", count);
			map.put("group", eventNames.get(item));
			result.add(map);
		}
		
		

		return ResultUtil.success(result);
	}
	@GetMapping("/getAlarmGroupStatistics2/{systemId}/{timeType}")
	@ApiOperation(value="获取告警分组统计",notes="统计出爬虫，跨域脚本，恶意文件，DDOS攻击，信息探测，信息篡改，sql注入，其他")
	@SysRequestLog(description = "获取告警分组统计", actionType = ActionType.SELECT, manually = false)
	public Result<Map<String,String>> getAlarmGroupStatistics2(@PathVariable("systemId") String systemId,@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		//Date now=new Date();
		//analysisVO.setStime(DateUtil.format(DateUtils.addDays(now, -29), "yyyy-MM-dd 00:00:00"));//前推29天
		//analysisVO.setEtime(DateUtil.format(now));
		
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
		
		
		Map<String,String> map=new HashMap<>();
		String[]   items= {"爬虫","跨域脚本","恶意文件","DDOS攻击","信息探测","信息篡改","sql注入","其他"};
		Map<String, String[]> eventConfigs = EventConfig.eventConfigArr;
		for(String item  :  items) {
			
			//analysisVO.setRuleName(item);
			
			if (eventConfigs.containsKey(item)) {

				String[] eventcodes = eventConfigs.get(item);
				analysisVO.setRiskEventCodeArr(eventcodes);

				analysisVO.setSystemId(systemId);;
				Result<Long> taodayTotalResult = warnResultForESService.eventAlarmTotal(analysisVO);
				if (taodayTotalResult.getCode() == 0) {
					map.put(item, taodayTotalResult.getData().toString());
				} else {
					return ResultUtil.error(taodayTotalResult.getCode(), taodayTotalResult.getMsg());
				}
			} else {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "找不到对应的配置:" + item);
			}
		}

		return ResultUtil.success(map);
	}

    @GetMapping("/getAttackedAppSystem/{timeType}")
    @ApiOperation(value="被攻击应用top5",notes="")
	@SysRequestLog(description = "被攻击应用top5", actionType = ActionType.SELECT, manually = false)
    public Result<List<Map<String,Object>>> getAttackedAppSystem(@PathVariable("timeType") String timeType){
        return this.getAttackedAppSystemTop5(timeType);
    }

	//Attacked application system
	@GetMapping("/getAttackedAppSystemTop5/{timeType}")
	@ApiOperation(value="被攻击应用top5",notes="")
	@SysRequestLog(description = "被攻击应用top5", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAttackedAppSystemTop5(@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		List<Map<String, Object>> dstIpsData =new LinkedList<>();
		SearchField searchField = new SearchField("appSystemInfo.appIds", FieldType.String,0,10000, null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO,
				searchField);
		 Map<String,Object> countMap=new HashMap<>();
		 List<String> systemIds=new ArrayList<>();
		 for(Map<String, Object> map : eventAlarmStatistics) {
			 String systemId=map.get("appSystemInfo.appIds").toString();
			 map.remove("appSystemInfo.appIds");
			if(systemId.contains(",")){
				String[] ids=systemId.split(",");
				for(String id :ids){
						if(countMap.containsKey(id)){
							int count=Integer.valueOf(map.get("doc_count").toString())+ Integer.valueOf(countMap.get(id).toString());
							countMap.put(id,count);
						}else{
							countMap.put(id,map.get("doc_count"));
						}
						Map<String,Object> map1=new HashMap<>();
						mapperUtil.copy(map,map1);
						constructGroupMap(map1, id);
						if(!systemIds.contains(id)){
							map1.put("systemId", id);
							dstIpsData.add(map1);
							systemIds.add(id);
						}

				}
			}else{
				if(countMap.containsKey(systemId)){
					int count=Integer.valueOf(map.get("doc_count").toString())+ Integer.valueOf(countMap.get(systemId).toString());
					countMap.put(systemId,count);
				}else{
					countMap.put(systemId,map.get("doc_count"));
				}
				map.put("systemId", systemId);
				constructGroupMap(map, systemId);
				if(!systemIds.contains(systemId)){
					dstIpsData.add(map);
					systemIds.add(systemId);
				}

			}
			 map.remove("doc_count");
		 }
		 for(Map<String,Object> map:dstIpsData){
		 	String key=map.get("systemId").toString();
		 	if(countMap.containsKey(key)){
				map.put("value",countMap.get(key));
			}
		 }
		Collections.sort(dstIpsData, new Comparator<Map<String, Object>>(){
			@Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return o1.get("value").toString().compareTo(o2.get("value").toString());
			}

		});
		if(dstIpsData.size()>5){
			dstIpsData=dstIpsData.subList(dstIpsData.size()-5,dstIpsData.size());
		}
		return ResultUtil.success(dstIpsData);
				
	}



	private void constructGroupMap(Map<String, Object> map, String systemId) {
		for(BaseSysinfo item : getAllApplication()) {
			if(item.getId().toString().equals(systemId)) {//这里匹配的是 appiID
				map.put("name", item.getName());
				map.put("dstIps", item.getIps());
				map.put("ip", item.getIp());
				break;
			}
		}
	}


	@GetMapping("/getAttackedAppSystemForSeaoil/{timeType}/{top}")
	@ApiOperation(value="被攻击应用(海油定制)",notes="")
	@SysRequestLog(description = "被攻击应用(海油定制)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAttackedAppSystemForSeaoil(@PathVariable("timeType") String timeType,@PathVariable("top") int top){
		AnalysisVO analysisVO=new AnalysisVO();
		
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		//List<Map<String, Object>> dstIpsData = warnResultForESService.getDstIpsData(analysisVO);
		
		//
		
		List<Map<String, Object>> dstIpsData =new LinkedList<>();
		SearchField searchField = new SearchField("appSystemInfo.appIds", FieldType.String,0,top, null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO,
				searchField);
		 int index=0;
		 for(Map<String, Object> map : eventAlarmStatistics) {
			 
			 String systemId=map.get("appSystemInfo.appIds").toString();
			 map.put("systemId", systemId);
			 map.remove("appSystemInfo.appIds");
			 
			map.put("value", map.get("doc_count"));
			map.remove("doc_count");

			 for(BaseSysinfo item : getAllApplication()) {
				 if(item.getId().toString().equals(systemId)) {//这里匹配的是 appiID
						map.put("name", item.getName());
						map.put("dstIps", item.getIps());
						map.put("ip", item.getIp());
						break;
				 }
			 }
			 dstIpsData.add(map);
			 index++;
		 	if(index==5) {
		 		break;
		 	}
		 }
		return ResultUtil.success(dstIpsData);
				
	}
	
	
	@GetMapping("/getAttackedAppSystem/{systemId}/{timeType}")
	@ApiOperation(value="被攻击应用",notes="")
	@SysRequestLog(description = "被攻击应用", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAttackedAppSystem(@PathVariable("systemId") String systemId,@PathVariable("timeType") String timeType){
		AnalysisVO analysisVO=new AnalysisVO();
		analysisVO.setSystemId(systemId);
		
		try {
			baseSysinfoService.addUserAppPermissions(analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		Result<Long> taodayTotalResult = warnResultForESService.eventAlarmTotal(analysisVO);
		
		List<Map<String,Object>> result=new ArrayList<>();
		Map<String,Object> map=new HashMap<>();
		map.put("value", taodayTotalResult.getData());
		 
	 	for(BaseSysinfo item : getAllApplication()) {
			if(item.getSystemId().equals(systemId)) {//这里匹配的是 systemId
				map.put("name", item.getName());
				map.put("ip", item.getIp());
				map.put("systemId", systemId);
				map.put("dstIps", item.getIps());
				break;
			}
		}
		result.add(map);
		return ResultUtil.success(result);
				
	}
	
	/**
	 * 30天内访问前5名
	 * @return
	 */
	@GetMapping("/getAppVisit/{top}/{timeType}")
	@ApiOperation(value="30天内访问前5名",notes="返回的数据集合中，字段有id,name,value(visit_count)")
	@SysRequestLog(description = "30天内访问前5名", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAppVisit(@PathVariable("top") Integer top,@PathVariable("timeType") String timeType){
		List<Map<String, Object>> appVisitTopFor30d = appSystemService.getAppVisitTop(timeType);
		
		List<Map<String, Object>> result=new LinkedList<>();
		 List<BaseSysinfo> allApplication = getAllApplication();
		 logger.debug("用户管理安全域数量为："+allApplication.size());
		 for(Map<String, Object> item : appVisitTopFor30d) {
				for(BaseSysinfo  app: allApplication) {
					//app_id
					if(item.get("app_id").toString().equals(app.getSystemId())) {
						Map<String, Object> map=new HashMap<>();
						map.put("id", app.getSystemId());
						//map.put("ip", app.getIp());
						map.put("name", app.getName());
						map.put("value", item.get("doc_count"));
						if(result.size()<top) {
							result.add(map);
							break;
						}else {
							return ResultUtil.success(result);
						}
					}
				}
		 }
		return ResultUtil.success(result);
	}
	
	@GetMapping("/getAppVisit/{app_id}/{top}/{timeType}")
	@ApiOperation(value="30天内访问来源前5名",notes="返回的数据集合中，字段有name(ip),value(visit_count)")
	@SysRequestLog(description = "30天内访问来源前5名", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getAppVisit(@PathVariable("app_id") String  app_id,@PathVariable("top") Integer top,@PathVariable("timeType") String timeType){
		List<Map<String, Object>> appVisitTopFor30d = appSystemService.getAppVisitTop(app_id,timeType);
		
		List<Map<String, Object>> result=new LinkedList<>();

		for (Map<String, Object> item : appVisitTopFor30d) {
 
			if (result.size() < top) {
				addData(item.get("terminal_id").toString(), item.get("doc_count"), result);
			} else {
				return ResultUtil.success(result);
			}

		}
		return ResultUtil.success(result);
	}
	
	
	/**
	 * 访问设备数
	 * @param appId
	 * @return
	 */
	@GetMapping("/getUV/{appId}/{timeType}")
	@ApiOperation(value="访问设备数",notes="")
	@SysRequestLog(description = "访问设备数", actionType = ActionType.SELECT, manually = false)
	public  Result<Integer> getUV(@PathVariable("appId") String appId,@PathVariable("timeType") String timeType){
		
		return ResultUtil.success(appSystemService.getUV(appId, timeType));
	}
	
 
	@GetMapping("/getPV/{appId}/{timeType}")
	@ApiOperation(value=" 访问次数",notes="")
	@SysRequestLog(description = "访问次数", actionType = ActionType.SELECT, manually = false)
	public  Result<Long> getPV(@PathVariable("appId") String appId,@PathVariable("timeType") String timeType){
		
		return ResultUtil.success(appSystemService.getPV(appId,timeType));
	}
	
	/**
	 * 访问来源ip top10
	 * @param appId
	 * @return
	 */
 
	@GetMapping("/getVisitTop10BySrcIP/{appId}/{timeType}")
	@ApiOperation(value="访问来源ip top10",notes="可能存在时间差8个小时的问题")
	@SysRequestLog(description = "访问来源ip top10", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getVisitTop10BySrcIP(@PathVariable("appId") String appId,@PathVariable("timeType") String timeType){
		
		List<Map<String, Object>> visitTop10For30dBySrcIP = appSystemService.getVisitTop10BySrcIP(appId,timeType);
		
		List<Map<String,Object>>  result=new ArrayList<>();
		for(Map<String, Object> map : visitTop10For30dBySrcIP) {
			addData(map.get("terminal_id").toString(), map.get("doc_count"), result);
		}
		return ResultUtil.success(result);
	}
	
	@GetMapping("/getVisitTop10BySrcIP/{timeType}")
	@ApiOperation(value="访问来源ip top10",notes="可能存在时间差8个小时的问题")
	@SysRequestLog(description = "访问来源ip top10", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getVisitTop10BySrcIP(@PathVariable("timeType") String timeType){
		
		List<Map<String, Object>> visitTop10For30dBySrcIP = appSystemService.getVisitTop10BySrcIP(null,timeType);
		
		List<Map<String,Object>>  result=new ArrayList<>();
		for(Map<String, Object> map : visitTop10For30dBySrcIP) {
			addData(map.get("terminal_id").toString(), map.get("doc_count"), result);
		}
		return ResultUtil.success(result);
	}
	
	@GetMapping("/getVisitTop10ByHH/{appId}/{srcIp}/{timeType}")
	@ApiOperation(value="访问时间段 top10  (30天内)",notes="可能存在时间差8个小时的问题")
	@SysRequestLog(description = "访问时间段 top10  (30天内)", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getVisitTop10For30dBySrcIP(@PathVariable("appId") String appId,@PathVariable("srcIp") String srcIp,@PathVariable("timeType") String timeType){
 
		List<Map<String, Object>> visitTop10For30dBySrcIP = appSystemService.getVisitTop10ByHH(appId,srcIp,timeType);
		
		List<Map<String,Object>>  result=new ArrayList<>();
		for(Map<String, Object> map : visitTop10For30dBySrcIP) {
			addData(map.get("indate").toString(), map.get("doc_count"), result);
		}
		return ResultUtil.success(result);
	}
	
	
	
	@PostMapping(value="/getEventTypeStatistics/{count}")
	@ApiOperation(value="告警分类查询接口",notes="")
	@SysRequestLog(description = "告警分类查询接口", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getEventTypeStatistics(@PathVariable("count") Integer count,
			@RequestBody EventTypeSearchVO searchVo) {

		AnalysisVO analysisVO = new AnalysisVO();
		
		try {
			baseSysinfoService.addUserPermissions (analysisVO);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		
		if (searchVo.getWeight() != null) {
			analysisVO.setWeight(Integer.toString(searchVo.getWeight()));
		}

		if (!StringUtils.isEmpty(searchVo.getEvent_group())) {
			String event_group = searchVo.getEvent_group();
			Map<String, String> eventNames = EventConfig.eventNames;
			if (eventNames.values().contains(event_group)) {
				Map<String, String[]> eventConfigArr = EventConfig.eventConfigArr;
				for (String key : eventNames.keySet()) {
					if (eventNames.get(key).equals(event_group)) {
						String[] strings = eventConfigArr.get(key);
						analysisVO.setRiskEventCodeArr(strings);
						break;
					}
				}

			} else {
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "事件分类传参异常");
			}
		}

		if (!StringUtils.isEmpty(searchVo.getStatusEnum())) {
			if (!"all".equals(searchVo.getStatusEnum().toLowerCase())) {
				analysisVO.setStatusEnum(searchVo.getStatusEnum());
			}
		}

		if (!StringUtils.isEmpty(searchVo.getStime()) && !StringUtils.isEmpty(searchVo.getEtime())) {
			analysisVO.setStime(searchVo.getStime());
			analysisVO.setEtime(searchVo.getEtime());
		}
		int index = 0;
		List<Map<String, Object>> result = new ArrayList<>();
		if (searchVo.getIsApp() != null && searchVo.getIsApp()) {
			analysisVO.setLinkApp(true);
		}
		
		if (!StringUtils.isEmpty(searchVo.getSystemId())) {
			analysisVO.setSystemId(searchVo.getSystemId());
		}
		

		SearchField searchField = new SearchField("riskEventName", FieldType.String, null);
		List<Map<String, Object>> eventAlarmStatistics = warnResultForESService.eventAlarmStatistics(analysisVO,
				searchField);

		for (Map<String, Object> map : eventAlarmStatistics) {
			String riskEventName = map.get("riskEventName").toString();
			Long toatl = 0L;
			toatl += Long.parseLong(map.get("doc_count").toString());
			if (index < count) {
				addData(riskEventName, toatl, result);
				index++;
			} else {
				break;
			}

		}

		return ResultUtil.success(result);

	}
	
	@GetMapping(value="/getMapData/{mapType}/{timeType}")
	@ApiOperation(value="大屏地图数据",notes="mapType 传参意义：1州  2国家 3省份  4城市")
	@SysRequestLog(description = "大屏地图数据", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getMapData(@PathVariable("mapType") Integer mapType,@PathVariable("timeType") String timeType){
		
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
			baseSysinfoService.addUserAppPermissions (analysisVO);
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
				Long toatl=0L;
				toatl+=Long.parseLong(childMap.get("doc_count").toString());

				item.put("count", toatl);
				if(toatl>0) {
					result.add(item);
				}
			}
		}
	  
		return ResultUtil.success(result);
	}
	
	@GetMapping(value="/getMapData/{mapType}/{systemId}/{timeType}")
	@ApiOperation(value="大屏地图数据",notes="mapType 传参意义：1州  2国家 3省份  4城市")
	@SysRequestLog(description = "大屏地图数据", actionType = ActionType.SELECT, manually = false)
	public Result<List<Map<String,Object>>> getMapData(@PathVariable("mapType") Integer mapType
			,@PathVariable("systemId") String systemId
			,@PathVariable("timeType") String timeType){
		
		AnalysisVO analysisVO=new AnalysisVO();
 
		try {
			baseSysinfoService.setTimes(timeType, analysisVO);
		} catch (Exception e1) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e1.getMessage());
		}
		
		analysisVO.setSystemId(systemId);
		
		try {
			baseSysinfoService.addUserAppPermissions (analysisVO);
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
				Long count=0L;
				count+=Long.parseLong(childMap.get("doc_count").toString());  
				
/*				Map<String, Object>  item=new HashMap<String, Object>();
				item.put("from", map.get("srcWorldMapName"+key));
				item.put("to", childMap.get("dstWorldMapName"+key));
				item.put("count", childMap.get("doc_count"));
				*/
				if(count>0) {
					item.put("count",count);
					result.add(item);
				}
			}
		}
	  
		return ResultUtil.success(result);
	}
	
	
	
	private void addData(String key,Object value ,List<Map<String, Object>> result) {
		Map map=new HashMap<>();
		map.put("name", key);
		map.put("value", value);
		result.add(map);
	}
}

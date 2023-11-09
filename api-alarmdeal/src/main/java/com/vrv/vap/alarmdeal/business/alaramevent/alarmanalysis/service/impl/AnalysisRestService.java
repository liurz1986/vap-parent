package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.BaseSysinfoService;
import com.vrv.vap.alarmdeal.business.analysis.model.AreaLocation;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.AreaLocationService;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.TimeCountService;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealTypeEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.enums.AppStatusEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.enums.CoreAppEnum;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.IndexsInfoVO;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;

/**
 * 楚天云第三方插件接口
 * @author wd-pc
 *
 */
@Service
public class AnalysisRestService extends ElasticSearchRestClientService<WarnResultLogTmpVO> {
    
	private static Logger logger = LoggerFactory.getLogger(AnalysisRestService.class);
	public static final String WARN_RESULT_TMP = "warnresulttmp";
	Gson  gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();
	
	@Autowired
	private TimeCountService timeCountService;
	@Autowired
	private BaseSysinfoService baseSysinfoService;
	@Autowired
	private WarnResultForESService warnResultForEsService;
	@Autowired
	private AreaLocationService areaLocationService;
	
	@Override
	public String getIndexName() {
		return WARN_RESULT_TMP;
	}

	
	public Result<Long> getVirtulAlarmInfosByInterrupt(AnalysisVO analysisVO) {
		String stime = analysisVO.getStime();
		String etime = analysisVO.getEtime();
		List<String> days = DateUtil.getDatesBetweenDays(stime, etime, DateUtil.Year_Mouth_Day);
		timeCountService.saveTimeCountList(days); //保存数据
		long count = getTotalTimeCountByDate(days, analysisVO.getAttackFlag());
		Result<Long> result = ResultUtil.success(count);
		return result;
	}
	
	public Result<Long> getDealedAnalysisFakeCount(AnalysisVO analysisVO) {
		String stime = analysisVO.getStime();
		String etime = analysisVO.getEtime();
		List<String> days = DateUtil.getDatesBetweenDays(stime, etime, DateUtil.Year_Mouth_Day);
		timeCountService.saveTimeCountList(days); //保存数据
		long count = getTotalTimeCountByDate(days, analysisVO.getAttackFlag());
		Result<Long> result = ResultUtil.success(count);
		return result;
	}
	
	private long getTotalTimeCountByDate(List<String> days,String attackFlag){
		long total =0L;
		for (String day : days) {
			Integer timeCountByDate = timeCountService.getTimeCountByDate(day);
			String startTime = day+" 00:00:00";
			String endTime = day+" 23:59:59";
			List<QueryCondition_ES> conditions = new ArrayList<>();
			conditions.add(QueryCondition_ES.between("triggerTime", startTime, endTime));
			if(StringUtils.isNotEmpty(attackFlag)){
				conditions.add(QueryCondition_ES.eq("attackFlag", attackFlag));
			}
			long count = count(conditions);
			long countValue = count-timeCountByDate;
			if(countValue>0){
				total+=countValue;
			}else{
				total+=count;
			}
		}
		return total;
	}
	
	 /**
	   * 获得已处理的事件数
	   * @param analysisVO
	   * @return
	   */
	public Result<Long> getDealedAnalysisCount(AnalysisVO analysisVO){
		String stime = analysisVO.getStime();
		String etime = analysisVO.getEtime();
		List<QueryCondition_ES> conditions = new ArrayList<>();
		if(StringUtils.isNotEmpty(stime)&&StringUtils.isNotEmpty(etime)){
			conditions.add(QueryCondition_ES.between("triggerTime", stime, etime));
		}
		conditions.add(QueryCondition_ES.or(
				QueryCondition_ES.eq("statusEnum",AlarmDealTypeEnum.ERROR_REPORT.getIndex()), 
				QueryCondition_ES.eq("statusEnum", AlarmDealTypeEnum.END.getIndex()), 
				QueryCondition_ES.eq("statusEnum", AlarmDealTypeEnum.INTERRUPT.getIndex()),
				QueryCondition_ES.eq("statusEnum", AlarmDealTypeEnum.IGNORE.getIndex())
				));
		long count = count(conditions);
		Result<Long> result = ResultUtil.success(count);
		return result;
	}
	
	
	/**
	 * 获得核心应用系统数据
	 * @return
	 */
	public Result<List<Map<String, Object>>> getCoreSystemInfo() {
		List<Map<String, Object>> list = new ArrayList<>();
		String[] ruleIdArr = new String[] {"8338fd717d4845f19eb55ad596cf3328"}; //TODO 对应失陷痕迹规则
		Map<String,Object> map = new HashMap<>();
		for (CoreAppEnum coreAppEnum : CoreAppEnum.values()) {
			String[] systemIds = coreAppEnum.getSystemIds();
			Set<String> ipList = baseSysinfoService.getIpList(systemIds); //IP集合
			List<String> result = new ArrayList<>(ipList);
			Integer coreAppStatus = getCoreAppStatus(result, ruleIdArr);
			map.put(coreAppEnum.getCode(), coreAppStatus);
		}
		list.add(map);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}

	
	/**
     * 获得核心应用系统状态
     * @param ipList
     * @param ruleIdArr
     * @return
     */
	public Integer getCoreAppStatus(List<String> ipList,String[] ruleIdArr){
		Boolean judgeIsFallStatus = judgeIsFallStatus(ipList, ruleIdArr);
		if(judgeIsFallStatus){ //失陷
			return AppStatusEnum.fall.getIndex();
		}else{ //
			Boolean judgeIsThreatStatus = judgeIsThreatStatus(ipList);
			if(judgeIsThreatStatus){
				return AppStatusEnum.threat.getIndex();
			}else {
				return AppStatusEnum.normal.getIndex();
			}
		}
		
	}
	
	/**
	 * 判断是否已经是失陷状态 true:失陷状态；false：未失陷状态
	 * @param ipList
	 * @param ruleIdArr
	 * @return
	 */
	private Boolean judgeIsFallStatus(List<String> ipList,String[] ruleIdArr){
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(QueryCondition_ES.in("dstIps", ipList));
		conditions.add(QueryCondition_ES.in("ruleId", ruleIdArr));
		conditions.add(QueryCondition_ES.eq("statusEnum", AlarmDealTypeEnum.ALREADY_SURE.getIndex()));
		long count = count(conditions);
		if(count>0) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 判断是否已经是威胁状态 true:威胁状态；false：正常状态状态
	 * @param ipList
	 * @return
	 */
	private Boolean judgeIsThreatStatus(List<String> ipList) {
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(QueryCondition_ES.in("dstIps", ipList));
		Date eTime = null;
		Date sTime = null;
		try {
			eTime = DateUtils.parseDate(DateUtil.format(new Date()), "yyyy-MM-dd HH:mm:ss");
			sTime = DateUtils.parseDate(DateUtil.format(DateUtil.addHours(new Date(), -24)), "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			logger.error("时间解析出现问题", e);
		}
		IndexsInfoVO indexsInfoVO = getIndexsInfoVO(sTime,eTime);
		long count = count(indexsInfoVO, conditions);
		if(count>0) {
			return true;
		}else {
			return false;
		}
	}
	
	private IndexsInfoVO getIndexsInfoVO(Date dtStart, Date dtEnd) {
		List<String> indexs=new ArrayList<>();
		String[] allIndex = getIndexListByBaseIndexName(getIndexName());
		if (dtStart != null && dtEnd != null) {
			Calendar cale = Calendar.getInstance();
			cale.setTime(dtEnd);
			cale.set(Calendar.DAY_OF_MONTH, 1);
			cale.set(Calendar.HOUR, 0);
			cale.set(Calendar.MILLISECOND, 0);
			cale.set(Calendar.SECOND, 0);
			cale.set(Calendar.MINUTE, 0);
			Date lastMonthFirstDay = cale.getTime();
			Date nextMonthLastDay = DateUtils.addMonths(lastMonthFirstDay, 1);
			for (Date temp = dtStart; temp.before(nextMonthLastDay)
					|| temp.getTime() == dtEnd.getTime(); temp = DateUtils.addDays(temp, 1)) {
				String index = getIndexName() + DateUtil.format(temp, "-yyyy-MM-dd");
				for (String indexKey : allIndex) {
					if (indexKey.equals(index)) {
						indexs.add(index);
					}
				}
			}
		}else {
			indexs.add(getIndexName()+"*");
		}
		
		logger.debug("成功匹配的索引有："+gson.toJson(indexs));
		IndexsInfoVO indexsInfoVO=new IndexsInfoVO();
		
		indexsInfoVO.setIndex(indexs.toArray(new String[indexs.size()]));
		return indexsInfoVO;
	}
	
	
	public Result<List<WarnResultLogTmpVO>> getAlarmTable(AnalysisVO analysisVO) {
		Integer count = analysisVO.getCount_();
		List<QueryCondition_ES> condition = warnResultForEsService.getCondition(analysisVO);
		List<WarnResultLogTmpVO> list = findAllBySize(condition, "triggerTime", "desc", count);
		Result<List<WarnResultLogTmpVO>> result = ResultUtil.success(list);
		return result;
	}
	
	
	public Result<List<Map<String, Object>>> getAttackSourceByRegionTop20(AnalysisVO analysisVO) {
		String  mapLevel = analysisVO.getMapLevel();
		String nameStr="";
		switch (mapLevel) {
		case "1":
			nameStr=".continent";
			break;
		case "2":
			nameStr=".country";
			break;
		case "3":
			nameStr=".province";
			break;
		case "4":
			nameStr=".city";
			break;
		default:
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "地图显示范围传参异常");
		}
		List<QueryCondition_ES> condition = warnResultForEsService.getCondition(analysisVO);
		SearchField searchField = new SearchField("srcWorldMapName"+nameStr, FieldType.String,0, 20, null);
		List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
		return ResultUtil.success(queryStatistics);
	}
	
	
	
	public Result<List<Map<String, Object>>> getMapDataByWarnResult(AnalysisVO analysisVO){
		String mapLevel = analysisVO.getMapLevel();
		String nameStr="";
		switch (mapLevel) {
		case "1":
			nameStr=".continent";
			break;
		case "2":
			nameStr=".country";
			break;
		case "3":
			nameStr=".province";
			break;
		case "4":
			nameStr=".city";
			break;
		default:
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "地图显示范围传参异常");
		}
		List<QueryCondition_ES> condition = getTimeCondition();
		SearchField child=new SearchField("dstWorldMapName"+nameStr, FieldType.String, null);
		SearchField searchField=new SearchField("srcWorldMapName"+nameStr, FieldType.String, child);
		List<Map<String,Object>> list = queryStatistics(condition, searchField);
		List<Map<String,Object>>  result=new  ArrayList<>();
		for(Map<String, Object> map : list)
		{
			if(map.get("srcWorldMapName"+nameStr)==null||map.get("dstWorldMapName"+nameStr)==null) {
				continue;
			}
			List<Map<String, Object>> childList=(List<Map<String, Object>>)map.get("dstWorldMapName"+nameStr);
			for(Map<String, Object> childMap : childList)
			{
				Map<String, Object>  item=new HashMap<String, Object>();
				String srcByAreaLocation = getLocationByAreaName(map.get("srcWorldMapName"+nameStr).toString());
				String dstByAreaLocation = getLocationByAreaName(childMap.get("dstWorldMapName"+nameStr).toString());
				if(StringUtils.isEmpty(dstByAreaLocation)) {
					dstByAreaLocation = "114.512104,30.561823"; 
				}
				if(StringUtils.isNotEmpty(srcByAreaLocation)&&StringUtils.isNotEmpty(dstByAreaLocation)){
					item.put("from", srcByAreaLocation);
					item.put("to", dstByAreaLocation);
					item.put("count", childMap.get("doc_count"));
					result.add(item);
				}
			}
		}
		return ResultUtil.success(result);
	}
	
	
	/**
	 * 获得对应的时间查询范围
	 * @return
	 */
	private List<QueryCondition_ES> getTimeCondition() {
		List<QueryCondition_ES> condition = new ArrayList<>();
		String startime = DateUtil.format(DateUtil.addMinutes(new Date(), -30));
		String endTime = DateUtil.format(new Date());
		condition.add(QueryCondition_ES.between("triggerTime", startime, endTime));
		return condition;
	}


	/**
	 * 根据区域名称获得区域的坐标
	 * @param areaName
	 * @return
	 */
	private String getLocationByAreaName(String areaName){
		String location = null;
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("areaName", areaName));
		List<AreaLocation> list = areaLocationService.findAll(conditions);
		if(list.size()>0){
			AreaLocation areaLocation = list.get(0);
			location = areaLocation.getLocation();
		}
		return location;
	}
	
	
	/**
	 * 近15天攻击拦截趋势
	 * @param analysisVO
	 * @return
	 */
	public Result<List<Map<String, Object>>> getAttackInterruptVirutalTrendByTimeRange(AnalysisVO analysisVO) {
		String stime = analysisVO.getStime();
		String etime = analysisVO.getEtime();
		List<String> days = DateUtil.getDatesBetweenDays(stime, etime, DateUtil.Year_Mouth_Day);
		Date startTime = null;
		Date endTime = null;
		if(StringUtils.isNotEmpty(stime)&&StringUtils.isNotEmpty(etime)) {
			List<QueryCondition_ES> condition = warnResultForEsService.getCondition(analysisVO);
			try {
				startTime = DateUtil.parseDate(stime, DateUtil.DEFAULT_DATE_PATTERN);
				endTime = DateUtil.parseDate(etime, DateUtil.DEFAULT_DATE_PATTERN);				
			}catch(ParseException e){
				throw new RuntimeException("时间解析报错!");
			}
			long interval = (long)24*60*60*1000;
			String format = "yyyy-MM-dd";
			String groupBy = "triggerTime";
			SearchField searchField = 	new SearchField(groupBy, FieldType.Date, format, interval, null);
			List<Map<String,Object>> list = queryStatistics(condition, searchField);
			timeCountService.saveTimeCountList(days);
			for (Map<String, Object> map : list) {
				if(map.containsKey("triggerTime")&&map.get("triggerTime")!=null&&map.containsKey("doc_count")&&map.get("doc_count")!=null){
					String triggerTime = map.get("triggerTime").toString();
					Long docCount = (Long)map.get("doc_count");
					Integer timeCountByDate = timeCountService.getTimeCountByDate(triggerTime);
					if(docCount-timeCountByDate>0){
						map.put("doc_count", docCount-timeCountByDate);
					}else {
						map.put("doc_count", docCount);
					}
				}
			}
			SocUtil.getTimeFullMap(startTime, endTime, interval, format, list);
			return ResultUtil.success(list);
		}else {
		   throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),"请传对应的时间参数!");
		}
	
	}
	
	

}

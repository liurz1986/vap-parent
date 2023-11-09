package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.BaseSysinfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AppSystemService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.IndexsInfoVO;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;

@Service
public class AppSystemServiceImpl extends ElasticSearchRestClientService<WarnResultLogTmpVO>  implements AppSystemService {

	
	private static Logger logger = LoggerFactory.getLogger(AppSystemServiceImpl.class);
	
	Gson  gson=new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
			.create();
	
	@Autowired
	BaseSysinfoService baseSysinfoService;
	
	
	@Override
	public String getIndexName() {
		 
		return "app-audit";
	}

	
	private Date getTodayZero() {

        Calendar calendar1 = Calendar.getInstance();

        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),

                0, 0, 0);

        Date beginOfDate = calendar1.getTime();
        return beginOfDate;
	}
	
	private Date getDayZero(Date dtEnd, int dayAdd) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(dtEnd);
		cale.set(Calendar.HOUR, 0);
		cale.set(Calendar.MILLISECOND, 0);
		cale.set(Calendar.SECOND, 0);
		cale.set(Calendar.MINUTE, 0);
		Date lastMonthFirstDay = cale.getTime();
		Date nextMonthLastDay = DateUtils.addDays(lastMonthFirstDay, dayAdd);
		return nextMonthLastDay;
	}
	
	
	private QueryCondition_ES getTimeQueryCondition(Date dtStart, Date dtEnd) {
		return QueryCondition_ES.between("indate",  format(dtStart), format(dtEnd));
	}
	
	
	/**
	 * 30天内被访问前10000名应用系统
	 */
	@Override
    public List<Map<String,Object>> getAppVisitTop(String timeType) {
		
		Date dtEnd =new Date();
		Date  dtStart=DateUtils.addDays(dtEnd, -29);
		try {
			
			if("all".equals(timeType)) {
				 dtEnd =new Date();
				 dtStart=DateUtils.addYears(dtEnd, -100);
			}else {
				AnalysisVO analysisVO = new AnalysisVO();
				baseSysinfoService.setTimes(timeType, analysisVO);
				if (StringUtils.isNotEmpty(analysisVO.getEtime()) && StringUtils.isNoneEmpty(analysisVO.getStime())) {
					dtEnd = DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
					dtStart = DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		IndexsInfoVO indexsInfoVO = getIndexsInfoVO(dtStart,dtEnd);
 
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(getTimeQueryCondition(dtStart,dtEnd));
		SearchField	riskEventNameField= new SearchField("app_id", FieldType.String,null);
		
		logger.debug("统计查询索引："+gson.toJson(indexsInfoVO));
		 
		logger.debug("统计查询分组字段："+gson.toJson(riskEventNameField));
		
		List<Map<String,Object>> list = queryStatistics(indexsInfoVO,conditions, riskEventNameField);
		
		if(list.size()>10) {
			logger.debug("统计查询结果："+gson.toJson(list.subList(0, 10)));	
		}else {
			logger.debug("统计查询结果："+gson.toJson(list));
		}
		return list;
	}
	
	/**
	 * 取30天内某个应用系统 访问来源排名
	 * @param app_id
	 * @return
	 */
	@Override
    public List<Map<String,Object>> getAppVisitTop(String app_id, String timeType) {
		
		Date dtEnd =new Date();
		Date  dtStart=DateUtils.addDays(dtEnd, -29);
		try {
			
			if("all".equals(timeType)) {
				 dtEnd =new Date();
				 dtStart=DateUtils.addYears(dtEnd, -100);
			}else {
				AnalysisVO analysisVO = new AnalysisVO();
				baseSysinfoService.setTimes(timeType, analysisVO);
				if (StringUtils.isNotEmpty(analysisVO.getEtime()) && StringUtils.isNoneEmpty(analysisVO.getStime())) {
					dtEnd = DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
					dtStart = DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		IndexsInfoVO indexsInfoVO = getIndexsInfoVO(dtStart,dtEnd);
 
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(getTimeQueryCondition(dtStart,dtEnd));
		conditions.add(QueryCondition_ES.eq("app_id", app_id));
		SearchField	riskEventNameField= new SearchField("terminal_id", FieldType.String,null);

		List<Map<String,Object>> list = queryStatistics(indexsInfoVO,conditions, riskEventNameField);
		return list;
	}
	
	/**
	 * 访问设备数
	 * @param appId
	 * @return
	 */
	@Override
    public  int getUV(String appId, String timeType) {
		
		Date dtEnd =new Date();
		Date  dtStart=getDayZero(dtEnd, -29);
		try {
			
			if("all".equals(timeType)) {
				 dtEnd =new Date();
				 dtStart=DateUtils.addYears(dtEnd, -100);
			}else {
				AnalysisVO analysisVO = new AnalysisVO();
				baseSysinfoService.setTimes(timeType, analysisVO);
				if (StringUtils.isNotEmpty(analysisVO.getEtime()) && StringUtils.isNoneEmpty(analysisVO.getStime())) {
					dtEnd = DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
					dtStart = DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		IndexsInfoVO indexsInfoVO = getIndexsInfoVO(dtStart,dtEnd);
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(getTimeQueryCondition(dtStart,dtEnd));
		conditions.add(QueryCondition_ES.eq("app_id", appId));
		SearchField	riskEventNameField= new SearchField("terminal_id", FieldType.String,null);
		List<Map<String,Object>> list = queryStatistics(indexsInfoVO,conditions, riskEventNameField);
		return list.size();
	}
	/**
	 * 访问次数
	 * @param appId
	 * @return
	 */
	@Override
    public  Long getPV(String appId, String timeType) {
		
		Date dtEnd =new Date();
		Date  dtStart=getDayZero(dtEnd, -29);
		try {
			
			if("all".equals(timeType)) {
				 dtEnd =new Date();
				 dtStart=DateUtils.addYears(dtEnd, -100);
			}else {
				AnalysisVO analysisVO = new AnalysisVO();
				baseSysinfoService.setTimes(timeType, analysisVO);
				if (StringUtils.isNotEmpty(analysisVO.getEtime()) && StringUtils.isNoneEmpty(analysisVO.getStime())) {
					dtEnd = DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
					dtStart = DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		IndexsInfoVO indexsInfoVO = getIndexsInfoVO(dtStart,dtEnd);
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(getTimeQueryCondition(dtStart,dtEnd));
		conditions.add(QueryCondition_ES.eq("app_id", appId));
		return  count(indexsInfoVO,conditions);
	}
	
	
	//访问来源ip top10
	
	@Override
    public List<Map<String,Object>>  getVisitTop10BySrcIP(String appId, String timeType){
		//terminal_id
		Date dtEnd =new Date();
		Date  dtStart=getDayZero(dtEnd, -29);
		try {
			
			if("all".equals(timeType)) {
				 dtEnd =new Date();
				 dtStart=DateUtils.addYears(dtEnd, -100);
			}else {
				AnalysisVO analysisVO = new AnalysisVO();
				baseSysinfoService.setTimes(timeType, analysisVO);
				if (StringUtils.isNotEmpty(analysisVO.getEtime()) && StringUtils.isNoneEmpty(analysisVO.getStime())) {
					dtEnd = DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
					dtStart = DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		IndexsInfoVO indexsInfoVO = getIndexsInfoVO(dtStart,dtEnd);
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(getTimeQueryCondition(dtStart,dtEnd));
		if(StringUtils.isNotBlank(appId)){
			conditions.add(QueryCondition_ES.eq("app_id", appId));
		}
		SearchField	riskEventNameField= new SearchField("terminal_id", FieldType.String,0,10,null);
		
		List<Map<String,Object>> list = queryStatistics(indexsInfoVO,conditions, riskEventNameField);
		
		return list;
	}
	
	@Override
    public List<Map<String,Object>>  getVisitTop10ByHH(String appId, String srcIp, String timeType){
		//terminal_id
		Date dtEnd =new Date();
		Date  dtStart=getDayZero(dtEnd, -29);
		try {
			
			if("all".equals(timeType)) {
				 dtEnd =new Date();
				 dtStart=DateUtils.addYears(dtEnd, -100);
			}else {
				AnalysisVO analysisVO = new AnalysisVO();
				baseSysinfoService.setTimes(timeType, analysisVO);
				if (StringUtils.isNotEmpty(analysisVO.getEtime()) && StringUtils.isNoneEmpty(analysisVO.getStime())) {
					dtEnd = DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
					dtStart = DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		IndexsInfoVO indexsInfoVO = getIndexsInfoVO(dtStart,dtEnd);
		List<QueryCondition_ES> conditions = new ArrayList<>();
		conditions.add(getTimeQueryCondition(dtStart,dtEnd));
		conditions.add(QueryCondition_ES.eq("app_id", appId));
		conditions.add(QueryCondition_ES.eq("terminal_id", srcIp));
 
		String groupBy = "indate";
		String format = "HH";
		DateHistogramInterval timeInterval =   DateHistogramInterval.HOUR;
		SearchField searchField = new SearchField(groupBy, FieldType.Date, format, timeInterval, null,0,50);
		
		List<Map<String,Object>> queryStatistics = queryStatistics(indexsInfoVO,conditions, searchField);
		
		return queryStatistics;
	}
	
	
	private	IndexsInfoVO getIndexsInfoVO(Date dtStart, Date dtEnd) {
		
		//logger.debug("索引匹配起始时间："+DateUtil.format(dtStart, "yyyy-MM-dd HH:mm:ss"));
		//logger.debug("索引匹配结束时间："+DateUtil.format(dtEnd, "yyyy-MM-dd HH:mm:ss"));
		
		Calendar cale = Calendar.getInstance();  
		cale.setTime(dtEnd);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		cale.set(Calendar.HOUR, 0);
		cale.set(Calendar.MILLISECOND, 0);
		cale.set(Calendar.SECOND, 0);
		cale.set(Calendar.MINUTE, 0);
		Date lastMonthFirstDay= cale.getTime();
		Date nextMonthLastDay= DateUtils.addMonths(lastMonthFirstDay, 1);
		
		
		//logger.debug("索引匹配结束时间当月第一天："+DateUtil.format(lastMonthFirstDay, "yyyy-MM-dd HH:mm:ss"));
		List<String> indexs=new ArrayList<>();
		String[] allIndex = getIndexListByBaseIndexName(getIndexName());
		
		for(Date temp=dtStart;temp.before(nextMonthLastDay)||temp.getTime()==dtEnd.getTime();temp=DateUtils.addMonths(temp, 1)) {
			//logger.debug("匹配时间为："+DateUtil.format(temp, "yyyy-MM-dd HH:mm:ss"));
			String index=getIndexName()+DateUtil.format(temp, "-yyyy.MM");
			//logger.debug("字段生成索引名称："+index);
			for(String indexKey : allIndex) {
				if(indexKey.equals(index)) {
					indexs.add(index);
				}
			}
		}
		
		logger.debug("成功匹配的索引有："+gson.toJson(indexs));
		
		IndexsInfoVO indexsInfoVO=new IndexsInfoVO();
		
		indexsInfoVO.setType(new String[] {"logs"});
		
		indexsInfoVO.setIndex(indexs.toArray(new String[indexs.size()]));
		return indexsInfoVO;
	}
	
	private long getCount(Date dtStart, Date dtEnd,List<QueryCondition_ES> querys) {
		long unparsed=0L;
		try {
			IndexsInfoVO indexsInfoVO= getIndexsInfoVO(dtStart,dtEnd);
			
			unparsed=count(indexsInfoVO,querys);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return unparsed;
	}
	
	
	public static String format(Date date) {
		SimpleDateFormat formatTool = new SimpleDateFormat();
		date=DateUtils.addHours(date, -8);
		formatTool.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return formatTool.format(date);
	}
	

}

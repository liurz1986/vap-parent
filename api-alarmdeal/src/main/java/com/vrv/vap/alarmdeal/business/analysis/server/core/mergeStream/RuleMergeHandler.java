package com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.StrategyEnum;
import com.vrv.vap.alarmdeal.business.analysis.vo.FieldInfoVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.RuleInfoVO;
import lombok.Data;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * * 
 * 
 * @author wudi   
 * E‐mail:wudi@vrvmail.com.cn  @version 
 * 创建时间：2018年9月26日 下午4:30:33
 * 类说明 规则合并流处理器
 */
@Data
public class RuleMergeHandler {

	private  static  final  String TIMETYPE="timeType"; //时间类型合并
	private  static  final  String STATUSTYPE="statusType"; //状态类型合并

	private static final Logger logger = LoggerFactory.getLogger(RuleMergeHandler.class);
    
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	private final Map<String, Object> ruleCache = new ConcurrentHashMap<>(); // 规则缓存
	
	private final Map<String,Integer> countCache = new ConcurrentHashMap<>(); //个数缓存
	

	private long nextTime; //下一次清除缓存的时间
	
	private FieldInfoVO fieldInfoVO;
	
	
	private RuleInfoVO ruleInfoVO; //规则信息VO
	
     

	/**
	 * 计算缓存规则（ES版本）
	 * @param warnResultLogVO
	 */
	public void handle(WarnResultLogTmpVO warnResultLogVO) {

			// 添加rule信息（ES版本）
			addRulefo(warnResultLogVO);

		// 	// 日志结构变成List
		// 	changeLogInfoConstructor(warnResultLogVO);
		//
		// // 计算对应的key值
		// String caculateKey = caculateKey(warnResultLogVO);
		//
		// // 更新缓存信息,进行对应的合并操作
		// refreshCache(warnResultLogVO, caculateKey);
	}
	
	/**
	 * 日志结构变成List<Map<String,Object>>
	 * @param warnResultLogVO
	 */
	public void changeLogInfoConstructor(WarnResultLogTmpVO warnResultLogVO){
		List<Map<String, Object>> mapList = new ArrayList<>();
		String logsInfo = warnResultLogVO.getLogsInfo();
		if(StringUtils.isNotEmpty(logsInfo)){
			try{
				Map<String,Object> fromJson = gson.fromJson(logsInfo, Map.class);
				mapList.add(fromJson);
				String map_list_str = gson.toJson(mapList);
				warnResultLogVO.setLogsInfo(map_list_str);			
			}catch(Exception e) {
				warnResultLogVO.setLogsInfo(logsInfo);
			}			
		}
	}
	
	/**
	 * 添加rule信息（ES版本）
	 * @param warnResultLogVO
	 */
	public void addRulefo(WarnResultLogTmpVO warnResultLogVO) {
		RuleInfoVO info = getRuleInfoVO();
		warnResultLogVO.setMultiVersions(info.getMuitlVersionStr());
		//20230913修改调整：将主键id换成resultGuid
		warnResultLogVO.setId(warnResultLogVO.getResultGuid());
		warnResultLogVO.setRiskEventName(info.getRiskEventName());
		warnResultLogVO.setEventtypelevel(info.getCodeLevel());
		warnResultLogVO.setRiskEventCode(info.getCodeLevel());
		warnResultLogVO.setRuleId(info.getRuleId());
		warnResultLogVO.setRuleName(info.getRuleName());
		warnResultLogVO.setStatusEnum(0);
		//warnResultLogVO.setWeight(Integer.valueOf(info.getWeight()));
		warnResultLogVO.setRiskEventId(info.getRiskEventId());
		warnResultLogVO.setTableLabel(info.getTableLabel());
		warnResultLogVO.setAttackFlag(info.getAttackFlag());
		//warnResultLogVO.setStatusEnum(Integer.valueOf(info.getInitStatus()));
		warnResultLogVO.setTag(info.getTag());
		warnResultLogVO.setFailedStatus(info.getFailedStatus());
		warnResultLogVO.setHarm(info.getHarm());
		warnResultLogVO.setThreatSource(info.getThreatSource());
		warnResultLogVO.setDealAdvice(info.getDealAdvice());
		warnResultLogVO.setAttackLine(info.getAttackLine());
		warnResultLogVO.setThreatCredibility(info.getThreatCredibility());
		if (StringUtils.isBlank(warnResultLogVO.getPrinciple())){
			warnResultLogVO.setPrinciple(info.getPrinciple());
		}
        warnResultLogVO.setDataSource(info.getDataSource());
	}
	
	
	/**
	 * 清除缓存
	 */
	public boolean clearCache(){
		Date now = new Date();
		long nowtime = now.getTime();
		if(nextTime>0L){
		}
		if (nowtime > nextTime) {
			for(Map.Entry<String,Object> entry : ruleCache.entrySet()){
				if(entry.getKey().contains(TIMETYPE)){
					clearCache(entry.getKey());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *手动清除规则缓存-key
	 */
	public void clearCache(String key){
		Object guid_obj = ruleCache.get(key);
		if(guid_obj!=null){
			String guid=guid_obj.toString();
			countCache.remove(guid);
			ruleCache.remove(key);
		}

	}

	/**
	 *手动清除规则缓存-WarnResultLogVO
	 */
	public boolean clearCacheByWarnResultLogTmpVO(WarnResultLogTmpVO warnResultLogTmpVO){
		String key=caculateKey(warnResultLogTmpVO);
		if(StringUtils.isNotEmpty(key)){
			clearCache(key);
		}
		return  true;
	}

	
	/**
	 * 更新缓存信息,进行对应的合并操作(ES版本)
	 * @param warnResultLogVO
	 * @param key
	 */
	@Synchronized
	public void refreshCache(WarnResultLogTmpVO warnResultLogVO, String key) {
		if(!StringUtils.isEmpty(key)){
			setWarnResultRepeatCount(warnResultLogVO, key);
		}else {
			warnResultLogVO.setRepeatCount(0);
		}
	}

	/**
	 * 设置告警合并的测试
	 * 1.key如果不存在规则缓存当中，设置count为1
	 * 2.在规则缓存当中，根据对应的count缓存获得对应需要合并的次数
	 * 3.告警设置重复个数，更新对应的guid
	 * @param warnResultLogVO
	 * @param key
	 */
	public void setWarnResultRepeatCount(WarnResultLogTmpVO warnResultLogVO, String key) {
		if (ruleCache.containsKey(key)) {
			Object guid_obj = ruleCache.get(key);
			if(guid_obj!=null) {
				String guid = guid_obj.toString();
				Object countObj = countCache.get(guid);
				if(countObj!=null){
					Integer count = (Integer)countObj;
					++count;
					countCache.put(guid, count);
					warnResultLogVO.setRepeatCount(count);
					warnResultLogVO.setId(guid);
				}
			}
		}else {
			ruleCache.put(key, warnResultLogVO.getId());
			countCache.put(warnResultLogVO.getId(), 1);
			warnResultLogVO.setRepeatCount(1);
		}
	}

	/**
	 * 计算对应的key值（ES版本）
	 * 1.获得需要进行合并的字段 a.没有合并字段,个数为0；b.合并字段集合为null；
	 * 2.从告警当中获得合并字段，根据合并字段获得对应的值，值不为空，加入到集合当中
	 * 3.将对应的值进行序列化
	 * @param warnResultLogVO
	 * @return
	 */
	public String caculateKey(WarnResultLogTmpVO warnResultLogVO) {
		List<String> mergeFields = getMergeFields();
		List<String> fieldsRelateValue = getMergeFieldsRelateValue(warnResultLogVO, mergeFields);
		String servalizeKeys = servalizeKeys(fieldsRelateValue);
		FieldInfoVO fieldInfo = this.getFieldInfoVO();

		if(fieldInfo!=null){
			if(StrategyEnum.TIMESTRATEGY.getName().equals(fieldInfo.getRulePolicy())){
				servalizeKeys=servalizeKeys+"-"+warnResultLogVO.getRuleId()+"-"+TIMETYPE;
			}else if(StrategyEnum.STATUSSTRATEGY.getName().equals(fieldInfo.getRulePolicy())) {
				servalizeKeys=servalizeKeys+"-"+warnResultLogVO.getRuleId()+"-"+STATUSTYPE;
			}
		}
		return servalizeKeys;
	}
	
	/**
	 * 获得对应的合并字段
	 * @return
	 */
    public List<String> getMergeFields(){
    	FieldInfoVO fieldInfo = this.getFieldInfoVO();
    	if(fieldInfo!=null){
    		List<String> list = fieldInfo.getField();
    		return list;
    	}else {
    		return null;
    	}
    }
	
    /**
     * 从告警当中获得合并字段，根据合并字段获得对应的值，值不为空，加入到集合当中
     * @param warnResultLogVO
     * @return
     */
    public List<String> getMergeFieldsRelateValue(WarnResultLogTmpVO warnResultLogVO,List<String> fieldsList){
    	if(fieldsList!=null&&fieldsList.size()!=0) {
    		List<String> strList = new ArrayList<>();
    		for (String field : fieldsList) {
    			try {
					Field declaredField = warnResultLogVO.getClass().getDeclaredField(field);
					ReflectionUtils.makeAccessible(declaredField);
					// declaredField.setAccessible(true);
					String fieldValue = (String) declaredField.get(warnResultLogVO);
					if (StringUtils.isNotEmpty(fieldValue)) {
						strList.add(fieldValue);
					}else{
						throw new RuntimeException("合并字段"+fieldValue+"为空，请检查！");
					}
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					logger.error("获得属性值出现错误", e);
				}
			}
    		return strList;
    	}else {
    		return null;
    	}
    }
    
    

    /**
     * 序列化对应的key
     * @param fieldsList
     * @return
     */
    public String servalizeKeys(List<String> fieldsList) {
         if(fieldsList!=null&&fieldsList.size()!=0) {
        	 String keyStr = StringUtils.join(fieldsList, "-");
        	 return keyStr;
         }else{
        	 return null;
         }
    }

	/**
	 * 获取告警guid
	 */
	public String  getAlarmGuid(WarnResultLogTmpVO warnResultLogVO){
		String guid="";
		String key=caculateKey(warnResultLogVO);
		Object guid_obj=ruleCache.get(key);
		if(guid_obj!=null){
			guid=guid_obj.toString();
		}
		return  guid;
	}


}

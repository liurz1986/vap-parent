package com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.BaseEventLog;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventLogSupplementField;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.Label;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventUrge;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventAlarmSettingService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.GuidNameVO;
import com.vrv.vap.alarmdeal.business.analysis.model.AuthorizationControl;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.alarmdeal.business.asset.service.AssetExtendService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.util.DateUtil;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EventEntityMapper {
	@Autowired
	private EventCategoryService  eventCategoryService;
	
	@Autowired
	private RiskEventRuleService  riskEventRuleService;
	
	@Autowired
	private ElasticSearchRestClient elasticSearchRestClient;
	
	@Autowired
	private EventTabelService eventTabelService;
	
	@Autowired
	private AssetService  assetService;
	@Autowired
	private AssetExtendService assetExtendService;
	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	
	@Autowired
	private AssetTypeService assetTypeService;
	
    @Autowired
    private MapperUtil mapperUtil;

	private static List<Class<? extends BaseEventLog>> logBeanLists = new ArrayList<>(); 

	static {
		//获取该路径下所有类
		Reflections reflections = new Reflections("com.vrv.vap.alarmdeal.vo.eventLogs");
		//获取继承了IAnimal的所有类
		Set<Class<? extends BaseEventLog>> classSet = reflections.getSubTypesOf(BaseEventLog.class);
		logBeanLists.addAll(classSet);
	}
 
    
    
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

 
	
	@Autowired
	private  EventAlarmSettingService  eventAlarmSettingService;
	
	private Logger logger = LoggerFactory.getLogger(EventEntityMapper.class);

	public AlarmEventAttribute getAlarmEventAttribute(WarnResultLogTmpVO warn) {
		logger.debug("收到的数据"+gson.toJson(warn));
		//warn.getRuleCode()
		AlarmEventAttribute doc = new AlarmEventAttribute();

		doc.setIsRead(false);
		doc.setIsUrge(false);
		doc.setIsSupervise(false);

		doc.setEventCreattime(warn.getTriggerTime());

		doc.setEventId(warn.getId());
		//doc.setEventName(warn.getRuleName());//这里没有问题，原始赋值反了
		//doc.setEventCode(warn.getRiskEventCode());

		//doc.setRuleId(warn.getRuleId());
		//doc.setRuleName(warn.getRiskEventName());//这里没有问题，原始赋值反了

		//doc.setAlarmRiskLevel(warn.getWeight());
		doc.setEventDetails(warn.getAlamDesc());
		doc.setDataSource(warn.getDataSource());

		doc.setSrcIps(warn.getSrc_ips());
		doc.setDstIps(warn.getDstIps());
		
		doc.setPrincipalIp(warn.getRelatedIps());

		supplementEntity(warn, doc);
		return doc;
	}

	
	private  void  sendMsgToRole(List<GuidNameVO> roles)
	{
		
	}
	private  void  sendMsgToUser(List<GuidNameVO> roles)
	{
		
	}

	/**
	 * 补全实体
	 * @param warn
	 * @param doc
	 */
	public void supplementEntity(WarnResultLogTmpVO warn, AlarmEventAttribute doc) {
 
		//设置版本号
		doc.setEventVersion(getEventVersion(warn));
		
		//补全logs
		fromEventLogs(warn, doc);
		
		//跟据规则补全
		fromEventCategory(warn.getRuleCode(), doc);
		
		//设置权限
		setAuthorization(doc);

 

		switch (warn.getStatusEnum()) {
		case 0:
			doc.setAlarmDealState(AlarmDealStateEnum.UNTREATED.getCode());
			break;
		case 1:
		case 3:
		case 4:
			// case 5:
			doc.setAlarmDealState(AlarmDealStateEnum.PROCESSING.getCode());
			break;
		case 2:
		case 7:
		case 8:
		case 5:
		case 6:
			doc.setAlarmDealState(AlarmDealStateEnum.PROCESSED.getCode());
			break;
		default:
			doc.setAlarmDealState(AlarmDealStateEnum.UNTREATED.getCode());
			break;
		}

		if (doc.getDeviceInfos() == null) {
			doc.setDeviceInfos(new ArrayList<>());
		}
		if (doc.getFileInfos() == null) {
			doc.setFileInfos(new ArrayList<>());
		}
		if (doc.getApplicationInfos() == null) {
			doc.setApplicationInfos(new ArrayList<>());
		}
		if (doc.getLabels() == null) {
			doc.setLabels(new ArrayList<>());
		}
		if (doc.getStaffInfos() == null) {
			doc.setStaffInfos(new ArrayList<>());
		}
		if (doc.getUrgeInfos() == null) {
			doc.setUrgeInfos(new ArrayList<>());
		}
		doc.setStaffNum(doc.getStaffInfos() == null ? 0 : doc.getStaffInfos().size());
		doc.setDeviceCount(doc.getDeviceInfos() == null ? 0 : doc.getDeviceInfos().size());
		doc.setFileCount(doc.getFileInfos() == null ? 0 : doc.getFileInfos().size());
		doc.setDeviceAppCount(doc.getApplicationInfos() == null ? 0 : doc.getApplicationInfos().size());
		doc.setIsUrge((doc.getUrgeInfos()==null||doc.getUrgeInfos().isEmpty())?false:true);
//		"1配置合规性事件
//		2网络安全异常事件
//		3用户行为异常事件
//		4运维行为异常事件
//		5应用服务异常事件
//		6跨单位互联异常事件"

		
	}


	private void setAuthorization(AlarmEventAttribute doc) {
		EventAlarmSetting one = eventAlarmSettingService.getOne(doc.getRuleId());
		if(one==null)
		{
			EventCategory eventCategory=eventCategoryService.getOne(doc.getCategoryId());
			while(eventCategory!=null&&one==null)
			{
				 one = eventAlarmSettingService.getOne(doc.getCategoryId());
				 if(one==null)
				 {
					 if(StringUtils.isNotEmpty(eventCategory.getParentId()))
					 {
						 eventCategory=eventCategoryService.getOne(eventCategory.getParentId());
					 }else
					 {
						 eventCategory=null;
					 }
				 }
			}
		}
		if(one!=null)
		{
			List<GuidNameVO> toUser =null;
			if(!StringUtils.isEmpty(one.getToUser()))
			{
				 toUser =  gson.fromJson(one.getToUser(),  new TypeToken<List<GuidNameVO>>() {}.getType());
			}
			List<GuidNameVO> toRole =null;
			if(!StringUtils.isEmpty(one.getToRole()))
			{
				toRole = gson.fromJson(one.getToRole(), new TypeToken<List<GuidNameVO>>() {
				}.getType());
			}
			List<AlarmEventUrge> urgeInfos = doc.getUrgeInfos();
			if(urgeInfos==null&&Boolean.TRUE.equals(one.getIsUrge()))
			{
				urgeInfos=new ArrayList<>();

				AlarmEventUrge urgeInfo=new AlarmEventUrge();
				urgeInfo.setIsAuto(true);
				//urgeInfo.setInitiator(initiator);
				urgeInfo.setUrgeRemark(one.getUrgeReason());
				urgeInfo.setUrgeTime(new Date());
				if(toUser!=null)
				{
					 
					urgeInfo.setToUser(toUser);
					//是否需要给这些人发送消息
					sendMsgToUser(toUser);
				}

				if(toRole!=null)
				{
		 
					urgeInfo.setToRole(toRole);
					//是否需要给这些人发送消息
					sendMsgToUser(toRole);
				}
				
				urgeInfo.setValidityDate(DateUtil.addSeconds(new Date(), one.getTimeLimitNum()));
				urgeInfos.add(urgeInfo);
				doc.setValidityDate(urgeInfo.getValidityDate());
				doc.setUrgeInfos(urgeInfos);
			}

			
			
			 AuthorizationControl authorization = doc.getAuthorization();
			 if(authorization==null)
			 {
				 authorization=new AuthorizationControl();
				 
				 List<GuidNameVO> canOperateUser = new ArrayList<>();
				 if(toUser!=null)
				 {
					 toUser.forEach(item->{
						 canOperateUser.add(item);
					 });
				 }
				 
				 
				 List<GuidNameVO> canOperateRole = new ArrayList<>();
				 if(toRole!=null)
				 {
					 toRole.forEach(item->{
						 canOperateRole.add(item);
					 });
				 }
				 
				 
				 authorization.setCanOperateRole(canOperateRole);
				 authorization.setCanOperateUser(canOperateUser);
				 authorization.setOperatorRecord(new ArrayList<>());

				 doc.setAuthorization(authorization);
			 }
		}
	}


	private String getEventVersion(WarnResultLogTmpVO warn) {
		if (StringUtils.isNotEmpty(warn.getMultiVersions())) {
			String pattern = "(?<=\"version\":).*?(?=,\")";

			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(warn.getMultiVersions());
			// System.out.println(m.matches());
			if (m.find()) {
				return  m.group();
			}
		}
		return null;
	}


	private void fromEventLogs(WarnResultLogTmpVO warn, AlarmEventAttribute doc) {
		List<LogIdVO> logs = doc.getLogs(); 
		
		if(logs==null||logs.isEmpty())
		{
			logs=new ArrayList<>();
			
			Map<String, String[]> idRoom = warn.getIdRoom();
	
			if(idRoom!=null) {
				for(Map.Entry<String,String[]> item:  idRoom.entrySet())
				{
					String key=item.getKey();
					String[] logIds=item.getValue();
					
					
					
					EventTable  eventTable = eventTabelService.getEventTableByName(key);
					if(eventTable!=null)
					{
						
						String baseIndexName=eventTable.getIndexName();
						
						Map<String, List<String>> logsMap = new HashMap<>();
						Map<String, List<String>> logGuidsMap = new HashMap<>();
						
						//logsMap.put(baseIndexName, Arrays.asList(logIds));
						
						
						List<QueryCondition_ES> conditions = new ArrayList<>();
						conditions.add(QueryCondition_ES.in("guid", logIds));//logIds 集合太大时，可能存在问题，需要优化
						QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
						SearchResponse response = elasticSearchRestClient.getDocs(new String[] { baseIndexName+"-*" }, queryBuilder, null, null, 0, logIds.length);
						if(response.getHits()!=null)
						{
							long total = response.getHits().getTotalHits().value;
							if (total > 0) {
								SearchHits result = response.getHits();
								SearchHit[] hits = result.getHits();

								for (SearchHit hit : hits) {
									String indexName = hit.getIndex();
									String logId = hit.getId();

									if (logsMap.containsKey(indexName)) {
										List<String> list = logsMap.get(indexName);
										list.add(logId);
									} else {
										List<String> list = new ArrayList<>();
										list.add(logId);
										logsMap.put(indexName, list);
									}

									Map<String, Object>  sourceAsMap= hit.getSourceAsMap();
									if(sourceAsMap!=null&&sourceAsMap.containsKey("guid"))
									{
										String guid=sourceAsMap.getOrDefault("guid", "").toString();
										if (StringUtils.isNotEmpty(guid)) {
											if (logGuidsMap.containsKey(indexName)) {
												List<String> list = logGuidsMap.get(indexName);
												list.add(guid);
											} else {
												List<String> list = new ArrayList<>();
												list.add(guid);
												logGuidsMap.put(indexName, list);
											}
										}
									}
									
									String sourceAsString = hit.getSourceAsString();
									logger.debug("sourceAsString:"+sourceAsString);
									
							
									Class<? extends BaseEventLog> clazz = getLogBean(indexName);
									
									
									BaseEventLog fromJson = gson.fromJson(sourceAsString, clazz);
									if(fromJson!=null)
									{
										try {

											fromJson.InitEvent(doc);
										}catch (Exception e) {
											logger.error("日志数据信息提出出现异常：{}",e);
										}
									}
								}
								
								
					
							}
						}

						for(Map.Entry<String, List<String>> itemLog :  logsMap.entrySet() )
						{
							LogIdVO vo=new LogIdVO();
							vo.setIndexName(itemLog.getKey());
							vo.setEventTableGuid(eventTable.getId());
							vo.setEventTableName(eventTable.getName());
							vo.setIds(itemLog.getValue());
							vo.setLogGuids(logGuidsMap.get(itemLog.getKey()));
							logs.add(vo);
						}

					}
				}
			}
			doc.setLogs(logs);
		}
		
	 
		
		doc.setDeviceCount(doc.getDeviceInfos()==null?0:doc.getDeviceInfos().size());
		doc.setStaffNum(doc.getStaffInfos()==null?0:doc.getStaffInfos().size());
		doc.setDeviceAppCount(doc.getApplicationInfos()==null?0:doc.getApplicationInfos().size());
		doc.setFileCount(doc.getFileInfos()==null?0:doc.getFileInfos().size());
		doc.setLogs(logs);
	}


	private Class<? extends BaseEventLog> getLogBean(String indexName) {
		Class<? extends BaseEventLog> clazz=null;
		for(Class<? extends BaseEventLog> logBean : logBeanLists)
		{
			if(!logBean.getName().equals(EventLogSupplementField.class.getName()))
			{
				try {
					if (logBean.newInstance().matchIndexPrefix(indexName)) {
						clazz = logBean;
					}
				}catch (Exception e) {
					logger.error("反射获取索引是否匹配执行报错：{}",e);
				}
			}
		}
		
		if(clazz==null)
		{
			logger.info("索引匹配不到对应解析器:"+indexName);
			clazz=EventLogSupplementField.class;
		}
		return clazz;
	}

 

 


	private void fromEventCategory(String ruleCode, AlarmEventAttribute doc) {

		
		List<QueryCondition> querys=new ArrayList<>();
		querys.add(QueryCondition.eq("ruleCode", ruleCode));
		List<RiskEventRule> findAll = riskEventRuleService.findAll(querys);
		if(findAll!=null&&!findAll.isEmpty())
		{
			RiskEventRule riskEventRule =findAll.get(0);
			
			doc.setRuleId(riskEventRule.getId());
			doc.setRuleName(riskEventRule.getName());
			doc.setEventName(riskEventRule.getEventName());
		
			doc.setEventCode(riskEventRule.getWarmType());
			//doc.setDataSource(riskEventRule.getData_source());
			
			String riskEventId = riskEventRule.getRiskEventId();
			EventCategory eventCategory = eventCategoryService.getOne(riskEventId);
			
			doc.setCategoryId(eventCategory.getId());
			
//			1标准事件（6种事件类型）
//			2自定义事件
			
			
			//事件类型数据补全
			String riskEventCode = eventCategory.getCodeLevel();
			//用户行为异常 3
			if(riskEventCode.startsWith("/safer/AbnormalUserBehavior"))
			{
				doc.setEventType(3);
			}
			// 应用异常 5
			else if(riskEventCode.startsWith("/safer/AbnormalApplicationBehavior"))
			{
				doc.setEventType(5);
			}
			//配置合规信息 1
			else if(riskEventCode.startsWith("/safer/ConfigurationCompliance"))
			{
				doc.setEventType(1);
			}
			//互联互通异常  6
			else if(riskEventCode.startsWith("/safer/ConnectivityAbnormal"))
			{
				doc.setEventType(6);
			}
			//网络安全异常 2
			else if(riskEventCode.startsWith("/safer/NetworkSecurityException"))
			{
				doc.setEventType(2);
			}
			//运维行为异常 4
			else if(riskEventCode.startsWith("/safer/OperationaBehavior"))
			{
				doc.setEventType(4);
			}
			
			
			doc.setEventKind(doc.getEventType()!=null&&doc.getEventType()<=6?1:2);
			
			doc.setCategoryId(eventCategory.getId());
			//doc.setDataSource(eventCategory.getThreatSource());
			
			//String ruleId = warn.getRuleId();
			//RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
			//doc.setEventDetails(riskEventRule.getDesc());
			if(riskEventRule!=null)
			{
				doc.setAlarmRiskLevel(Integer.parseInt( riskEventRule.getLevelstatus()));
				
				String  tags=riskEventRule.getKnowledgeTag();
				if(!StringUtils.isEmpty(tags)&&(doc.getLabels()==null||doc.getLabels().isEmpty()))
				{
					List<Label> labels = new ArrayList<>();
					for(String tag : tags.split(","))
					{
						Label label=new Label(tag,"#000",tag);
						
						labels.add(label);
					}
					
					doc.setLabels(labels);
				}
			}
			
		}
		
		
		
	}

 
	
 
	
	 

	private AssetTypeSno getAssetTypeSno(Asset asset) {
		AssetTypeSno assetTypeSno = null;

		if (CommomLocalCache.containsKey(asset.getAssetTypeSnoGuid())) {
			assetTypeSno = (AssetTypeSno) CommomLocalCache.get(asset.getAssetTypeSnoGuid());
		} else {
			assetTypeSno = assetTypeSnoService.getOne(asset.getAssetTypeSnoGuid());
			CommomLocalCache.put(asset.getAssetTypeSnoGuid(), assetTypeSno, 2, TimeUnit.HOURS);
		}
		return assetTypeSno;
	}

 
}

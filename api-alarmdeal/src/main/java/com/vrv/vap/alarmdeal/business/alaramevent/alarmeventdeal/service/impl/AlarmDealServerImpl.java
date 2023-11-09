package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.impl;

import com.google.gson.Gson;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.analysis.enums.TypeClass;
import com.vrv.vap.alarmdeal.business.analysis.vo.UpdateAlarmDealVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.ResultData;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.dao.AlarmDealDao;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealTypeEnum;
import com.vrv.vap.alarmdeal.frameworks.feign.FeignCache;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmDeal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmItemDeal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.repository.AlarmDealRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmDealServer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmItemDealService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.DealCommand;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl.AlarmDealCommandInvoker;
import com.vrv.vap.alarmdeal.business.analysis.server.tabInfo.AlarmStatusChangeSubject;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmCommandVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmDealVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AnalysisStatusVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealInfoVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealTaskstaticVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealWayVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.ArrayUtil;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlarmDealServerImpl extends BaseServiceImpl<AlarmDeal, String> implements AlarmDealServer {

	private static final String ROOT_ = "root"; //root角色
	private static final String ADMIN_ = "admin"; //admin角色
	private static final Integer ADMIN_ID = 31;
	private Logger logger = LoggerFactory.getLogger(AlarmDealServerImpl.class);
	@Autowired
	private AlarmDealRespository alarmDealRespository;
    @Autowired
    private AlarmDealDao alarmDealDao;
	@Autowired
	private AlarmItemDealService alarmItemDealService;
	@Autowired
	private AlarmDealCommandInvoker alarmDealCommandInvoker;
	@Autowired
	private DealCommand mailCommand;
	@Autowired
	private DealCommand smsCommand;
	@Autowired
	private WarnResultForESService warnResultForEsService;
	@Autowired
	private AlarmStatusChangeSubject alarmStatusChangeSubject;
//    @Autowired
//    private FeignCache feignCache;


	
	@Override
	public AlarmDealRespository getRepository() {
		return alarmDealRespository;
	}

	@Override
	public PageRes<AlarmDeal> getAlarmDealPager(AlarmDealVO alarmDealVO, Pageable pageable) {
		//查询条件
		String dealStatus = alarmDealVO.getDealStatus();
		String createPeople = alarmDealVO.getCreatePeople();
		String alarmGuid = alarmDealVO.getAlarmGuid();
		String startTime = alarmDealVO.getStartTime();
		String endTime = alarmDealVO.getEndTime();
		String[] roleIds = alarmDealVO.getRoleIds();
		String userId = alarmDealVO.getUserId();
		String dealPeople=alarmDealVO.getDealPeople();
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();
		List<String> roleCodes = this.getRoleCode(roleIds);
		if(!roleCodes.contains(ADMIN_)&&!roleCodes.contains(ROOT_)){
			conditions.add(QueryCondition.like("createPeopleId", createPeople));
		}
		if (StringUtils.isNotEmpty(dealStatus)) {
			conditions.add(QueryCondition.eq("dealStatus", dealStatus));
		}
		if (StringUtils.isNotEmpty(alarmGuid)) {
			conditions.add(QueryCondition.eq("alarmGuid", alarmGuid));
		}
		if(StringUtils.isNotEmpty(dealPeople)){
			List<Map<String,Object>> list=alarmDealDao.getDealGuidByDealPeople(dealPeople);
			List<String> guids=list.stream().map(item->item.get("dealGuid").toString()).collect(Collectors.toList());
			conditions.add(QueryCondition.in("guid", guids));
		}
		if(StringUtils.isNotEmpty(startTime)){
			startTime = startTime+" 00:00:00";
			conditions.add(QueryCondition.gt("createTime", startTime));
		}
		if(StringUtils.isNotEmpty(endTime)){
			endTime = endTime+" 23:59:59";
			conditions.add(QueryCondition.lt("createTime", endTime));
		}
		Page<AlarmDeal> page = findAll(conditions, pageable);
		PageRes<AlarmDeal> res = PageRes.toRes(page);
		return res;
	}
	
	/**
	 * 根据roleId获得对应的role_code
	 * @param roleIds
	 */
	private List<String> getRoleCode(String[] roleIds) {
		List<String> roleCodeList = new ArrayList<>();
		for (String roleId : roleIds) {
			Map<String,Object> map = new HashMap<>();
			map.put("id", roleId);
			 List<Role> rolelist = FeignCache.getRoleById(map);
			if (rolelist != null) {
				for (Role role : rolelist) {
					roleCodeList.add(role.getCode());
				}
			}
		}
		return roleCodeList;
	}

	@Override
	public String judgeAlarmStatus(String alarmDealId) {
		List<String> statusList = new ArrayList<String>();
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();
		conditions.add(QueryCondition.eq("dealGuid", alarmDealId));
		List<AlarmItemDeal> list = alarmItemDealService.findAll(conditions);
		for (AlarmItemDeal alarmitemdeal : list){
			String itemStatus = alarmitemdeal.getItemStatus();
			statusList.add(itemStatus);
		}
		String[] array = (String[]) statusList.toArray(new String[statusList.size()]);
		String statusStr = ArrayUtil.join(array, ",");
		if(statusStr.contains("false")){
			return "false";
		}else if(statusStr.contains("waiting")){
			return "waiting";
		}else{
			return "true";
		}
	}

	@Override
	public void updateAlarmDeal(UpdateAlarmDealVO updateAlarmDealVO) {
		 ResultData<Boolean> resultData = updateAlarmDealVO.getResultData();
		 Boolean data = resultData.getData();
		 AlarmItemDeal alarmItemDeal = alarmItemDealService.getOne(updateAlarmDealVO.getAlarmItemId());
		 alarmItemDeal.setItemStatus(String.valueOf(data));
		 alarmItemDeal.setItemPeople(updateAlarmDealVO.getUserName());
		 alarmItemDeal.setItemPeopleId(updateAlarmDealVO.getUserId());
		 alarmItemDeal.setItemType(updateAlarmDealVO.getItemType());
		 alarmItemDeal.setHappenTime(DateUtil.format(new Date()));
		 alarmItemDeal.setLastExeTime(DateUtil.format(new Date()));
		 alarmItemDealService.save(alarmItemDeal);
		 String dealGuid = alarmItemDeal.getDealGuid();
		 //TODO 改变告警的状态,并返回初始化的值
		 String userName = updateAlarmDealVO.getUserName();
		 Integer initStatus = changeAlarmStatusByMailAndSms(userName,dealGuid,data);
		 logger.info("初始状态日志："+initStatus);
		 String status = judgeAlarmStatus(dealGuid);
		 AlarmDeal alarmDeal = getOne(dealGuid);
		 alarmDeal.setDealStatus(status);
		 String itemType=alarmDeal.getDealType(); //处置类型
		 if(StringUtils.isNotEmpty(itemType)){
			 alarmDeal.setDealType(itemType+","+updateAlarmDealVO.getItemType());
		 }else{
			 alarmDeal.setDealType(updateAlarmDealVO.getItemType());
		 }
		 alarmDeal.setEndTime(DateUtil.format(new Date()));
		 alarmDeal.setDeadLine(DateUtil.format(new Date()));
		 alarmDeal.setDealPerson(updateAlarmDealVO.getUserName());
		 alarmDeal.setDealDetail(initStatus+"=>"+AlarmDealTypeEnum.DEAL_ING.getIndex()+"=>"+AlarmDealTypeEnum.ALREADY_DEAL.getIndex());
		 save(alarmDeal);
	 		
	}

	@Override
	public Result<Boolean> issueAlarm(AlarmCommandVO alarmCommandVO) {
		Result<Boolean> result = new Result<>();
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();
		conditions.add(QueryCondition.eq("dealGuid", alarmCommandVO.getAlarmDealId()));
		List<AlarmItemDeal> list = alarmItemDealService.findAll(conditions);
		for (AlarmItemDeal alarmItemDeal : list) {
			String itemType = alarmItemDeal.getItemType();
			String guid = alarmItemDeal.getGuid();
			alarmCommandVO.setAlarmItemId(guid);
			switch (itemType) {
			case TypeClass.mail:
				alarmDealCommandInvoker.setDealCommand(mailCommand);
				break;
			case TypeClass.sms:
				alarmDealCommandInvoker.setDealCommand(smsCommand);
				break;
			default:
				break;
			}
			alarmDealCommandInvoker.executeCommand(alarmCommandVO);
		}
		
		
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(true);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}
	
	/**
	 * 改变邮件和短信状态
	 * @param alarmdealId
	 */
	private Integer changeAlarmStatusByMailAndSms(String userName,String alarmdealId,Boolean result) {
		Integer initStatusEnum = 0;
		if(result) {
			AlarmDeal alarmDeal = getOne(alarmdealId);
			String alarmGuid = alarmDeal.getAlarmGuid(); //告警数据
			String[] alarmGuidArr = alarmGuid.split(",");
			for (String guid : alarmGuidArr) {
				AnalysisStatusVO analysisStatusVO = new AnalysisStatusVO();
				analysisStatusVO.setId(guid);
				analysisStatusVO.setStatus(String.valueOf(AlarmDealTypeEnum.ALREADY_DEAL.getIndex()));
				analysisStatusVO.setDealPerson(userName);
				warnResultForEsService.changeAnalysisResultStatus(analysisStatusVO);
				//return initStatusEnum;
			}
		}
		return initStatusEnum;
	} 

	
	@Override
	public WarnResultLogTmpVO alarmDealByAuto(WarnResultLogTmpVO warnResult){
		warnResult.setStatusEnum(AlarmDealTypeEnum.DEAL_ING.getIndex());
		AlarmDeal alarmdeal = new AlarmDeal();
		String guid = UUIDUtils.getUUID();
		alarmdeal.setGuid(guid);
		alarmdeal.setAlarmGuid(warnResult.getId());
		alarmdeal.setCreatePeople("admin");
		alarmdeal.setCreateTime(DateUtil.format(new Date()));
		alarmdeal.setDealStatus("dealing");
		alarmdeal.setRiskEventName(warnResult.getRiskEventName());
		alarmdeal.setDealDetail(AlarmDealTypeEnum.WAITE_SURE.getIndex()+"=>"+AlarmDealTypeEnum.DEAL_ING.getIndex());
		alarmdeal.setDealType(TypeClass.eventDeal);
		save(alarmdeal);
		addDealItem("31","admin", warnResult.getRiskEventName()+"自动转工单", guid, TypeClass.eventDeal);
		return warnResult;
		
	}
	
	@Override
	public Result<String> alarmDeal(DealInfoVO dealInfo) {
		Result<String> result = new Result<>();
		String riskEventName = null;
		String userId = dealInfo.getUserId();
		String userName = dealInfo.getUserName();
		String id = dealInfo.getId();
		String[] splitArr = id.split(","); //所有的告警主键Id
		if(splitArr.length>0){
			String alarmGuid = splitArr[0]; //主告警Id
			WarnResultLogTmpVO warnResult= warnResultForEsService.getAlarmById(alarmGuid);
			//TODO 预警的加入
			if(warnResult!=null){
				AlarmDeal alarmdeal = new AlarmDeal();
				String changeAlarmStatus = null;
				Integer statusEnum = warnResult.getStatusEnum();
				riskEventName = warnResult.getRiskEventName();
				changeAlarmStatus = changeAlarmStatus(id,dealInfo);
				String guid = UUIDUtils.getUUID();
				alarmdeal.setGuid(guid);
				alarmdeal.setAlarmGuid(dealInfo.getId());
				alarmdeal.setCreatePeople(userName);
				alarmdeal.setCreatePeopleId(userId);
				if(StringUtils.isNotEmpty(dealInfo.getDeal_line())){
					alarmdeal.setDeadLine(dealInfo.getDeal_line());
				}else {
					alarmdeal.setDeadLine(DateUtil.format(new Date()));
				}
				if(StringUtils.isNotEmpty(dealInfo.getDealPerson())){
					alarmdeal.setDealPerson(dealInfo.getDealPerson());
				}else {
					alarmdeal.setDealPerson(dealInfo.getUserName());
				}
				alarmdeal.setCreateTime(DateUtil.format(new Date()));
				String status = checkImmediateStatus(dealInfo.getType());
				alarmdeal.setDealStatus(status);
				alarmdeal.setRiskEventName(riskEventName);
				alarmdeal.setDealDetail(statusEnum+"=>"+changeAlarmStatus);
				alarmdeal.setDealType(dealInfo.getType());
				String type = dealInfo.getType();
				if(type.equals(TypeClass.errorReport)){ //误报类型补充结束时间
					alarmdeal.setEndTime(DateUtil.format(new Date()));
				}
				setRoleInfoToAlarmdeal(alarmdeal);
				save(alarmdeal);
				addDealItem(userId,userName, dealInfo.getContent(), guid, dealInfo.getType());
				result.setCode(ResultCodeEnum.SUCCESS.getCode());
				result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
				result.setData(guid);
			}
		}
		
		
		return result;
	
	}
	
	/**
	 * 改变告警状态
	 * @param dealInfo
	 */
	private String changeAlarmStatus(String id,DealInfoVO dealInfo){
		String status = null;
		String[] splitArr = id.split(","); //所有的告警主键Id
		if(splitArr.length>0){
			for (String splitId : splitArr) {
				status = changeWarnResultStatus(splitId, dealInfo);
			}
		}
		return status;
	}

	private String changeWarnResultStatus(String id, DealInfoVO dealInfo) {
		WarnResultLogTmpVO warnResult = warnResultForEsService.getAlarmById(id);
		String status = String.valueOf(warnResult.getStatusEnum());
		switch (dealInfo.getType()) {
		case TypeClass.eventDeal: //转处置
			statusChangeListener(warnResult, String.valueOf(AlarmDealTypeEnum.DEAL_ING.getIndex()));
			warnResult.setStatusEnum(AlarmDealTypeEnum.DEAL_ING.getIndex());
			status = String.valueOf(AlarmDealTypeEnum.DEAL_ING.getIndex());
			break;
		case TypeClass.professor: //转专家
			statusChangeListener(warnResult, String.valueOf(AlarmDealTypeEnum.ANALYSIS_ING.getIndex()));
			warnResult.setStatusEnum(AlarmDealTypeEnum.ANALYSIS_ING.getIndex());
			status = String.valueOf(AlarmDealTypeEnum.ANALYSIS_ING.getIndex());
			break;
		case TypeClass.errorReport: //转误报
			statusChangeListener(warnResult, String.valueOf(AlarmDealTypeEnum.ERROR_REPORT.getIndex()));
			warnResult.setStatusEnum(AlarmDealTypeEnum.ERROR_REPORT.getIndex());
			status = String.valueOf(AlarmDealTypeEnum.ERROR_REPORT.getIndex());
			break;
		case TypeClass.eventDealEnd: //转事件处置结束
			statusChangeListener(warnResult, String.valueOf(AlarmDealTypeEnum.ERROR_REPORT.getIndex()));
			warnResult.setStatusEnum(AlarmDealTypeEnum.ALREADY_DEAL.getIndex());
			status = String.valueOf(AlarmDealTypeEnum.ALREADY_DEAL.getIndex());
			break;
		default:
			break;
		}
		AnalysisStatusVO analysisStatusVO = new AnalysisStatusVO();
		analysisStatusVO.setId(id);
		analysisStatusVO.setStatus(status);
		if(StringUtils.isNotEmpty(dealInfo.getDealPerson())){
			analysisStatusVO.setDealPerson(dealInfo.getDealPerson());
		}else{
			analysisStatusVO.setDealPerson(dealInfo.getUserName());
		}
		warnResultForEsService.changeAnalysisResultStatus(analysisStatusVO);


		return status;
	}
	
	private String checkImmediateStatus(String type) {
		String status = "0"; //待处置
		switch (type) {
		case TypeClass.message:
		case TypeClass.eventDealEnd:
		case TypeClass.errorReport:
			status = String.valueOf(true); //成功
			break;
		case TypeClass.professor:
			status ="dealing"; //处置中
			break;
		default:
			break;
		}
		
		return status;
	}

	private boolean addDealItem(String userId,String userName, String content, String guid, String type) {
		boolean candeal = false;
		String status = checkImmediateStatus(type);
		switch (type) {
		case TypeClass.eventDeal:
		case TypeClass.eventDealEnd:
		case TypeClass.message:
			candeal = true;
			break;
		case TypeClass.errorReport:
			candeal = true;
			break;
		case TypeClass.professor:
			candeal = true;
			break;
		case TypeClass.mail:
			candeal = true;
			break;
		default:
			break;
		}
		if(candeal) {
			String itemGuid = UUIDUtils.get32UUID();
			AlarmItemDeal alarmitemdeal = new AlarmItemDeal();
			alarmitemdeal.setDealGuid(guid);
			alarmitemdeal.setGuid(itemGuid);
			alarmitemdeal.setItemPeopleId(userId);
			alarmitemdeal.setItemPeople(userName);
			alarmitemdeal.setItemStatus(status);
			if(type.equals(TypeClass.message)||type.equals(TypeClass.mail)||type.equals(TypeClass.errorReport)){
				alarmitemdeal.setLastExeTime(DateUtil.format(new Date()));
			}
			alarmitemdeal.setHappenTime(DateUtil.format(new Date()));
			alarmitemdeal.setItemType(type);
			alarmitemdeal.setJsonInfo(content);
			alarmItemDealService.save(alarmitemdeal);
		} else {
			logger.warn("不支持的告警处置方案：" + type);
		}
		
		return candeal;
	}

	/**
	 * 保存对应的AlarmItem信息
	 * @param userId
	 * @param content
	 * @param guid
	 * @param type
	 */

	private void saveAlarmItemDeal(String userId,String username,String content, String guid,String type) {
		String itemGuid = UUIDUtils.get32UUID();
		AlarmItemDeal alarmitemdeal = new AlarmItemDeal();
		alarmitemdeal.setDealGuid(guid);
		alarmitemdeal.setGuid(itemGuid);
		alarmitemdeal.setHappenTime(DateUtil.format(new Date()));
		alarmitemdeal.setItemPeople(username);
		alarmitemdeal.setItemPeopleId(userId);
		alarmitemdeal.setItemStatus("0");
		alarmitemdeal.setItemType(type);
		alarmitemdeal.setJsonInfo(content);
		alarmItemDealService.save(alarmitemdeal);
	}
	
	@Override
	public List<Map<String, Object>> getDealedAlarm(String riskEventName, String nowDay, String beforeMouthDay) {
		List<Map<String,Object>> list = alarmDealDao.getDealedAlarm(riskEventName, nowDay, beforeMouthDay);
		return list;
	}

	@Override
	public List<Map<String, Object>> getDealitemPeople(String beforesixMouthDays, String nowDays) {
		List<Map<String,Object>> list = alarmDealDao.getDealitemPeople(beforesixMouthDays, nowDays);
		return list;
	}

	@Override
	public List<Map<String, Object>> getDealAlarmCountByStatus(String beforeSixMouth, String nowdayMouth,
			String peopleId, String status) {
		List<Map<String,Object>> list = alarmDealDao.getDealAlarmCountByStatus(beforeSixMouth, nowdayMouth, peopleId, status);
		return list;
	}

	@Override
	public Result<Boolean> issueRepeatAlarm(List<AlarmCommandVO> list) {
		Result<Boolean> result = new Result<>();
		for (AlarmCommandVO alarmCommandVO : list) {
			 String alarmItemId = alarmCommandVO.getAlarmItemId();
			 AlarmItemDeal alarmItemDeal = alarmItemDealService.getOne(alarmItemId);
			 String itemType = alarmItemDeal.getItemType();
			switch (itemType) {
			case TypeClass.mail:
				alarmDealCommandInvoker.setDealCommand(mailCommand);
				break;
			case TypeClass.sms:
				alarmDealCommandInvoker.setDealCommand(smsCommand);
				break;
			default:
				break;
			}
			alarmDealCommandInvoker.executeCommand(alarmCommandVO);
		}
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(true);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}

	@Override
	public void statusChangeListener(WarnResultLogTmpVO warnResult, String afterStatus) {
		Integer beforestatusEnum = warnResult.getStatusEnum();
		alarmStatusChangeSubject.notice(String.valueOf(beforestatusEnum), afterStatus, warnResult);
	}

	
	/**
	 * 将对应的数据进行修改(选择告警处置的数据从待确认变成处置中)
	 * @param ids
	 */
	private void changeAnalysisStatusInDealing(String ids,String userName){
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			AnalysisStatusVO analysisStatusVO = new AnalysisStatusVO();
			analysisStatusVO.setId(id);
			analysisStatusVO.setDealPerson(userName);
			analysisStatusVO.setStatus(String.valueOf(AlarmDealTypeEnum.DEAL_ING.getIndex()));
			warnResultForEsService.changeAnalysisResultStatus(analysisStatusVO);
		}
	}
	
	@Override
	@Transactional
	public Result<String> saveAlarmDeal(DealWayVO dealWayVO) {
		Result<String> result = new Result<>();
		List<MailVO> mail = dealWayVO.getMail();
		List<SmsVO> sms = dealWayVO.getSms();
		String riskEventName = null;
		String ticket = dealWayVO.getTicket();
		String userName = dealWayVO.getUserName();
		String userId = dealWayVO.getUserId();
		String ids = dealWayVO.getId();
		String dealType="";
		AlarmDeal alarmdeal = new AlarmDeal();
		//TODO 预警的加入
		String[] idArr = ids.split(",");
		if(idArr.length>0){
			String id = idArr[0];
			WarnResultLogTmpVO alarmById = warnResultForEsService.getAlarmById(id);
			riskEventName = alarmById.getRiskEventName();
		}
		String guid = UUIDUtils.getUUID();
		alarmdeal.setGuid(guid);
		alarmdeal.setAlarmGuid(dealWayVO.getId());
		alarmdeal.setCreatePeople(userName);
		alarmdeal.setCreatePeopleId(userId);
		alarmdeal.setCreateTime(DateUtil.format(new Date()));
		alarmdeal.setDealStatus("dealing");
		if(riskEventName != null){
			alarmdeal.setRiskEventName(riskEventName);
		}
		alarmdeal.setDealDetail(0+"=>"+AlarmDealTypeEnum.DEAL_ING.getIndex());
		
		setRoleInfoToAlarmdeal(alarmdeal);
		
		if (mail!=null&&mail.size()!=0) {
			dealType=dealType+","+TypeClass.mail;
			mail = getMailVoWithNewContent(mail, dealWayVO.getId());
			String jsonMail = JsonMapper.toJsonString(mail);
			saveAlarmItemDeal(userId,userName, jsonMail, guid,TypeClass.mail);
		}
		if (sms!=null&&sms.size()!=0) {
			dealType=dealType+","+TypeClass.sms;
			sms = getSmsVoWithNewContent(sms,dealWayVO.getId());
			String jsonSms = JsonMapper.toJsonString(sms);
			saveAlarmItemDeal(userId,userName, jsonSms, guid, TypeClass.sms);
		}
		dealType=dealType.substring(1,dealType.length());
		alarmdeal.setDealType(dealType);
		save(alarmdeal);
		if (ticket!=null){
			saveAlarmItemDeal(userId,userName, ticket, guid, TypeClass.ticket);
		}
		changeAnalysisStatusInDealing(ids, userName);
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		result.setData(guid);
		return result;
	}

	/**
	 * 设置roleId和roleName的角色至告警处置当中
	 * @param alarmdeal
	 */
	private void setRoleInfoToAlarmdeal(AlarmDeal alarmdeal) {
		com.vrv.vap.common.model.User currentUser = SessionUtil.getCurrentUser();
		if(currentUser!=null) {
			List<Integer> roleIds = currentUser.getRoleIds();
			List<String> roleName = currentUser.getRoleName();
			Integer[] roleIdArr = roleIds.toArray(new Integer[roleIds.size()]);
			String[] roleNameArr = roleName.toArray(new String[roleName.size()]);
			String roleIdStr = ArrayUtil.join(roleIdArr, ",");
			String roleNameStr = ArrayUtil.join(roleNameArr, ",");
			alarmdeal.setRoleId(roleIdStr);
			alarmdeal.setRoleName(roleNameStr);			
		}
	}


	/**
	 * 获得新的MailVO
	 * @return
	 */
	private List<MailVO> getMailVoWithNewContent(List<MailVO> mails,String guids){
		String mailAndSmsMergeContent = getMailAndSmsMergeContent(guids); //搞得告警合并邮件和短信内容
		for (MailVO mailVO : mails) {
			mailVO.setContent(mailAndSmsMergeContent);
		}
		return mails;
	}
	
	/**
	 * 获得新的短信smsVO
	 * @param sms
	 * @param guids
	 * @return
	 */
	private List<SmsVO> getSmsVoWithNewContent(List<SmsVO> sms,String guids){
		String mailAndSmsMergeContent = getMailAndSmsMergeContent(guids); //搞得告警合并邮件和短信内容
		for (SmsVO smsVO : sms) {
			smsVO.setContent(mailAndSmsMergeContent);
		}
		return sms;
	}
	
	/**
	 * 获得合并邮件信息内容
	 * 根据选择合并告警会的对应的信息内容
	 * @return
	 */
	private String getMailAndSmsMergeContent(String guids){
		Gson gson = new Gson();
		List<Map<String,Object>> list = new ArrayList<>();
		String[] guidArr = guids.split(",");
		for (String guid : guidArr) {
			 Map<String,Object> map = new HashMap<>();
			 WarnResultLogTmpVO warnResult = warnResultForEsService.getAlarmById(guid);
			 String triggerTime = DateUtil.format(warnResult.getTriggerTime());
			 String riskEventName = warnResult.getRiskEventName();
			 Integer weight = warnResult.getWeight();
			 String srcIps = warnResult.getSrc_ips();
			 String dstIps = warnResult.getDstIps();
			 String ruleName = warnResult.getRuleName();
			 map.put("告警规则名称", ruleName);
			 map.put("事件分类名称", riskEventName);
			 map.put("发生时间", triggerTime);
			 map.put("告警等级", weight);
			 map.put("告警源IP", srcIps);
			 map.put("目的IP", dstIps);
			 list.add(map);
		}
		String jsonContent = gson.toJson(list);
		return jsonContent;
	}
	
	/**
	 * 获得半年人的告警处置信息
	 * @return
	 */
	
	@Override
    public List<DealTaskstaticVO> getDealTaskAssignList() {
		List<DealTaskstaticVO> list = new ArrayList<>();
		String mouthByNum = DateUtil.addNMouth(-6).substring(0,7);
		String nowdayMouth = DateUtil.format(new Date()).substring(0, 7);
		//获得当前登录用户
		Integer currentLoginUserId = getCurrentLoginUserId();
		logger.info("当前用户的登录Id是："+currentLoginUserId);
		List<String> betweenMounths = DateUtil.getBetweenMounths(mouthByNum, nowdayMouth, DateUtil.Year_Mouth);
		List<Map<String,Object>> dealitemPeople = getDealitemPeople(mouthByNum, nowdayMouth);
		if(currentLoginUserId.equals(ADMIN_ID)) {
			getDealPersonListByAdmin(list, mouthByNum, nowdayMouth, betweenMounths, dealitemPeople);			
		}else {
			getDealPersonListByUserId(list,currentLoginUserId, mouthByNum, nowdayMouth,betweenMounths, dealitemPeople);			
		}
		return list;
	}

	
	
	
	/**
	 * admin获得处理人的权限
	 * @param list
	 * @param mouthByNum
	 * @param nowdayMouth
	 * @param betweenMounths
	 * @param dealitemPeople
	 */
	private void getDealPersonListByAdmin(List<DealTaskstaticVO> list, String mouthByNum, String nowdayMouth,List<String> betweenMounths, List<Map<String,Object>> dealitemPeople) {
		for (Map<String, Object> map : dealitemPeople) {
			DealTaskstaticVO dealTaskstaticVO = new DealTaskstaticVO();
			Map<String, Object> info = new HashMap<>();
			Object peopleObj = map.get("people");
			String people = null;
			if(peopleObj!=null) {
				people = peopleObj.toString();
				Integer userId = Integer.valueOf(people);
				setAlarmDealInfo(list, mouthByNum, nowdayMouth, betweenMounths, dealTaskstaticVO, info, people, userId);
			}
		}
	}

	/**
	 * 设置告警处置信息
	 * @param list
	 * @param mouthByNum
	 * @param nowdayMouth
	 * @param betweenMounths
	 * @param dealTaskstaticVO
	 * @param info
	 * @param people
	 * @param userId
	 */
	private void setAlarmDealInfo(List<DealTaskstaticVO> list, String mouthByNum, String nowdayMouth,
			List<String> betweenMounths, DealTaskstaticVO dealTaskstaticVO, Map<String, Object> info, String people,
			Integer userId) {
		User user = FeignCache.getUserById(userId.toString());
		if(user!=null){
			Map<String, Object> allMap = getDealInfoMap(mouthByNum, nowdayMouth, betweenMounths, people,"all");
			Map<String, Object> dealedMap = getDealInfoMap(mouthByNum, nowdayMouth, betweenMounths, people,"dealed");
			Map<String, Object> dealingMap = getDealInfoMap(mouthByNum, nowdayMouth, betweenMounths, people,"dealing");
			info.putAll(allMap);
			info.putAll(dealedMap);
			info.putAll(dealingMap);
			dealTaskstaticVO.setInfo(info);
			dealTaskstaticVO.setUser(user);
			list.add(dealTaskstaticVO);
		}
	}
	
	/**
	 * 非admin获得告警处置的信息
	 * @param list
	 * @param currentLoginUserId
	 * @param mouthByNum
	 * @param nowdayMouth
	 * @param betweenMounths
	 * @param dealitemPeople
	 */
	private void getDealPersonListByUserId(List<DealTaskstaticVO> list, Integer currentLoginUserId,String mouthByNum, String nowdayMouth,List<String> betweenMounths, List<Map<String,Object>> dealitemPeople) {
		for (Map<String, Object> map : dealitemPeople) {
			DealTaskstaticVO dealTaskstaticVO = new DealTaskstaticVO();
			Map<String, Object> info = new HashMap<>();
			Object peopleObj = map.get("people");
			String people = null;
			if(peopleObj!=null) {
				people = peopleObj.toString();
				Integer userId = Integer.valueOf(people);
				if(currentLoginUserId.equals(userId)) {					
					setAlarmDealInfo(list, mouthByNum, nowdayMouth, betweenMounths, dealTaskstaticVO, info, people,userId);
				}
			}
		}
		
	}
	
	/**
	 * 获得当前登录用户的Id
	 * @return
	 */
	private Integer getCurrentLoginUserId(){
		com.vrv.vap.common.model.User user = SessionUtil.getCurrentUser();
		Integer userId = user.getId();
		return userId;
	}

	/**
	 * 获得处理的信息
	 * @param mouthByNum
	 * @param nowdayMouth
	 * @param betweenMounths
	 * @param people
	 * @param status
	 * @return
	 */
	private Map<String, Object> getDealInfoMap(String mouthByNum, String nowdayMouth, List<String> betweenMounths,
			String people,String status) {
		Map<String,Object> allMap = new HashMap<>();
		List<Map<String,Object>> allList = getDealAlarmCountByStatus(mouthByNum, nowdayMouth, people, status); //获得所有告警信息
		SocUtil.dealAlarmdealInfo(allList, betweenMounths, null,"triggerTime");
		allMap.put(status, allList);
		return allMap;
	}

	
	
	/**
	 * 根据用户权限获得对应的数据
	 */
	@Override
	public List<Map<String, Object>> getDealedAlarmByUser(String userId, String riskEventName, String nowDay,
			String beforeMouthDay) {
		List<Map<String,Object>> list = alarmDealDao.getDealedAlarmByUser(userId, riskEventName, nowDay, beforeMouthDay);
		return list;
	}
	
	
	
}

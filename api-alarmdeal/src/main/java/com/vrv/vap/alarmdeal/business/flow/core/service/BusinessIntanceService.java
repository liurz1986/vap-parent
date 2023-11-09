package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model.EventAlarmTaskNodeMsg;
import com.vrv.vap.alarmdeal.business.asset.util.ExecutorServiceVrvUtil;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.config.GlobalEventListener;
import com.vrv.vap.alarmdeal.business.flow.core.config.InstanceEndListener;
import com.vrv.vap.alarmdeal.business.flow.core.constant.CollabrationConstant;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.constant.MyTicketConstant;
import com.vrv.vap.alarmdeal.business.flow.core.dao.BusinessInstanceDao;
import com.vrv.vap.alarmdeal.business.flow.core.model.*;
import com.vrv.vap.alarmdeal.business.flow.core.repository.BusinessIntanceRepository;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessCollaboration;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessIntanceVO;
import com.vrv.vap.alarmdeal.business.flow.core.vo.FlowMessageVO;
import com.vrv.vap.alarmdeal.business.flow.monitor.vo.MyTicketTreeVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.dao.MyTicketDao;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicketForminfo;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.alarmdeal.business.flow.processdef.util.FlowQueUtil;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.*;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.util.GwParamsUtil;
import com.vrv.vap.exportAndImport.excel.ExcelUtils;
import com.vrv.vap.exportAndImport.excel.util.DateUtils;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessIntanceService extends BaseServiceImpl<BusinessIntance, String> {

	private static Logger logger = LoggerFactory.getLogger(BusinessIntanceService.class);
	@Autowired
	private BusinessIntanceRepository businessIntanceRepository;
	@Autowired
	private BusinessInstanceDao businessInstanceDao;
	@Autowired
	private FlowService flowService;
	@Autowired
	private BusinessTaskLogService businessTaskLogService;
	@Autowired
    GlobalEventListener globalEventListener;
	@Autowired
	private MyTicketService myTicketService;
	@Autowired
	private AuthService authService;
	@Autowired
	private MyTicketDao myTicketDao;
	@Autowired
	private MyTicketService mTicketService;
	@Autowired
	private BusinessTaskCandidateService businessTaskCandidateService;

	@Autowired
	private BusinessTaskService businessTaskService;

	@Autowired
	private MapperUtil mapperUtil;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private FileConfiguration fileConfiguration;

	@Override
	public BaseRepository<BusinessIntance, String> getRepository() {
		return businessIntanceRepository;
	}
	@Autowired
	private AlarmEventManagementForESService alarmEventManagementForESService;

	@PostConstruct
	public void init() {
		globalEventListener.register(new InstanceEndListener() {
			@Override
			public void end(String processInstanceId, BusinessInstanceStatEnum endcanceled) {
				endInstance(processInstanceId, endcanceled);
			}
		});
	}

	// 创建了一个没有关联实例id的流程实例
	public Result<BusinessIntance> createProcessInstance(String userId, WorkDataVO data,MyTicket myTicket) {
		Map<String,Object> forms=data.getForms();
		String ticketName = myTicket.getName()==null?"":myTicket.getName().trim();
		if(forms.containsKey("triggerTime")&& forms.get("triggerTime")!=null){
			try {
				String triggerTime=String.valueOf(forms.get("triggerTime"));
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				forms.put("triggerTime",format.format(new Date(Long.valueOf(triggerTime))));
			} catch (NumberFormatException e) {
				logger.info("triggerTime类型有误",e);
			}
		}
		// 获取流程实例id: 外部id进行统一：事件处置eventId、协办和预警uuid统一为instanceId，后面增加的也统一为businessId  2022-11-8
		String instanceId = getBusinessId(forms);
		Date deadLineDate = getDeadLineDate(data.getDeadlineDate());
		BusinessIntance businessIntance = BusinessIntance.builder().guid(instanceId)
				.busiArgs(JsonMapper.toJsonString(data.getForms())).code(data.getCode()).createDate(new Date())
				.createUserId(userId).dealPeoples(BusinessIntanceService.assembleDealPeoples(userId))
				.name(data.getName()).processDefGuid(data.getProcessdefGuid())
				.statEnum(BusinessInstanceStatEnum.dealing).contextId(""+ "|,|").contextKey(""+ "|,|")
				.deadlineDate(deadLineDate).build();
		save(businessIntance);
		return ResultUtil.success(businessIntance);
	}

	private Date getDeadLineDate(String deadlineDate){
		Date date = new Date();
		if(StringUtils.isNotEmpty(deadlineDate)){
			date=stringToDate(deadlineDate);
		}
		return date;
	}


	private Date stringToDate(String source){
		Date date=new Date();
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(source.contains("Z")){
				source = source.replace("Z", " UTC");//UTC是本地时间
				format =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
			}
			date=format.parse(source);
		}catch (Exception e){
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 增加外部数据作为流程实例id，外部数据固定取自forms中businessId，
	 *  如果businessId存在不为空，以该值作为流程实例id (这种是为了流程实例与具体直接关联)
	 *  如果businessId不存在，以动态获取uuid作为流程实例id
	 * 2022-11-08
	 * @param forms
	 * @return
	 */
	private String getBusinessId(Map<String, Object> forms) {
		if(null == forms || forms.size() == 0 || org.springframework.util.StringUtils.isEmpty(forms.get("businessId"))){
			return UUIDUtils.get32UUID();
		}
		String intanceId = String.valueOf(forms.get("businessId"));
		// 针对外部id作为流程实例id，避免重复创建
		BusinessIntance instance = this.getOne(intanceId);
		if(null != instance){
			logger.error("已经存在流程了，不能创建流程了,当前businessId："+intanceId);
			throw  new FlowException(-1,"已经存在流程了，不能创建流程了,当前businessId："+intanceId);
		}
		return intanceId;
	}

	/**
	 * 改变工单状态
	 * @param businessCollaboration
	 * @return
	 */
	public Result<Boolean> changeTicketStatus(BusinessCollaboration businessCollaboration) {
		String ticketId = businessCollaboration.getTicketId();
		businessCollaboration.setUserId("31");  //下级反馈给上级没有userId
		BusinessIntance businessIntance = getOne(ticketId);
		businessIntance.setStatEnum(BusinessInstanceStatEnum.dealing);
		save(businessIntance);
		addBusinessLogRecord(businessCollaboration, businessIntance, CollabrationConstant.EXPATRIATEED);
		return ResultUtil.success(true);
	}
	
	
	public BusinessIntance getByInstanceId(String processInstanceId) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("processInstanceId", processInstanceId));
		List<BusinessIntance> findAll = findAll(conditions);
		if (findAll.size() == 0) {
			return null;
		} else if (findAll.size() > 1) {
			throw new RuntimeException("流程实例出现多个:" + processInstanceId);
		}

		return findAll.get(0);
	}
	
	

	/**
	 * 创建流程操作日志记录
	 * @param businessCollaboration
	 * @param businessIntance
	 */
	private void addBusinessLogRecord(BusinessCollaboration businessCollaboration, BusinessIntance businessIntance,String status) {
		String userId = businessCollaboration.getUserId();
		BusinessTaskLog businessTaskLog = new BusinessTaskLog();
		businessTaskLog.setId(UUID.randomUUID().toString());
		businessTaskLog.setPeopleId(userId);
		User user = null;
		try {
			user = authService.getUserInfoByUserId(userId);			
		}catch(Exception e) {
			logger.error("feign接口", e);
		}
		if(user != null) {
			businessTaskLog.setPeopleName(user.getName());
		} else {
			businessTaskLog.setPeopleName(userId);
		}
		businessTaskLog.setTaskDefindName(status);
		businessTaskLog.setTaskDefineKey(status);
		businessTaskLog.setProcessInstanceId(businessIntance.getProcessInstanceId());
		businessTaskLog.setProcessKey(businessIntance.getProcessDefGuid());
		businessTaskLog.setTime(DateUtil.format(new Date()));
		businessTaskLog.setAction(status);
		businessTaskLog.setAdvice(status);
		businessTaskLogService.save(businessTaskLog);
	}
	
	
	private String replaceIp(String url, String ip) {
		String replaceFirst = url.replaceAll("\\{ip\\}", ip);
		return replaceFirst;
	}
	
	
	
	/**
	 * 工单统计分类查询
	 * @return
	 */
	public List<Map<String, Object>> queryBusinessInstanceStatics() {
		List<Map<String, Object>> queryBusinessInstanceStatics = businessInstanceDao.queryBusinessInstanceStatics();
		return queryBusinessInstanceStatics;
	}

	/**
	 * 流程分类下：增加内部工单、外部工单
	 * 2022-10-31
	 * @param userId
	 * @return
	 */
	public List<MyTicketTreeVO> findUserInstancesTree(String userId) {
		List<MyTicketTreeVO> result = new ArrayList<>();
		MyTicketTreeVO root = new MyTicketTreeVO();
		root.setKey("0");
		root.setChildren(new ArrayList<>());
		root.setTitle("流程分类");
		result.add(root);
		List<Map<String,Object>> list = myTicketDao.queryRelateTicket(userId);
		int ticketTypeOne = 0; // 内部工单数量
		int ticketTypeTwo = 0; // 外部工单数量
		List<MyTicketTreeVO> ticketTypes= new ArrayList<>();
		List<MyTicketTreeVO> ticketTypeOnes= new ArrayList<>();
		List<MyTicketTreeVO> ticketTypeTwos= new ArrayList<>();
		for (Map<String, Object> map : list) {
			Integer count = Integer.valueOf(map.get("count").toString());
			String processDefName = map.get("process_def_name").toString();
			String ticketType = myTicketDao.getTicktType(processDefName); // 1，表示内部工单 ，2表示外部工单
			MyTicketTreeVO myTicketTree = new MyTicketTreeVO();
			myTicketTree.setKey(processDefName);
			myTicketTree.setCount(count);
			if(MyTicketConstant.TICKETTYPEONE.equals(ticketType)){
				ticketTypeOne = ticketTypeOne+ count ;
				ticketTypeOnes.add(myTicketTree);
			}else if(MyTicketConstant.TICKETTYPETWO.equals(ticketType)){
				ticketTypeTwo = ticketTypeTwo+count;
				ticketTypeTwos.add(myTicketTree);
			}
		}
		// 构造内外部
		MyTicketTreeVO ticketTypeOnevo = new MyTicketTreeVO();
		ticketTypeOnevo.setKey(MyTicketConstant.TICKETTYPEONEDESC);
		ticketTypeOnevo.setCount(ticketTypeOne);
		ticketTypeOnevo.setChildren(ticketTypeOnes);
		MyTicketTreeVO ticketTypeTwovo= new MyTicketTreeVO();
		ticketTypeTwovo.setKey(MyTicketConstant.TICKETTYPETWODESC);
		ticketTypeTwovo.setCount(ticketTypeTwo);
		ticketTypeTwovo.setChildren(ticketTypeTwos);
		ticketTypes.add(ticketTypeOnevo);
		ticketTypes.add(ticketTypeTwovo);
		root.setChildren(ticketTypes);
		return result;

	}
	
	
	public List<MyTicketTreeVO> findUserInstancesBysecTree(String header,String userId) {
		List<BaseSecurityDomain> securityDomains = authService.byUserId(header);    
		Set<String> userIds = getUsersByCode(securityDomains);
		userIds.add(userId);
	    List<MyTicketTreeVO> result = new ArrayList<>();
		MyTicketTreeVO root = new MyTicketTreeVO();
		root.setKey("0");
		root.setChildren(new ArrayList<>());
		root.setTitle("流程分类");
		result.add(root);
		List<Map<String,Object>> list = myTicketDao.queryRelateTicketBySec(userIds);
		for (Map<String, Object> map : list) {
			Integer count = Integer.valueOf(map.get("count").toString());
			String processDefName = map.get("process_def_name").toString();
			MyTicketTreeVO myTicketTree = new MyTicketTreeVO();
			myTicketTree.setKey(processDefName);
			myTicketTree.setCount(count);
			root.getChildren().add(myTicketTree);
		}
		return result;   
	
	}
	
	
	
	
	/**
	 * 获取用户经手过的任务（该用户所在安全域的其他用户也可以看见）
	 * @param userId
	 * @param example
	 * @param pageable
	 * @return
	 */
	public Page<BusinessIntanceVO> findUserInstancesBySec(String header, String userId, BusinessIntance example, Pageable pageable){
		List<BaseSecurityDomain> securityDomains = authService.byUserId(header);
		Set<String> userIds = getUsersByCode(securityDomains);
		userIds.add(userId);
		List<BusinessIntanceVO> businessIntanceVOTotal = new ArrayList<>();
		long total = 0L; 
		for (String user : userIds) {
			List<QueryCondition> conditions = new ArrayList<>();
			conditions.add(QueryCondition.like("dealPeoples", assembleDealPeoples(user)));
			if(example==null){
				example = new BusinessIntance();
			}
			Page<BusinessIntance> findAll = findAll(example, conditions, pageable);
			total+=findAll.getTotalElements();
			List<QueryCondition> conditionList=new ArrayList<>();
			conditionList.add(QueryCondition.eq("candidate",userId));
			List<BusinessTaskCandidate> businessTaskCandidateList=businessTaskCandidateService.findAll(conditionList);
			List<String> taskIds=businessTaskCandidateList.stream().map(item->item.getTaskId()).collect(Collectors.toList());
			List<BusinessIntanceVO> businessIntanceVOList=mapperUtil.mapList(findAll.getContent(),BusinessIntanceVO.class);
			for(BusinessIntanceVO businessIntanceVO:businessIntanceVOList){
				construcMyInstanceDeals(taskIds, businessIntanceVO);
			}
			
			for (BusinessIntanceVO businessIntanceVO : businessIntanceVOList) {
				 boolean judgeIsEqual = judgeIsEqual(businessIntanceVO);
				 setBusinessInstanceAuthInfo(userId, businessIntanceVO, judgeIsEqual);
			}
			
			for (BusinessIntanceVO businessIntanceVO : businessIntanceVOList) {
				selectBusiArg(businessIntanceVO);
			}
			
			businessIntanceVOTotal.addAll(businessIntanceVOList);
		}
		businessIntanceVOTotal = getBusinessInstanceVOs(pageable, businessIntanceVOTotal);
		return new PageImpl<>(businessIntanceVOTotal,pageable,total);
	}

	/**
	 * 获得分页的数据
	 * @param pageable
	 * @param businessIntanceVOTotal
	 * @return
	 */
	private List<BusinessIntanceVO> getBusinessInstanceVOs(Pageable pageable,
			List<BusinessIntanceVO> businessIntanceVOTotal) {
//		int pageNumber = pageable.getPageNumber();  //第几页
		int pageSize = pageable.getPageSize();   //每页的数量
//		int begin = pageNumber*pageSize;
//		int end = pageNumber*pageSize+pageSize;
		if(businessIntanceVOTotal.size()<pageSize) {
			return businessIntanceVOTotal;
		}
		businessIntanceVOTotal = businessIntanceVOTotal.subList(0, pageSize-1);
		return businessIntanceVOTotal;
	}
	
	

	

	/**
	 * 获得登录用户关联安全域下对应的人员
	 * @param securityDomains
	 * @return
	 */
	private Set<String> getUsersByCode(List<BaseSecurityDomain> securityDomains) {
		Set<String> users = new HashSet<>();
		for (BaseSecurityDomain baseSecurityDomain : securityDomains) {
			String code = baseSecurityDomain.getCode();
			Map<String,Object> map = new HashMap<>();
			map.put("code", code);
            List<String> secList = authService.byCode(map);
            users.addAll(secList);
            
		}
		return users;
	}
	
	public static void main(String[] args) {
		String context = "|,|bd8b20a6-8d1d-4140-9dab-106ecabfdd30|,|bd8b20a6-8d1d-4140-9dab-106ecabfdd30/424535f0-6fae-47b6-a490-d91830c20470\r\n" + 
				"|,|bd8b20a6-8d1d-4140-9dab-106ecabfdd30/c26858d7-bca6-401b-a4b6-2d008b11d102|,||,|";
		String[] split = context.split("\\|,\\|",-1);
		System.out.println(split);
	}

	/**
	 * 获取用户经手过的任务
	 *
	 * 流程分类下：增加内部工单、外部工单菜单节点
	 * 	2022-11-2
	 * @param userId
	 * @param pageable
	 * @return
	 */
	public Page<BusinessIntanceVO> findUserInstances(String userId, BusinessIntance example, Pageable pageable){
		// 内外部工单节点处理 2022-11-1
		String nodeName = example == null?"":example.getProcessDefName();
		boolean isTicketTypeNode = MyTicketConstant.isNodeTrue(nodeName);
		if(!isTicketTypeNode){
			// 不是内外部菜单节点按照以前的逻辑
			return getBusinessInstaceOld(userId,example,pageable);
		}else{
			// 是内外部菜单节点处理
			return getBusinessInstaceNew(userId,example,pageable,nodeName);
		}
	}
	private Page<BusinessIntanceVO> getBusinessInstaceOld(String userId, BusinessIntance example, Pageable pageable) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.like("dealPeoples", assembleDealPeoples(userId)));
		if(example==null){
			example = new BusinessIntance();
		}
		Page<BusinessIntance> findAll = findAll(example, conditions, pageable);
		List<QueryCondition> conditionList=new ArrayList<>();
		conditionList.add(QueryCondition.eq("candidate",userId));
		List<BusinessTaskCandidate> businessTaskCandidateList=businessTaskCandidateService.findAll(conditionList);
		List<String> taskIds=businessTaskCandidateList.stream().map(item->item.getTaskId()).collect(Collectors.toList());
		List<BusinessIntanceVO> businessIntanceVOList=mapperUtil.mapList(findAll.getContent(),BusinessIntanceVO.class);
		for(BusinessIntanceVO businessIntanceVO:businessIntanceVOList){
			construcMyInstanceDeals(taskIds, businessIntanceVO);
		}
		for (BusinessIntanceVO businessIntanceVO : businessIntanceVOList) {
			boolean judgeIsEqual = judgeIsEqual(businessIntanceVO);
			setBusinessInstanceAuthInfo(userId, businessIntanceVO, judgeIsEqual);
		}

		for (BusinessIntanceVO businessIntanceVO : businessIntanceVOList) {
			selectBusiArg(businessIntanceVO);
		}
		return new PageImpl<>(businessIntanceVOList,pageable,findAll.getTotalElements());
	}
	/**
	 * 是内外部节点处理
	 * 2022-11-1
	 * @param userId
	 * @param example
	 * @param pageable
	 * @param nodeName
	 * @return
	 */
	private Page<BusinessIntanceVO> getBusinessInstaceNew(String userId, BusinessIntance example, Pageable pageable,String nodeName) {
		List<QueryCondition> conditionList=new ArrayList<>();
		conditionList.add(QueryCondition.eq("ticketType",MyTicketConstant.queryCodeByName(nodeName)));
		List<MyTicket> myTickets = myTicketService.findAll(conditionList);
		if(CollectionUtils.isEmpty(myTickets)){
			return new PageImpl<>(new ArrayList<>(),pageable,0);
		}
		List<String> guids= myTickets.stream().map(item -> item.getGuid()).collect(Collectors.toList());

		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.like("dealPeoples", assembleDealPeoples(userId)));
		conditions.add(QueryCondition.in("processDefGuid",guids));
		if(example==null){
			example = new BusinessIntance();
		}
		example.setProcessDefName(null);
		Page<BusinessIntance> findAll = findAll(example, conditions, pageable);
		conditionList=new ArrayList<>();
		conditionList.add(QueryCondition.eq("candidate",userId));
		List<BusinessTaskCandidate> businessTaskCandidateList=businessTaskCandidateService.findAll(conditionList);
		List<String> taskIds=businessTaskCandidateList.stream().map(item->item.getTaskId()).collect(Collectors.toList());
		List<BusinessIntanceVO> businessIntanceVOList=mapperUtil.mapList(findAll.getContent(),BusinessIntanceVO.class);
		for(BusinessIntanceVO businessIntanceVO:businessIntanceVOList){
			construcMyInstanceDeals(taskIds, businessIntanceVO);
		}
		for (BusinessIntanceVO businessIntanceVO : businessIntanceVOList) {
			boolean judgeIsEqual = judgeIsEqual(businessIntanceVO);
			setBusinessInstanceAuthInfo(userId, businessIntanceVO, judgeIsEqual);
		}

		for (BusinessIntanceVO businessIntanceVO : businessIntanceVOList) {
			selectBusiArg(businessIntanceVO);
		}
		return new PageImpl<>(businessIntanceVOList,pageable,findAll.getTotalElements());
	}
	/**
	 * 筛选BusiArg当中的数据
	 * @param businessIntanceVO
	 */
	private void selectBusiArg(BusinessIntanceVO businessIntanceVO) {
		String contextId = businessIntanceVO.getContextId();
		Set<String> contextIdArr = businessIntanceVO.getContextIdArr();
		if(StringUtils.isNotEmpty(contextId)&&contextIdArr!=null&&contextIdArr.size()!=0) {
			String busiArgs = businessIntanceVO.getBusiArgs();
			Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
			String processDefGuid = businessIntanceVO.getProcessDefGuid();
			MyTicket myTicket = myTicketService.getOne(processDefGuid);
			if(myTicket!=null) {
				MyTicketForminfo myTicketForminfo = myTicket.getForminfo();
				String formData = myTicketForminfo.getFormData();
				FormInfoVO formInfos = JsonMapper.fromJsonString(formData, FormInfoVO.class);
				List<UsedFieldVO> list = formInfos.getFormInfos().getUsedFields();
				for (UsedFieldVO formVO : list) {
					Map<String, Object> context = formVO.getOption();
					if(null == context){
						continue;
					}
					if(context.containsKey("isSign")&&context.get("isSign")!=null) {
						Boolean isSign = (Boolean)context.get("isSign");
						if(isSign) { //确认是会签结点
							String fkey = context.get("fieldId").toString();
							List<Map<String,Object>> selectMaps = new ArrayList<>();
							List<String> conIds= new ArrayList<String>(); // 存放contextId，主要是防止contextId重复处理 2022-12-29
							if(map.containsKey(fkey)&&map.get(fkey)!=null) {
								Object object = map.get(fkey);
								if(object instanceof List<?>) { //累加
									List<Map<String,Object>> objList = (List<Map<String,Object>>)object;
									for (Map<String, Object> map2 : objList) {
										if(map2.containsKey("contextId")&&map2.get("contextId")!=null) {
											String curContextId = map2.get("contextId").toString();
											for (String contextIds  : contextIdArr) {
												if(curContextId.contains(contextIds) || contextIds.contains(curContextId)) {
													if(!conIds.contains(curContextId)){ // 防止数据重复
														conIds.add(curContextId);
														selectMaps.add(map2);
													}
												}
											}
										}
									}
									map.put(fkey, selectMaps);
								}else {
									//throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "会签组件不是数组类型请检查！");
									logger.error("会签组件不是数据类型，请检查！");
								}
							}
						}						
					}
				}
			}
			businessIntanceVO.setBusiArgs(JsonMapper.toJsonString(map));
		}
	}

	/**
	 * 设置contextId和contextKey权限的数据
	 * @param userId
	 * @param businessIntanceVO
	 * @param judgeIsEqual
	 */
	private void setBusinessInstanceAuthInfo(String userId, BusinessIntanceVO businessIntanceVO, boolean judgeIsEqual) {
		if(judgeIsEqual) {
			 int minCount = 0;  //全局的最小个数
			 int minLength = Integer.MAX_VALUE; //最短的长度
			 int minLocal = 0 ; //最短长度的位置
			 List<Integer> locationList = getLocationList(businessIntanceVO,userId);
			 
			 String contextId = businessIntanceVO.getContextId();
			 String contextKey = businessIntanceVO.getContextKey();
		     String[] contextKeyArr = contextKey.split("\\|,\\|",-1);
		     String[] contextIdArr = contextId.split("\\|,\\|",-1);
		     List<String> contextIds = new ArrayList<>();
		     List<String> contextKeys = new ArrayList<>();
		     for (int i = 0; i < locationList.size(); i++) {
		    	 Integer local = locationList.get(i);                	 
		    	 String cur = contextKeyArr[local];
		    	 if(StringUtils.isNotEmpty(cur)) {
		    		 String[] split = cur.split("\\/");
		    		 if(split.length==minLength) {
		    			 minLength=split.length;
		    			 minCount++; 
		    			 minLocal = local;
		    			 contextKeys.add(contextKeyArr[minLocal]);
		    			 contextIds.add(contextIdArr[minLocal]);
		    		 }
		    		 if(split.length<minLength) {
		    			 minLength=split.length;
		    			 minCount = 1;
		    			 minLocal = local;
		    			 contextKeys.add(contextKeyArr[minLocal]);
		    			 contextIds.add(contextIdArr[minLocal]);
		    		 }		    		 
		    	 }
			 }			     
		     if(minCount!=1) {  //存在多个contextkey
		    	 boolean flag = judgeIsRepeateElementIndArr(contextKeys);
		    	 if(flag) {
		    		 businessIntanceVO.setContextKey(contextKeyArr[minLocal]);
		    		 businessIntanceVO.setContextId(contextIdArr[minLocal]);
		    		 Set<String> set = new HashSet<>(contextIds);
		    		 businessIntanceVO.setContextIdArr(set);
		    	 }else {
		    		 businessIntanceVO.setContextKey("");
		    		 businessIntanceVO.setContextId("");
		    		 businessIntanceVO.setContextIdArr(new HashSet<>());
		    	 }
		    	 
		     }else {
		    	// Integer curMinLocal = locationList.get(minLocal);  //现在最小的位置
		    	 businessIntanceVO.setContextKey(contextKeyArr[minLocal]);
		    	 businessIntanceVO.setContextId(contextIdArr[minLocal]);
		    	
		    	 Set<String> set = new HashSet<>();
		    	 for (int i = 0; i < locationList.size(); i++) {
		    		 String id = contextIdArr[locationList.get(i)];
		    		 if(StringUtils.isNotEmpty(id)) {
		    			 set.add(contextIdArr[locationList.get(i)]);		    			 
		    		 }
				}
		    	businessIntanceVO.setContextIdArr(set);
		     }
		 }
	}
	
	private  boolean judgeIsRepeateElementIndArr(List<String> contextKeys) {
		boolean flag = true;
		for (int i = 0; i < contextKeys.size()-1; i++) {
			for (int j = 1; j < contextKeys.size(); j++) {
				if(!contextKeys.get(i).equals(contextKeys.get(j))) {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}
	
	
	
	

	/**
	 * 判断contextId,contextKey,dealPeoples个数是否相等
	 * @param businessIntanceVO
	 * @return
	 */
	private boolean judgeIsEqual(BusinessIntanceVO businessIntanceVO) {
		String dealPeoples = businessIntanceVO.getDealPeoples();
		String contextId = businessIntanceVO.getContextId();
		String contextKey = businessIntanceVO.getContextKey();
		if(StringUtils.isEmpty(contextId)||StringUtils.isEmpty(contextKey)) {
			return false;
		}
		String[] dealPeopleArr = dealPeoples.split("\\|,\\|",-1);
		String[] contextIdArr = contextId.split("\\|,\\|",-1);
		String[] contextKeyArr = contextKey.split("\\|,\\|",-1);
		if(dealPeopleArr.length==contextIdArr.length&&dealPeopleArr.length==contextKeyArr.length&&contextIdArr.length==contextKeyArr.length) {
			return true;
		}
		return false;
	}

	
	
	/**
	 * 获得对应的路径
	 * @param businessIntanceVO
	 * @param userId
	 * @return
	 */
	private List<Integer> getLocationList(BusinessIntanceVO businessIntanceVO,String userId) {
		List<Integer> locationList = new ArrayList<>();
		String dealPeoples = businessIntanceVO.getDealPeoples();
		String[] split = dealPeoples.split("\\|,\\|",-1);
		for (int i = 0; i < split.length; i++) {
			if(split[i].equals(userId)) {
				locationList.add(i);
			}
		}
		return locationList;
	}

	private void construcMyInstanceDeals(List<String> taskIds, BusinessIntanceVO businessIntanceVO) {
		String processInstanceId=businessIntanceVO.getGuid();
		List<BusinessTask> businessTaskList=businessTaskService.getByInstance(processInstanceId);
		for(BusinessTask businessTask:businessTaskList){
			String taskId=businessTask.getTaskId();
			if(taskIds.contains(taskId)){
				String nameStr = getDealPeopleNames(taskId);
				businessIntanceVO.setCandidatePerson(nameStr);
			}
		}
	}

	public String getDealPeopleNames(String taskId) {
		List<QueryCondition> conditionList=new ArrayList<>();
		conditionList.add(QueryCondition.eq("taskId",taskId));
		List<BusinessTaskCandidate> businessTaskCandidates=businessTaskCandidateService.findAll(conditionList);
		List<String> dealPeolpes=businessTaskCandidates.stream().map(item->item.getCandidateName()).filter(Objects::nonNull).collect(Collectors.toList());
		return String.join(",", dealPeolpes);
	}

	/**
	 * 构造工单监控处置人中文名称
	 * @param businessIntanceVO
	 */
	public void constructDealPeopleNames(BusinessIntanceVO businessIntanceVO) {
		String processInstanceId=businessIntanceVO.getGuid();
		List<BusinessTask> businessTaskList=businessTaskService.getByInstance(processInstanceId);
		String nameStr="";
		for(BusinessTask businessTask:businessTaskList){
			String taskId=businessTask.getTaskId();
			String dealPeolpes=getDealPeopleNames(taskId);
			nameStr= dealPeolpes+","+nameStr;
		}
		if(StringUtils.isNotEmpty(nameStr)){
			businessIntanceVO.setCandidatePerson(nameStr.substring(0,nameStr.length()-1));
		}
	}



	public static String assembleDealPeoples(String userId) {
		if (Strings.isNullOrEmpty(userId)) {
			throw new RuntimeException("userId不能为空,否则是漏洞");
		}
		return userId + "|,|";
	}

	/////////////////////////////////// 流程结束相关///////////////////////
	/**
	 * 通过processInstanceId获取instance并修改state
	 * 
	 * @param processInstanceId
	 * @param state
	 */
	public void endInstance(String processInstanceId, BusinessInstanceStatEnum state) {
		BusinessIntance byInstanceId = getByInstanceId(processInstanceId);
		String processDefGuid = byInstanceId.getProcessDefGuid();
		MyTicket myTicket = myTicketService.getOne(processDefGuid);
		if(myTicket!=null){
			User cuurentUser = flowService.getFlowCurrentuser(processInstanceId);
			byInstanceId.setStatEnum(state);
			byInstanceId.setFinishDate(new Date());
			save(byInstanceId);
			saveEndLog(processInstanceId, state, byInstanceId);
			// 流程结束处理发kafka消息 2021-08-30 事件id_timeStamp2022/09/07
			logger.info(byInstanceId.getProcessDefName()+"流程结束");
			sendKafakMsgEndNode(GwParamsUtil.getEventIdByFlowId(byInstanceId.getGuid()),3,cuurentUser);
			// 事件协办、事件预警流程结束发消息给队列 2022-10-13
			businessTaskService.sendFlowMsg(byInstanceId, FlowMessageVO.END,cuurentUser);
		}else {
			logger.error("myTicket为null");
		}
	}


	private void saveEndLog(String processInstanceId, BusinessInstanceStatEnum state, BusinessIntance byInstanceId) {
		BusinessTaskLog businessTaskLog = new BusinessTaskLog();
		String userId = flowService.getVariableByExecutionId(processInstanceId, FlowConstant.USERID).toString();
		businessTaskLog.setPeopleId(userId);
		User user = authService.getUserInfoByUserId(userId);
		if(user!=null){
			businessTaskLog.setPeopleName(user.getName());
		}
		businessTaskLog.setId(UUID.randomUUID().toString());
		businessTaskLog.setTaskDefindName("完成");
		businessTaskLog.setTaskDefineKey(state.toString());
		businessTaskLog.setProcessInstanceId(processInstanceId);
		businessTaskLog.setProcessKey(byInstanceId.getProcessDefGuid());
		try {
			Thread.sleep(1000); //睡一秒钟
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		businessTaskLog.setTime(DateUtil.format(new Date()));
		businessTaskLog.setAction(state.getText());
		if (state.equals(BusinessInstanceStatEnum.endCanceled)) {
			Object busiArgsObj = flowService.getBusiArgsByExecutionId(processInstanceId);
			HashMap<String, Object> map = (HashMap<String, Object>) busiArgsObj;
			if(map.containsKey("deleteReason")&&map.get("deleteReason")!=null){
				String deleteReason = map.get("deleteReason").toString();
				businessTaskLog.setAdvice(deleteReason);
			}
		} else {

			businessTaskLog.setAdvice("该工单已完结");
		}
		businessTaskLogService.save(businessTaskLog);
	}

	/**
	 * 工单监控导出
	 * 2023-1-3
	 * @param processDefName
	 * @return
	 */
	public Result<String> exprotExcel(String processDefName) {
		// 判断当前节点是不是内部工单或外部工单
		boolean isTicketTypeNode = MyTicketConstant.isNodeTrue(processDefName);
		List<QueryCondition> conditions = new ArrayList<>();
		List<ProcessExportExcelVO> lists= null;
		if(isTicketTypeNode){
			List<String> myTicketGuids =getMyTicketGuids(processDefName);
			lists = getProcessExportExcelVOs(myTicketGuids);
		}else{
			lists = getProcessExportExcelVOsByName(processDefName);
		}
		String flowPath =fileConfiguration.getFilePathFlow();
		File newFile= new File(flowPath);
		if (!newFile.exists()) {
			newFile.mkdirs();
		}
		String uuid="工单监控导出"+ DateUtils.date2Str(new Date(), "yyyyMMddHHmmss")+".xlsx";
		logger.info("fileName: "+uuid);
		String filePath=flowPath+ File.separator+uuid;
		try{
			ExcelUtils.getInstance().exportObjects2Excel(lists, ProcessExportExcelVO.class, true, filePath);
		}catch(Exception e){
			logger.error("工单监控数据导出失败",e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导出失败");
		}
		return ResultUtil.success(uuid);
	}

	private List<ProcessExportExcelVO> getProcessExportExcelVOs(List<String> myTicketGuids) {
		if(CollectionUtils.isEmpty(myTicketGuids)){
			return new ArrayList<>();
		}
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.in("processDefGuid",myTicketGuids));
        List<BusinessIntance> list = this.findAll(conditions);
		if(CollectionUtils.isEmpty(list)){
			return new ArrayList<>();
		}
		// 获取待办任务
		List<BusinessCandidateVO> candidateVOS = getCandidates(myTicketGuids);
		List<ProcessExportExcelVO> datas = new ArrayList<>();
		ProcessExportExcelVO exportExcelVO = null;
		for(BusinessIntance intance : list){
			exportExcelVO = new ProcessExportExcelVO();
			exportExcelVO.setName(intance.getName());
			exportExcelVO.setProcessDefName(intance.getProcessDefName());
			exportExcelVO.setCreateTime(intance.getCreateDate());
			exportExcelVO.setCreateuserName(intance.getCreateUserName());
			exportExcelVO.setDeadlineDate(intance.getDeadlineDate());
			exportExcelVO.setStatus(intance.getStatEnum().getText());
			exportExcelVO.setCandidatePerson(getCandidatePerson(candidateVOS,intance.getGuid()));
			datas.add(exportExcelVO);
		}
		return datas;
	}

	private String getCandidatePerson(List<BusinessCandidateVO> candidateVOS,String instanceGuid) {
		if(CollectionUtils.isEmpty(candidateVOS)){
			return null;
		}
		String person="";
		boolean status =false;
		for(BusinessCandidateVO vo : candidateVOS){
			if(instanceGuid.equals(vo.getBusinessGuid())){
				person = person+vo.getCandidataName()+",";
				status =true;
			}
		}
		if(status){
			person = person.substring(0,person.length()-1);
		}
		return person;
	}


	private List<BusinessCandidateVO> getCandidates(List<String> myTicketGuids) {
		String sql="select candidate.candidate_name as candidateName,instance.guid as guid  from business_task_candidate  as candidate inner join business_task as task on  candidate.busi_task_id=task.id " +
				" inner join business_intance as instance on instance.guid=task.instance_guid" +
				" where instance.process_def_guid in('"+StringUtils.join(myTicketGuids, "','")+"')";
		List<BusinessCandidateVO> list = jdbcTemplate.query(sql, new RowMapper<BusinessCandidateVO>() {
			@Override
			public BusinessCandidateVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusinessCandidateVO businessCandidateVO = new BusinessCandidateVO();
				businessCandidateVO.setBusinessGuid(rs.getString("guid"));
				businessCandidateVO.setCandidataName(rs.getString("candidateName"));
				return businessCandidateVO;
			}
		});
       return list;
	}

	private List<ProcessExportExcelVO> getProcessExportExcelVOsByName(String processDefName) {
		if(StringUtils.isEmpty(processDefName)){
			return new ArrayList<>();
		}
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("processDefName",processDefName));
		List<BusinessIntance> list = this.findAll(conditions);
		if(CollectionUtils.isEmpty(list)){
			return new ArrayList<>();
		}
		// 获取待办任务
		List<BusinessCandidateVO> candidateVOS = getCandidatesByProcessDefName(processDefName);
		List<ProcessExportExcelVO> datas = new ArrayList<>();
		ProcessExportExcelVO exportExcelVO = null;
		for(BusinessIntance intance : list){
			exportExcelVO = new ProcessExportExcelVO();
			exportExcelVO.setName(intance.getName());
			exportExcelVO.setProcessDefName(intance.getProcessDefName());
			exportExcelVO.setCreateTime(intance.getCreateDate());
			exportExcelVO.setCreateuserName(intance.getCreateUserName());
			exportExcelVO.setDeadlineDate(intance.getDeadlineDate());
			exportExcelVO.setStatus(intance.getStatEnum().getText());
			exportExcelVO.setCandidatePerson(getCandidatePerson(candidateVOS,intance.getGuid()));
			datas.add(exportExcelVO);
		}
		return datas;
	}

	private List<BusinessCandidateVO> getCandidatesByProcessDefName(String processDefName) {
		String sql="select candidate.candidate_name as candidateName,instance.guid as guid  from business_task_candidate  as candidate inner join business_task as task on  candidate.busi_task_id=task.id " +
				" inner join business_intance as instance on instance.guid=task.instance_guid" +
				" where instance.process_def_name ='"+processDefName+"'";
		List<BusinessCandidateVO> list = jdbcTemplate.query(sql, new RowMapper<BusinessCandidateVO>() {
			@Override
			public BusinessCandidateVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusinessCandidateVO businessCandidateVO = new BusinessCandidateVO();
				businessCandidateVO.setBusinessGuid(rs.getString("guid"));
				businessCandidateVO.setCandidataName(rs.getString("candidateName"));
				return businessCandidateVO;
			}
		});
		return list;
	}


	/**
	 * 获取内部工单或外部工单菜单节点对应流程guid
	 * @param processDefName
	 * @return
	 */
	public List<String> getMyTicketGuids(String processDefName) {
		String ticketType = MyTicketConstant.queryCodeByName(processDefName);
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("ticketType",ticketType));
		List<MyTicket> myTickets = mTicketService.findAll(conditions);
		if(CollectionUtils.isEmpty(myTickets)){
			return null;
		}
		List<String> guids = myTickets.stream().map(item -> item.getGuid()).collect(Collectors.toList());
		return guids;
	}

	/**
	 * 针对事件处理流程：结束后调用告警接口
	 * @param eventId
	 * @param status
	 * @param cuurentUser
	 */
	private void sendKafakMsgEndNode(String eventId, Integer status,User cuurentUser) {
		if (null == cuurentUser) {
			logger.info("获取当前用户为空");
			throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(),  "获取当前用户为空！");
		}
		EventAlarmTaskNodeMsg msg = new EventAlarmTaskNodeMsg();
		msg.setEventId(eventId); // 事件id，传过来的
		msg.setTaskName("");//流程节点名称、
		msg.setDealedPersonId(cuurentUser.getId());// 实际处理人id
		msg.setDealedPersonName(cuurentUser.getName());// 实际处理人名称
		msg.setEventAlarmStatus(status);//告警事件状态(开始状态，进行中，结束状态) 界面配置
		logger.info("流程结束后消息：" + JSON.toJSONString(msg));
		alarmEventMsg(msg);
	}

	// 流程结束处理调用事件处理接口
	private void alarmEventMsg(EventAlarmTaskNodeMsg msg) {
		try {
			logger.warn("流程结束处理调用事件处理接口：" + JSON.toJSONString(msg));
			alarmEventManagementForESService.saveEventAlarmDealChange(msg);
			logger.info("流程结束处理调用事件处理接口成功");
		} catch (Exception e) {
			logger.error("流程结束处理调用事件处理接口异常", e);
		}
	}


	private List<String> listHandle(List<Integer> roleIds) {
		List<String> roles = new ArrayList<>();
		for(Integer roleId : roleIds){
			roles.add(roleId.toString());
		}
		return roles;
	}

	/**
	 * 根据开始时间和结束时间获取事件处置流程数量
	 * 2023-04-3
	 * @param satartTime
	 * @param endTime
	 * @return
	 */
	public long getAlarmCount(Date satartTime,Date endTime){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("processDefName","事件处置流程"));
		conditions.add(QueryCondition.between("createDate",satartTime,endTime));
		return this.count(conditions);
	}
}

package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.constant.MyTicketConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.*;
import com.vrv.vap.alarmdeal.business.flow.core.repository.BusinessTaskRepository;
import com.vrv.vap.alarmdeal.business.flow.core.vo.FlowMessageVO;
import com.vrv.vap.alarmdeal.business.flow.monitor.vo.MyTicketTreeVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.dao.BusinessTaskDao;
import com.vrv.vap.alarmdeal.business.flow.processdef.dao.MyTicketDao;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicketForminfo;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.alarmdeal.business.flow.processdef.util.FlowQueUtil;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FormInfoVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FormInfos;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FormVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.UsedFieldVO;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import lombok.Synchronized;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BusinessTaskService extends BaseServiceImpl<BusinessTask, String> {
	private Logger logger = LoggerFactory.getLogger(BusinessTaskService.class);

	@Autowired
	private BusinessTaskRepository businessTaskRepository;
	@Autowired
	private FlowService flowService;
	@Autowired
	private BusinessIntanceService businessIntanceService;
	@Autowired
	private MyTicketService myTicketService;
	@Autowired
	private BusinessTaskLogService businessTaskLogService;
	@Autowired
	private TbConfService tbConfService;
	@Autowired
	private MyTicketDao myticketDao;
	@Autowired
	private MapperUtil mapper;
	@Autowired
	private BusinessTaskDao businessTaskDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AuthService authService;

	@Override
	public BusinessTaskRepository getRepository() {
		return businessTaskRepository;
	}
	
	@Transactional
	@Synchronized
	public Result<BusinessIntance> createTicket(WorkDataVO datas) {
		if(null == datas){
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"请求数据为空");
		}
		logger.info("createTicket start,datas:"+JSON.toJSONString(datas));
		MyTicket myTicket = myTicketService.getOne(datas.getProcessdefGuid());
		String deployId = myTicket.getDeployId();//deployId为deployKey
		String defineId = flowService.getDefineIdByDeploy(deployId);
		String userId = datas.getUserId();
		// 增加了告警事件id创建流程，事件id是否重复的判断 2022-06-30
		Result<BusinessIntance> result = businessIntanceService.createProcessInstance(userId, datas,myTicket);
		if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
			return  result;
		}
		BusinessIntance instance = result.getData();
		instance.setCreateUserName(datas.getUserName());
		instance.setProcessDefName(myTicket.getName());
		User currenUser = authService.getUserInfoByUserId(userId);  // 获取当前登陆用户信息
		ProcessInstance processInstance = flowService.startProcessById(defineId, userId, datas, instance ,currenUser);
		String processInstanceId = processInstance.getId();
		instance.setProcessInstanceId(processInstanceId);
		// 刷新instance的值 2022-09-08解决报错
		flowService.setVariables(processInstanceId,FlowConstant.INSTANCE, JSON.toJSONString(instance));
		BusinessInstanceStatEnum businessIntanceStated = getBusinessIntanceState(processInstanceId);
		instance.setStatEnum(businessIntanceStated);
		businessIntanceService.save(instance);
		businessTaskLogService.saveStartLog(myTicket.getGuid(),datas.getUserName(), userId, processInstance);
		return ResultUtil.success(instance);
	}

	/**
	 * 获得对应的流程实例状态
	 * @param processInstanceId
	 * @return
	 */
	private BusinessInstanceStatEnum getBusinessIntanceState(String processInstanceId) {
		Object processInstanceState = flowService.getVariableByExecutionId(processInstanceId, FlowConstant.PROCESSINSTANCESTATE);
		return (BusinessInstanceStatEnum)processInstanceState;
	}


	/**
	 * 新增流程业务变量
	 * @param processInstanceId
	 * @param params
	 */
	public void setVariables(String processInstanceId,Map<String,Object> params){
		flowService.setVariables(processInstanceId, params);
	} 
	
	/**
	 * 额外属性
	 * @param processInstanceId
	 * @param key
	 * @param params
	 */
	public void setVariables(String processInstanceId,String key,Object params){
		flowService.setVariables(processInstanceId, key, params);
	}

	/**
	 * 获得对应的处理意见
	 * advice处理：只取当行文本框、多行文本框中的内容 2022-12-26
	 *            单行文本框：type为input 、多行文本框：type为textarea
	 * @param params
	 * @return
	 */
	private String getAdvice(List<Map<String, Object>> params) {
		String advice = null;
		if(params!=null) {
			StringBuffer sb = new StringBuffer();
			for (Map<String, Object> paramsMap : params) {
				if(paramsMap.containsKey("label")&&paramsMap.get("label")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null&&paramsMap.containsKey("type")&&paramsMap.get("type")!=null) {
					String label = paramsMap.get("label").toString();
					Object value = paramsMap.get("value");
					String type = paramsMap.get("type").toString();
					if(StringUtils.isNotEmpty(type)&&(type.contains("input")||type.contains("textarea"))){
						String valueStr =getValue(value);
						sb.append(label).append(":").append(valueStr).append(";");
					}
				}
			}
			advice = sb.toString();
		}
		return advice;
	}

	private String getValue(Object value) {
		if(null == value){
			return "";
		}
		// 针对会签分权的数据处理
		if(value instanceof Map<?,?>) {
			Map<String,Object> cmap = (Map<String,Object>)value;
			return cmap.get("value")==null?"":String.valueOf(cmap.get("value"));
		}else{
            return String.valueOf(value);
		}
	}


	/**
	 * 提供给外部第三方接口
	 * @param businessVO
	 * @return
	 */
	@Transactional
	public boolean completeTaskByThirdBusiness(BusinessVO businessVO){
		String processInstanceId = businessVO.getProcessInstanceId();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("busiId",processInstanceId));
		List<BusinessTask> businessTasks = findAll(conditions);
		if(businessTasks.size()==0||businessTasks.size()>1){
			throw new RuntimeException("please check businessTasks count ! businessTaskscount:"+businessTasks.size());
		}
		BusinessTask businessTask = businessTasks.get(0);
		BusinessIntance businessIntance = businessTask.getInstance();
		String busiArgs = businessIntance.getBusiArgs();
		Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
		Map<String, Object> params = businessVO.getParams();
		map.putAll(params);
		busiArgs = JsonMapper.toJsonString(map);
		businessIntance.setBusiArgs(busiArgs);

		String taskId = businessTask.getTaskId();
		String userId = businessVO.getUserId();
		String action = businessVO.getAction();
		String advice  = "第三方系统建议："+action;
		flowService.completeTask(taskId, userId, action, advice);

		updateBusinessInstanceState(businessIntance);
		businessIntanceService.save(businessIntance);
        return true;
	}

	/**
	 * 更新对应的状态；
	 * @param businessIntance
	 */
	private void updateBusinessInstanceState(BusinessIntance businessIntance) {
		String instanceId = businessIntance.getProcessInstanceId();
		BusinessIntance intance = businessIntanceService.getByInstanceId(instanceId);
		if(!intance.getStatEnum().toBoolean()){
			BusinessInstanceStatEnum businessIntanceState = getBusinessIntanceState(instanceId);
			businessIntance.setStatEnum(businessIntanceState);
		}
	}

	/**
	 * 完成任务
	 * @param deals
	 * @param userId
	 */
	@Synchronized  // 同步
	@Transactional
	public void completeTask(DealVO deals, String userId) {
		BusinessTask task = getOne(deals.getTaskId()); 
		BusinessTaskType taskType = task.getTaskType();
		BusinessIntance instance = businessIntanceService.getOne(task.getInstance().getGuid());
		String busiArgs = instance.getBusiArgs();
		Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
		List<Map<String, Object>> params = deals.getParams();
		String advice = deals.getAdvice();
		if(StringUtils.isEmpty(advice)) {
			advice = getAdvice(params);
		}
		logger.info(instance.getProcessDefName()+"流程，对应的id"+instance.getGuid()+"执行审批操作");
        logger.info(task.getTaskDefindName()+"节点执行审批操作，task_id:"+task.getTaskId());
		String dealPeoples = instance.getDealPeoples();
		instance.setDealPeoples(dealPeoples + BusinessIntanceService.assembleDealPeoples(userId));
		updateBusinessInstanceState(instance);
		setAssginContextInfo(instance, params);  //会签分权
		constructBusiArgInfo(task, instance, map, params);//构造busiArgs
		busiArgs = JsonMapper.toJsonString(map);
		instance.setBusiArgs(busiArgs);
		businessIntanceService.save(instance);
		User cuurentUser = authService.getUserInfoByUserId(userId);  // 获取当前处理人用户信息
		String userJson = cuurentUser == null?null : JSON.toJSONString(cuurentUser);
		flowService.setVariables(instance.getProcessInstanceId(), FlowConstant.CURRENTUSER,userJson);
		flowService.setVariables(instance.getProcessInstanceId(),FlowConstant.INSTANCE, JSON.toJSONString(instance));
		switch (taskType) {
			case node:
				flowService.completeTask(task.getTaskId(), userId, deals.getAction(), advice);
				break;
			case undo:
				flowService.completeCallback(deals, userId, task);
				break;
			default:
				break;
		}
	}


	/**
	 * 设置会签完成条件
	 * @param task
	 */
	public void setAsignCompleteCondition(BusinessTask task,String action) {
		String taskId = task.getTaskId();
		String busiId = task.getBusiId();
	    Integer passCount = 0;//审批同意人数
	    Integer noPassCount = 0;//审批不同意人数
	    Integer totalCount = 0;//任务总人数
	    List<Task> tasks = flowService.queryTasksByProcessInstanceIdAndTaskId(taskId, busiId);
		Task ctask = flowService.querySingleTasksByTaskId(taskId);
		String tmpNoPassCount = flowService.getVariableByExecutionId(busiId, ctask.getTaskDefinitionKey()+"#"+FlowConstant.NO_PASS_COUNT)+"";
		String tmpPassCount = flowService.getVariableByExecutionId(busiId, ctask.getTaskDefinitionKey()+"#"+FlowConstant.PASS_COUNT)+"";
		String tmpTotalCount = flowService.getVariableByExecutionId(busiId, ctask.getTaskDefinitionKey()+"#"+FlowConstant.TOTAL_COUNT)+"";
		
		if(StringUtils.isNotEmpty(tmpNoPassCount) &&!tmpNoPassCount.equals("null")) {
			noPassCount = Integer.valueOf(tmpNoPassCount);
		}
		
		if(StringUtils.isNotEmpty(tmpPassCount) &&!tmpPassCount.equals("null")) {
			passCount = Integer.valueOf(tmpPassCount);
		}
		
		if(tmpTotalCount.equals("null") || tmpTotalCount.trim().equals("")) {
			totalCount = tasks.size();
		}else if(StringUtils.isNotEmpty(tmpTotalCount) &&!tmpTotalCount.equals("null")) {
			totalCount = Integer.parseInt(tmpTotalCount);
		}
		
		for (Task curTask : tasks) {
			if(action.equals(FlowConstant.PASS) && curTask.getId().equals(taskId)){//选择通过则通过人数+1
	               passCount++;
	        }
			if(action.equals(FlowConstant.NO_PASS) && curTask.getId().equals(taskId)){//选择不通过则通过人数+1
				  noPassCount++;
	        }
		}
		Map<String,Object> map = new HashMap<>();
		map.put(ctask.getTaskDefinitionKey()+"#"+FlowConstant.NO_PASS_COUNT, noPassCount);
		map.put(ctask.getTaskDefinitionKey()+"#"+FlowConstant.PASS_COUNT, passCount);
		map.put(ctask.getTaskDefinitionKey()+"#"+FlowConstant.TOTAL_COUNT, totalCount);
		map.put(FlowConstant.NO_PASS_COUNT, noPassCount);
		map.put(FlowConstant.PASS_COUNT, passCount);
		map.put(FlowConstant.TOTAL_COUNT, totalCount);
		map.put(FlowConstant.NO_PASS_COUNT, noPassCount);
		map.put(FlowConstant.PASS_COUNT, passCount);
		map.put(FlowConstant.TOTAL_COUNT, totalCount);
		flowService.setVariableLocal(taskId, map);
		Object variable1 = flowService.getVariable(taskId, "nrOfInstances");
		Object variable2 = flowService.getVariable(taskId, "nrOfCompletedInstances");
		Object variable3= flowService.getVariable(taskId, "nrOfActiviteInstances");
		logger.info("variable1:"+variable1+",variable2:"+variable2+",variable3:"+variable3);
		
	}
	
	
	private void setAssginContextInfo(BusinessIntance instance, List<Map<String, Object>> params) {
		if(null == params ){
			return;
		}
		for (Map<String, Object> map : params) {
			if(map.containsKey("key")&&map.get("key")!=null&&map.containsKey("value")&&map.get("value")!=null){
				String key = map.get("key").toString();
				if(key.equals("contextId")) {
					String contextId = map.get("value").toString();
					instance.setContextId(instance.getContextId()+contextId+"|,|");
				}
				if(key.equals("contextKey")) {
					String contextKey = map.get("value").toString();
					instance.setContextKey(instance.getContextKey()+contextKey+"|,|");
				}
				
			}
		}
	}

	/**
	 * 获得对应的ContextId;
	 * @param params
	 * @return
	 */
	private String getContextId(List<Map<String, Object>> params){
		String contextId = null;
		for (Map<String, Object> paramsMap : params) {
			if(paramsMap.containsKey("key")&&paramsMap.get("key")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null) {
				String key = paramsMap.get("key").toString();
				if(key.equals("contextId")) {
					contextId = paramsMap.get("value").toString();
					break;
				}
			}
		}
		return contextId;
	}
	private String getContextLabel(List<Map<String, Object>> params) {
		String lable = null;
		for (Map<String, Object> paramsMap : params) {
			if(paramsMap.containsKey("key")&&paramsMap.get("key")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null) {
				String key = paramsMap.get("key").toString();
				if(key.equals("contextLabel")) {
					lable = paramsMap.get("value").toString();
					break;
				}
			}
		}
		return lable;
	}
	
	/**
	 * 构造BusiArg信息
	 * @param task
	 * @param instance
	 * @param map
	 * @param params
	 *
	 * 工单1.0表单
	 */
	private void constructBusiArgInfoOld(BusinessTask task, BusinessIntance instance, Map map,List<Map<String, Object>> params) {
		String json1 = JsonMapper.toJsonString(map);
		logger.info(json1);
		if(params!=null){
			for (Map<String, Object> paramsMap : params) {
				if(paramsMap.containsKey("key")&&paramsMap.get("key")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null){
					Map<String,Object> hashMap = new HashMap<>();
					String key = paramsMap.get("key").toString();
					String processDefGuid = instance.getProcessDefGuid();
					MyTicket myTicket = myTicketService.getOne(processDefGuid);
					if(myTicket!=null) {    //查找对应的表单模板
						MyTicketForminfo myTicketForminfo = myTicket.getForminfo();
						String formType = myTicketForminfo.getFormType();
						if(formType.equals("template")) {
							String formData = myTicketForminfo.getFormData();
							FormInfos formInfos = JsonMapper.fromJsonString(formData, FormInfos.class);
							List<FormVO> list = formInfos.getFormInfos();
							for (FormVO formVO : list) {
								Map<String, Object> context = formVO.getContext();
								String fkey = context.get("code").toString();
								if(key.equals(fkey)){   //表单模板与传参模板是否匹配
									if(context.containsKey("isSign")&&context.get("isSign")!=null){ //判断是否是会签组件
										Boolean isSign = (Boolean)context.get("isSign");
										if(isSign) { //确认是会签结点
											if(map.containsKey(key)&&map.get(key)!=null) {
												Object object = map.get(key);
												if(object instanceof List<?>) { //累加
													List<Map<String,Object>> objList = (List<Map<String,Object>>)object;
													recoverLastParam(params, objList); //删除原来的数据
													Object object2 = paramsMap.get("value");
													if(object2 instanceof Map<?,?>) {
														Map<String,Object> cmap = (Map<String,Object>)object2;
														objList.add(cmap);
														hashMap.put(key, objList);
													}
												}else {
													throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "会签组件不是数组类型请检查！");
												}
											}else {  //这是第一次的时候
												List<Map<String,Object>> objList = new ArrayList<>();
												Object object2 = paramsMap.get("value");
												if(object2  instanceof Map<?,?>) {
													Map<String,Object> cMap = (Map<String,Object>)object2;
													objList.add(cMap);
													hashMap.put(key, objList);
												}
											}
										}else {
											hashMap.put(key, paramsMap.get("value"));
										}
									}else {
										hashMap.put(key, paramsMap.get("value"));
									}
								}
							}
						}else {
							hashMap.put(key, paramsMap.get("value"));
						}
					}
					map.putAll(hashMap);
				}
			}
		}
	}


	/**
	 * 构造BusiArg信息
	 * @param task
	 * @param instance
	 * @param map
	 * @param params
	 *  工单2.0表单
	 *   1. 对于会签分权处理时，表单类型变为了custom
	 *   2. 表单组件机构发生变化
	 *   3. 结构进行重构
	 */
	private void constructBusiArgInfo(BusinessTask task, BusinessIntance instance, Map map,List<Map<String, Object>> params) {
	    if(null == params){
			return;
		}
		String processDefGuid = instance.getProcessDefGuid();
		MyTicket myTicket = myTicketService.getOne(processDefGuid);
		if(null == myTicket){
			return;
		}
		for (Map<String, Object> paramsMap : params) {
			 paramsHandle(paramsMap,myTicket,map,params);
		}
	}

	/**
	 * 构造BusiArg数据：会签分权单独处理、其他直接新增处理
	 * 是会签分权: 审批传的参数数据与历史数据进行组合，然后放在BusiArg中
	 * 非会签分权：审批传的参数数据直接放入BusiArg中
	 *  2022-12-27
	 * @param paramsMap
	 * @param myTicket
	 * @param map
	 * @param params
	 */

	private void paramsHandle(Map<String, Object> paramsMap, MyTicket myTicket, Map map,List<Map<String, Object>> params) {
		if(paramsMap.containsKey("key")&&paramsMap.get("key")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null){
			String key = paramsMap.get("key").toString();  // 参数的key
			MyTicketForminfo myTicketForminfo = myTicket.getForminfo();
			String formData = myTicketForminfo.getFormData();
			FormInfoVO formInfos = JsonMapper.fromJsonString(formData, FormInfoVO.class);
			List<UsedFieldVO> list = formInfos.getFormInfos().getUsedFields();
			// 判断是不是会签分权，是的话单独处理
			if(isSign(list,key)){
				signDataHandle(key,paramsMap,map,params);
			}else{ // 不是会签分权，参数直接放入BusiArg
				map.put(key,paramsMap.get("value"));
			}
		}
	}

	// 会签分权数据处理
	private void signDataHandle(String key, Map<String, Object> paramsMap, Map map,List<Map<String, Object>> params) {
		if(map.containsKey(key)&&map.get(key)!=null&&!map.get(key).equals("")) {
			Object object = map.get(key);
			if(object instanceof List<?>) { //累加
				List<Map<String,Object>> objList = (List<Map<String,Object>>)object;
				recoverLastParam(params, objList); //删除原来的数据
				Object object2 = paramsMap.get("value");
				if(object2 instanceof Map<?,?>) {
					Map<String,Object> cmap = (Map<String,Object>)object2;
					objList.add(cmap);
					map.put(key, objList);
				}
			}else {
				throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "会签组件不是数组类型请检查！");
			}
		}else {  //这是第一次的时候
			List<Map<String,Object>> objList = new ArrayList<>();
			Object object2 = paramsMap.get("value");
			if(object2  instanceof Map<?,?>) {
				Map<String,Object> cMap = (Map<String,Object>)object2;
				objList.add(cMap);
				map.put(key, objList);
			}
		}
	}

    // 判断组件是不是会签分权
	private boolean isSign(List<UsedFieldVO> list, String key) {
		for (UsedFieldVO formVO : list){
			Map<String, Object> context = formVO.getOption();
			String fkey = context.get("fieldId").toString();
			if(key.equals(fkey)){
				if(null !=context.get("isSign")&&(Boolean)context.get("isSign")){
					return true;
				}
			}
		}
		return false;
	}



	private void recoverLastParam(List<Map<String, Object>> params, List<Map<String, Object>> objList) {
		String newContextId = getContextId(params);  //传参的新contextId
		Iterator<Map<String, Object>> iterator = objList.iterator();
		while(iterator.hasNext()) {
			Map<String, Object> map2 = iterator.next();
			if(map2.containsKey("contextId")&&map2.get("contextId")!=null) {
				String contextId = map2.get("contextId").toString();
				if(contextId.equals(newContextId)){
					iterator.remove();
				}
			}
		}
	}




	public BusinessIntance completeTaskByBusinessInstance(DealVO deals, String userId) {
		BusinessTask task = getOne(deals.getTaskId()); 
		BusinessTaskType taskType = task.getTaskType();
		BusinessIntance instance = task.getInstance();
		String guid = instance.getGuid();
		instance.setDealPeoples(instance.getDealPeoples() + BusinessIntanceService.assembleDealPeoples(userId));
		String busiArgs = instance.getBusiArgs();
		Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
		List<Map<String, Object>> params = deals.getParams();
		if(params!=null){
			for (Map<String, Object> paramsMap : params) {
				if(paramsMap.containsKey("key")&&paramsMap.get("key")!=null&&paramsMap.containsKey("value")&&paramsMap.get("value")!=null){
					Map<String,Object> hashMap = new HashMap<>();
					String key = paramsMap.get("key").toString();
					hashMap.put(key, paramsMap.get("value"));
					map.putAll(hashMap);														
				}
			}
		}
		busiArgs = JsonMapper.toJsonString(map);
		instance.setBusiArgs(busiArgs);
		businessIntanceService.save(instance);
		switch (taskType) {
			case node:
			flowService.completeTask(task.getTaskId(), userId, deals.getAction(), deals.getAdvice());
			break;
			case undo:
			flowService.completeCallback(deals, userId, task);
			break;
		default:
			break;
		}
		BusinessIntance businessIntance = businessIntanceService.getOne(guid);
		return businessIntance;
	}
	
	
	public List<BusinessTask> getByInstanceIdAndTaskcode(String instanceId, String code) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("busiId", instanceId));
		conditions.add(QueryCondition.eq("taskCode", code));
		List<BusinessTask> findAll = findAll(conditions);
		
		return findAll;
	}

	/**
	 * 找到待办工单的树
	 * 流程分类下：增加内部工单、外部工单菜单节点
	 * 2022-11-2
	 * @param userId
	 * @return
	 */
	public List<MyTicketTreeVO> findUserTasksTree(String userId) {
		List<MyTicketTreeVO> result = new ArrayList<>();
		MyTicketTreeVO root = new MyTicketTreeVO();
		root.setKey("0");
		root.setChildren(new ArrayList<>());
		root.setTitle("流程分类");
		result.add(root);
		int ticketTypeOne = 0; // 内部工单数量
		int ticketTypeTwo = 0; // 外部工单数量
		List<MyTicketTreeVO> ticketTypes= new ArrayList<>();
		List<MyTicketTreeVO> ticketTypeOnes= new ArrayList<>();
		List<MyTicketTreeVO> ticketTypeTwos= new ArrayList<>();
		List<Map<String,Object>> list = myticketDao.queryMyTaskTicket(userId);
		for (Map<String, Object> map : list) {
			if(map.containsKey("process_def_name")&&map.get("process_def_name")!=null){
				Integer count = Integer.valueOf(map.get("count").toString());
				String processDefName = map.get("process_def_name").toString();
				String ticketType = myticketDao.getTicktType(processDefName); // 1，表示内部工单 ，2表示外部工单
				MyTicketTreeVO myTicketTree = new MyTicketTreeVO();
				myTicketTree.setKey(processDefName);
				myTicketTree.setCount(count);
				if(MyTicketConstant.TICKETTYPEONE.equals(ticketType)){
					ticketTypeOne = ticketTypeOne+ count ;
					ticketTypeOnes.add(myTicketTree);
				}else if (MyTicketConstant.TICKETTYPETWO.equals(ticketType)){
					ticketTypeTwo = ticketTypeTwo+count;
					ticketTypeTwos.add(myTicketTree);
				}
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


	/**
	 * 流程分类下：增加内部工单、外部工单菜单节点
	 * 2022-11-2
	 * @param userId
	 * @param pageable
	 * @return
	 */
	public Page<BusinessTask> findUserTasks(String userId, BusinessTask example, Pageable pageable) {
		// 内外部工单节点处理 2022-11-1
		BusinessIntance instance = example == null?null :example.getInstance();
		String nodeName = instance == null?" " :instance.getProcessDefName();
		boolean isTicketTypeNode = MyTicketConstant.isNodeTrue(nodeName);
		if(!isTicketTypeNode){
			// 不是内外部节点按照以前的逻辑
			return findUserTasksOld(userId,example,pageable);
		}else{
			// 是内外部节点处理
			return findUserTasksNew(userId,example,pageable,nodeName);
		}



	}
	private Page<BusinessTask> findUserTasksOld(String userId, BusinessTask example, Pageable pageable) {
		ExampleMatcher match = ExampleMatcher.matching().withStringMatcher(StringMatcher.CONTAINING);
		Example<BusinessTask> of = Example.of(example, match);
		Specification<BusinessTask> specification = new Specification<BusinessTask>() {

			@Override
			public Predicate toPredicate(Root<BusinessTask> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Subquery<String> subquery = query.subquery(String.class);
				Root<BusinessTaskCandidate> subRoot = subquery.from(BusinessTaskCandidate.class);
				Path<String> path = subRoot.get("busiTaskId");
				Subquery<String> where = subquery.select(path).where(cb.equal(subRoot.get("candidate"), userId),cb.notEqual(subRoot.get("assignType"),0));
				Predicate predicate = QueryByExamplePredicateBuilder.getPredicate(root, cb, of);
				Predicate and = cb.and(predicate, cb.in(root.get("id")).value(where));
				return and;
			}

		};
		return businessTaskRepository.findAll(specification, pageable);
	}
	private Page<BusinessTask> findUserTasksNew(String userId, BusinessTask example, Pageable pageable, String nodeName) {
		String ticketType =  MyTicketConstant.queryCodeByName(nodeName);
		List<String> taskIds = getTaskIds(ticketType);
		if (CollectionUtils.isEmpty(taskIds)) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}
		example.getInstance().setProcessDefName(null);  // 设置为null
		ExampleMatcher match = ExampleMatcher.matching().withStringMatcher(StringMatcher.CONTAINING);
		Example<BusinessTask> of = Example.of(example, match);
		Specification<BusinessTask> specification = new Specification<BusinessTask>() {
			@Override
			public Predicate toPredicate(Root<BusinessTask> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Subquery<String> subquery = query.subquery(String.class);
				Root<BusinessTaskCandidate> subRoot = subquery.from(BusinessTaskCandidate.class);
				Path<String> path = subRoot.get("busiTaskId");
				CriteriaBuilder.In<Object> in = cb.in(subRoot.get("busiTaskId")); //
				for (String guid : taskIds) {
					in.value(guid);
				}
				Subquery<String> where = subquery.select(path).where(cb.equal(subRoot.get("candidate"), userId),
						cb.notEqual(subRoot.get("assignType"), 0), in);
				Predicate predicate = QueryByExamplePredicateBuilder.getPredicate(root, cb, of);
				Predicate and = cb.and(predicate, cb.in(root.get("id")).value(where));
				return and;
			}
		};
		return businessTaskRepository.findAll(specification, pageable);
	}
	public List<String> getTaskIds(String ticketType) {
		String sql = "select task.id from business_task as task " +
				"  inner join business_intance as instance on instance.guid=task.instance_guid " +
				"  inner join my_ticket as ticket on ticket.guid=instance.process_def_guid where ticket.ticket_type='"+ticketType+"'";
		List<String> list = jdbcTemplate.queryForList(sql,String.class);
		return list;
	}
     
	public static void main(String[] args) {
		String content = "[{\"key\":\"input_160983640093527760\",\"context\":{\"type\":\"Input\",\"field\":\"标\",\"code\":\"input_code_160983640093527760\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":false,\"verify\":\"\",\"multiple\":false,\"cascadeNameU\":\"\",\"cascadeCode\":\"\",\"cascadeName\":\"\",\"roleCode\":\"\",\"defaultValue\":\"\",\"hasDefault\":false,\"isSign\":false,\"contextKey\":\"\"}},{\"key\":\"textarea_160983642342129050\",\"context\":{\"type\":\"Textarea\",\"field\":\"说明\",\"code\":\"textarea_code_160983642342129050\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":false,\"verify\":\"\",\"multiple\":false,\"cascadeNameU\":\"\",\"cascadeCode\":\"\",\"cascadeName\":\"\",\"roleCode\":\"\",\"defaultValue\":\"\",\"hasDefault\":false}},{\"key\":\"domain_160983643738172070\",\"context\":{\"type\":\"domain\",\"field\":\"地市\",\"code\":\"domainCode\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":true,\"verify\":\"\",\"multiple\":true,\"cascadeNameU\":\"\",\"cascadeCode\":\"\",\"cascadeName\":\"\",\"roleCode\":\"\",\"defaultValue\":\"\",\"hasDefault\":false}},{\"key\":\"upload_160983649492779386\",\"context\":{\"type\":\"Upload\",\"field\":\"文件上传\",\"code\":\"upload_code_160983649492779386\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":false,\"verify\":\"\",\"multiple\":false,\"defaultValue\":\"\",\"roleCode\":\"\",\"isSign\":false,\"contextKey\":\"province\"}},{\"key\":\"textarea_160983651204883177\",\"context\":{\"type\":\"Textarea\",\"field\":\"备注\",\"code\":\"textarea_code_160983651204883177\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":false,\"verify\":\"\",\"multiple\":false,\"cascadeNameU\":\"\",\"cascadeCode\":\"\",\"cascadeName\":\"\",\"roleCode\":\"\",\"defaultValue\":\"\",\"hasDefault\":false}},{\"key\":\"textarea_160991367215399394\",\"context\":{\"type\":\"Textarea\",\"field\":\"地市建议\",\"code\":\"advice\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":false,\"verify\":\"\",\"multiple\":false,\"cascadeNameU\":\"\",\"cascadeCode\":\"\",\"cascadeName\":\"\",\"roleCode\":\"\",\"defaultValue\":\"\",\"hasDefault\":false,\"isSign\":true,\"contextKey\":\"city\"}},{\"key\":\"upload_161104483573699568\",\"context\":{\"type\":\"Upload\",\"field\":\"地市文件上传\",\"code\":\"upload_code_161104483573699568\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":false,\"verify\":\"\",\"multiple\":false,\"defaultValue\":\"\",\"roleCode\":\"\",\"isSign\":true,\"contextKey\":\"city\"}},{\"key\":\"textarea_161104495584157006\",\"context\":{\"type\":\"Textarea\",\"field\":\"省厅审核\",\"code\":\"textarea_code_161104495584157006\",\"option\":[\"\"],\"columns\":1,\"children\":{},\"require\":false,\"verify\":\"\",\"multiple\":false,\"cascadeNameU\":\"\",\"cascadeCode\":\"\",\"cascadeName\":\"\",\"roleCode\":\"\",\"defaultValue\":\"\",\"hasDefault\":false,\"isSign\":true,\"contextKey\":\"city\"}}]"; 
	}
	
	
	/**
	 * 对Busiarg数据进行筛选
	 * @param content
	 */
	public void selectBusiArgTask(List<BusinessTask> content) {
		for (BusinessTask businessTask : content) {
			String contextId = businessTask.getContextId();
			if(StringUtils.isNotEmpty(contextId)) {
				BusinessIntance businessIntance = businessTask.getInstance();
//				String busiArgs2 = businessIntance.getBusiArgs();
				String guid = businessIntance.getGuid();
				BusinessIntance intance = businessIntanceService.getOne(guid);
				BusinessIntance newBusinessInstance = new BusinessIntance();
				mapper.copy(intance, newBusinessInstance);
				String busiArgs = newBusinessInstance.getBusiArgs();
				//logger.info("guid:"+guid+","+"busiArgs:"+busiArgs);
				Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
				logger.info("查询成功！");
				String processDefGuid = businessIntance.getProcessDefGuid();
				MyTicket myTicket = myTicketService.getOne(processDefGuid);
				if(myTicket!=null) {
					MyTicketForminfo myTicketForminfo = myTicket.getForminfo();
					String formData = myTicketForminfo.getFormData();
					logger.info("formData数据:"+formData);
					FormInfoVO formInfos = JsonMapper.fromJsonString(formData, FormInfoVO.class);
					logger.info("转换结束");
					if(formInfos!=null) {
						List<UsedFieldVO> list = formInfos.getFormInfos().getUsedFields();
						for (UsedFieldVO formVO : list) {
							Map<String, Object> context = formVO.getOption();
							if(context.containsKey("isSign")&&context.get("isSign")!=null) {
								Boolean isSign = (Boolean)context.get("isSign");
								if(isSign) { //确认是会签结点
									String fkey = context.get("fieldId").toString();
									List<Map<String,Object>> selectMaps = new ArrayList<>();
									if(map.containsKey(fkey)&&map.get(fkey)!=null) {
										Object object = map.get(fkey);
										if(object instanceof List<?>) { //累加
											List<Map<String,Object>> objList = (List<Map<String,Object>>)object;
											for (Map<String, Object> map2 : objList) {
												if(map2.containsKey("contextId")&&map2.get("contextId")!=null) {
													String curContextId = map2.get("contextId").toString();
													if(curContextId.contains(contextId) || contextId.contains(curContextId)) {
														selectMaps.add(map2);
													}
												}
											}
											map.put(fkey, selectMaps);
										}else {
											logger.error("会签组件不是数组类型,请检查！");
											//throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "会签组件不是数组类型请检查！");
										}
									}
								}						
							}
							
						}
					}
				}
				newBusinessInstance.setBusiArgs(JsonMapper.toJsonString(map));
				businessTask.setInstance(newBusinessInstance);
			}
			
		}
	}

	public boolean existsUndoTask(String processInstanceId, String parentId, String signal) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("busiId", processInstanceId));
		conditions.add(QueryCondition.eq("executionId", parentId));
		conditions.add(QueryCondition.eq("relatedInfos", signal));
		
		return exists(conditions);
	}

	public List<BusinessTask> getByInstance(String instanceGuid) {
		BusinessTask example = new BusinessTask();
		BusinessIntance instance = new BusinessIntance();
		instance.setGuid(instanceGuid);
		example.setInstance(instance);
		return findAll(example);
	}

	/**
	 * 判断该任务是否逾期
	 */
	public Boolean isOverdue(BusinessTask businessTask){
		Boolean bool=false;
		Date deadDate=businessTask.getDeadDate();
		if(deadDate==null){
			BusinessIntance businessIntance=businessIntanceService.getByInstanceId(businessTask.getBusiId());
			if(businessIntance!=null){
				deadDate=businessIntance.getDeadlineDate();
			}
		}
		if(deadDate!=null){
			TbConf tbConf =tbConfService.getOne("overdue_time");
			Integer overdueTime=Integer.valueOf(tbConf.getValue());
			logger.info("overdueTime :"+overdueTime);
			Date now=new Date();
			Date finalDate=DateUtil.addDay(now,overdueTime);
			String finalDateStr=DateUtil.format(now,"yyyy-mm-dd");
			String deadDateStr=DateUtil.format(finalDate,"yyyy-mm-dd");
			if(finalDateStr.compareTo(deadDateStr)<=0){
				bool=true;
			}
		}
		return  bool;
	}
	
	
	/**
	 * 设置会签完成条件
	 * @param delegateTask
	 */
	public void setAssignCompleteCondition(DelegateTask delegateTask) {
		flowService.setFlowLocalVari(delegateTask, FlowConstant.NO_PASS_COUNT, "0");
		flowService.setFlowLocalVari(delegateTask, FlowConstant.PASS_COUNT, "0");
		flowService.setFlowLocalVari(delegateTask, FlowConstant.TOTAL_COUNT, "0");
	}
	
	
	/**
	 * 设置子流程/会签流程信息(contextId)
	 * @param delegateTask
	 * @param bTask
	 */
	public void setSubFlowVariByContextId(DelegateTask delegateTask, BusinessTask bTask,String signerKeyValue) {
		String signer = flowService.getFlowVaribleStr(signerKeyValue, delegateTask);
		String contextKeyId = flowService.getFlowVaribleStr(FlowConstant.CONTEXTID, delegateTask);
		if(StringUtils.isNotEmpty(contextKeyId)) {
			contextKeyId = contextKeyId+"/"+signer;
		}else {
			contextKeyId = signer;
		}
		bTask.setContextId(contextKeyId);
		delegateTask.setVariableLocal("contextId", contextKeyId);
	}
	
	/**
	 * 设置子流程当中的ContextKey值
	 * @param delegateTask
	 * @param fixValue
	 * @param bTask
	 */
	public void setSubFlowVariByContextKey(DelegateTask delegateTask,FixedValue fixValue, BusinessTask bTask) {
		String contextKey = flowService.getFlowVaribleStr(FlowConstant.CONTEXTKEY, delegateTask);
		String localcontextKey = flowService.getContextKey(fixValue,delegateTask);
		if(StringUtils.isNotEmpty(contextKey)&&StringUtils.isNotEmpty(localcontextKey)) {
			contextKey = contextKey+"/"+localcontextKey;
		}
		if(StringUtils.isEmpty(contextKey) && StringUtils.isNotEmpty(localcontextKey)) {
			contextKey = localcontextKey;
		}
		bTask.setContextKey(contextKey);
		//delegateTask.setVariableLocal("contextKey", contextKey);
	}


	/**
	 * 根据流程id获取当前任务id
	 * @param instanceId
	 * @return
	 */
	public Result<String> getTaskIdByInstanceId(String instanceId) {
		Result<String> result = new Result<String>();
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		String taskId = businessTaskDao.getCurrentTaskByUserIdAndBusinessId(instanceId);
		result.setData(taskId);
		result.setMsg("success");
		return result;
	}

	/**
	 * 发消息给队列：目前只给事件协办、事件预警发，其他不发
	 * 2022-10-13
	 * @param instance
	 * @param status
	 */
	public void sendFlowMsg(BusinessIntance instance,String status,User cuurentUser) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(null == instance){
						return;
					}
					String ticketName = instance.getProcessDefName();
					if("事件协办".equals(ticketName)||"事件预警".equals(ticketName)){
						String busiArgs = instance.getBusiArgs();
						if(StringUtils.isEmpty(busiArgs)){
							return;
						}
						Map map = JsonMapper.fromJsonString(busiArgs, Map.class);
						Object senMsg = map.get("sendMsg");

						if(FlowMessageVO.CREATE.equals(status)&&!isSendMsg(senMsg)){
							return;
						}
						FlowMessageVO message = new FlowMessageVO();
						message.setBusiArgs(busiArgs);
						message.setStatus(status);
						message.setTicketName(instance.getProcessDefName());
						message.setInstanceId(instance.getGuid());
						message.setUser(cuurentUser);
						logger.warn(instance.getProcessDefName()+"流程结束，发消息给队列,消息数据："+JSON.toJSONString(message));
						FlowQueUtil.flowMessagePut(message);
					}
				}catch (Exception e){
					logger.error("发消息给队列失败", e);
				}
			}
		}).start();

	}

	// senMsg为fasle不发队列消息
	private boolean isSendMsg(Object senMsg) {
		if(null == senMsg){
			return true;
		}
		String sendMsgStr = String.valueOf(senMsg);
		if("false".equals(sendMsgStr)){
			return false;
		}
		return true;
	}
}

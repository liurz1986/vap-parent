package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.*;
import com.vrv.vap.alarmdeal.business.flow.define.controller.DeploymentResponse;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.ProcessStateEnum;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.log.LoggerUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Service
public class FlowService {
	private static LoggerUtil logger = LoggerUtil.getLogger(FlowService.class);
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private FormService formService;
	@Autowired
	private AuthService authService;
	@Autowired
	private MyTicketService myTicketService;
	@Autowired
	private MapperUtil mapper;
	@Autowired
	private BusinessTaskService businessTaskService;
	
	public String getStartFormKey(String deploymentId){
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
		String startFormKey = formService.getStartFormKey(processDefinition.getId());
		return startFormKey;
	}
	
	/**
	 * 根据任务Id获得对应的自定义表单内容
	 * @param taskId
	 * @return
	 */
	public String getTaskFormKey(String taskId){
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String formKey = task.getFormKey();
		return formKey;
	}
	
	
	@Transactional
	public ProcessInstance startProcessById(String processdefId, String userId, WorkDataVO data, BusinessIntance instance, User currenUser) {
		Map<String, Object> args = new HashMap<>();
		args.put(FlowConstant.BUSI_ARG, data.getForms());
		args.put(FlowConstant.USERID, userId);
		args.put(FlowConstant.ADVICE, "");
		args.put(FlowConstant.ACTION, "开启");
		args.put(FlowConstant.INSTANCE, JSON.toJSONString(instance));
		if(null != currenUser){
			args.put(FlowConstant.CURRENTUSER, JSON.toJSONString(currenUser)); // 当前用户
		}
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processdefId, args);
		return processInstance;
	}
	
	@Transactional
	public ProcessInstance startProcessByKey(String processKey, String userId, WorkDataVO data, BusinessIntance instance) {
		Map<String, Object> args = new HashMap<>();
		args.put(FlowConstant.BUSI_ARG, data.getForms());
		args.put(FlowConstant.USERID, userId);
		args.put(FlowConstant.ADVICE, "");
		args.put(FlowConstant.ACTION, "开启");
		args.put(FlowConstant.INSTANCE, instance);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, args);
		return processInstance;
	}

	/**
	 * 设置业务流程变量
	 * @param map
	 */
	public void setVariables(String processInstanceId,Map<String,Object> params){
		runtimeService.setVariable(processInstanceId, FlowConstant.BUSI_ARG, params);
	}
	
	/**
	 * 设置对应key业务流程变量
	 * @param map
	 */
	public void setVariables(String processInstanceId,String key,Object params){
		runtimeService.setVariable(processInstanceId, key, params);
	}
	/**
	 * 设置流程变量
	 * @param map
	 */
	public void setVariablesNew(String processInstanceId,Map<String,Object> params){
		runtimeService.setVariables(processInstanceId, params);
	}
	
	/**
	 * 标准完成任务
	 * @param taskId 任务id
	 * @param userId 操作用户id
	 * @param action 执行动作
	 * @param advice 建议
	 */
	public void completeTask(String taskId, String userId, String action, String advice) {
		Map<String,Object> map = new HashMap<>();
		map.put(FlowConstant.USERID, userId);
		map.put(FlowConstant.ACTION, action);
		map.put(FlowConstant.ADVICE, advice);
		taskService.setVariables(taskId, map);
		taskService.complete(taskId);
	}
	
	
	
	public void completeCallback(DealVO deals, String userId, BusinessTask task) {
		List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(task.getBusiId()).signalEventSubscriptionName(task.getRelatedInfos()).list();
		Map<String,Object> map = new HashMap<>();
		map.put(FlowConstant.USERID, userId);
		map.put(FlowConstant.ACTION, deals.getAction());
		map.put(FlowConstant.ADVICE, deals.getAdvice());
		for (Execution execution : list) {
			runtimeService.signalEventReceived(task.getRelatedInfos(),execution.getId(), map);
		}
	}
	
	/**
	 * 查询流程实例执行流
	 * @param processInstanceId
	 * @return
	 */
	public List<Execution> getRunningTimeExecution(String processInstanceId){
		List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
		return list;
	}
	
	/**
	 * 设置流程实例执行流变量
	 * @param executeId
	 * @param map
	 */
	public void setRunningTimeVaribles(String executeId, Map<String,Object> map){
		runtimeService.setVariables(executeId, map);
	}

	public List<Task> queryAllTasks() {
		List<Task> list = taskService.createTaskQuery().list();
		
		return list;
	}

	
	public List<Task> queryTasksByProcessInstanceId(String processInstanceId){
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		return list;
	}
	
	public List<Task> queryTasksByProcessInstanceIdAndTaskId(String taskId,String processInstanceId){
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        List<Task> list = taskService.createTaskQuery().taskName(task.getName()).processInstanceId(processInstanceId).list();
		return list;
	}
	
	
	public Task querySingleTasksByTaskId(String taskId){
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return task;
	}
	
	
	public List<Task> queryAllTasksByPage() {
		List<Task> listPage = taskService.createTaskQuery().listPage(1, 2);
		
		return listPage;
	}
	
	/**
	 * 更新流程中的业务数据参数
	 * @param obj
	 */
	public void setBusiArgs(String taskId, Object obj) {
		taskService.setVariable(taskId, FlowConstant.BUSI_ARG, obj);
	}
	
	/**
	 * 获取流程中的业务参数
	 * @param taskId
	 */
	public Object getBusiArgs(String taskId) {
		Object variable = taskService.getVariable(taskId, FlowConstant.BUSI_ARG);
		
		return variable;
	}
	
	/**
	 * 获取流程中的业务参数
	 * @param executionId
	 */
	public Object getBusiArgsByExecutionId(String executionId) {
		Object variableByExecutionId = getVariableByExecutionId(executionId, FlowConstant.BUSI_ARG);
		
		return variableByExecutionId;
	}
	
	public Object getBusiInstanceByExecutionId(String executionId) {
		Object instanceByExecutionId = getVariableByExecutionId(executionId, FlowConstant.INSTANCE);
		return instanceByExecutionId;
	}
	/**
	 * 设置执行流流程变量
	 * @param taskId
	 * @param map
	 */
	public void setVariableLocal(String taskId,Map<String,Object> map) {
		taskService.setVariablesLocal(taskId, map);
	}
	/**
	 * 设置全局流程变量
	 * @param taskId
	 * @param map
	 */
	public void setVariable(String taskId,Map<String,Object> map) {
		taskService.setVariables(taskId, map);
	}
	
	public void setVariable(String taskId,String varibleName,Object value) {
		taskService.setVariable(taskId, varibleName, value);
	}
	
	

	public Object getVariable(String taskId, String variableName) {
		Object variable = taskService.getVariable(taskId, variableName);
		return variable;
	}
	
	/**
	 * 获得本结点的流程变量
	 * @param taskId
	 * @param variableName
	 * @return
	 */
	public Object getVariableLocal(String taskId, String variableName) {
		Object variableLocal = taskService.getVariableLocal(taskId, variableName);
		return variableLocal;
	}
	
	
	public Object getVariableByExecutionId(String executionId, String variableName) {
		Object variable = runtimeService.getVariable(executionId, variableName);
		return variable;
	}

	public  Object getVariableByProcessDefinitionId(String processDefinitionId, String variableName){
		Object variable=runtimeService.getVariable(processDefinitionId,variableName);
		return variable;
	}
	
	/**
	 * 删除流程实例
	 * @param processInstanceId
	 * @param deleteReason
	 */
	public void deleteProcessInstance(String processInstanceId,String deleteReason) {
		runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
	}
    /**
     * 挂起流程	
     * @param processInstanceId
     */
	public void suspendProcessInstance(String processInstanceId) {
		runtimeService.suspendProcessInstanceById(processInstanceId);
	}
	/**
	 * 恢复流程
	 * @param processInstanceId
	 */
	public void activeProcessInstance(String processInstanceId) {
		runtimeService.activateProcessInstanceById(processInstanceId);
	}
	
	/**
	 * 撤回信号量
	 */
	public void undoTicketAction(String processInstanceId,String undoSignal,Map<String,Object> processVariables) {
		List<Execution> executeList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).signalEventSubscriptionName(undoSignal).list();
		for (Execution execution : executeList) {
			runtimeService.signalEventReceived(undoSignal,execution.getId(), processVariables);
		}
	}

	/**
	 * 根据发布的id获取define的id
	 * @param deployId
	 * @return
	 */
	public String getDefineIdByDeploy(String deployId) {

		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
		return singleResult.getId();
	}

	/**
	 * 根据发布的id获取define的id
	 * @param deployId
	 * @return
	 */
	public String getDefineIdByDeployKey(String deployKey) {
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery().processDefinitionKey(deployKey).singleResult();
		return singleResult.getId();
	}
	
	
	public boolean querySignal(String executionId, String signal) {
		long count = runtimeService.createExecutionQuery().executionId(executionId).signalEventSubscriptionName(signal).count();
		return count > 0;
	}

	public BusinessIntance getFlowInstance(String executionId) {
		Object data = runtimeService.getVariable(executionId, FlowConstant.INSTANCE);
		if(null == data){
			return null;
		}
		return JSONObject.parseObject(data.toString(),BusinessIntance.class);
	}
	public Map getBusiArgVariable(String processInstanceId){
		return (Map)runtimeService.getVariable(processInstanceId,FlowConstant.BUSI_ARG);
	}

	/**
	 * 根据流程定义id，获取是否还有正在运行的流程
	 * @param defineIdByDeploy
	 * @return
	 */
	public boolean containsInstance(String defineIdByDeploy) {
		long count = runtimeService.createExecutionQuery().processDefinitionId(defineIdByDeploy).count();
		
		return count > 0;
	}

	public String getCanceledReason(String processInstanceId) {
		HistoricProcessInstance singleResult = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if(singleResult == null) {
			return "";
		}
		return singleResult.getDeleteReason();
	}
	
	public String getDeployName(String deploymentId) {
		Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
		String name = deployment.getName();
		return name;
	}

	public Optional<Execution> getParentExecution(String executionId, String instanceId) {
		Execution singleResult = runtimeService.createExecutionQuery().parentId(executionId).singleResult();
		long count = runtimeService.createExecutionQuery().processInstanceId(instanceId).count();
		System.out.println("runtime execution count:" + count);
		List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(instanceId).list();
		for (Execution execution : list) {
			System.out.println("Execution execution:" + execution.getId());
		}
		if(singleResult != null) {
			String parentId = singleResult.getParentId();
			if(parentId != null) {
				Execution pExecution = runtimeService.createExecutionQuery().executionId(parentId).singleResult();
				return Optional.ofNullable(pExecution);
			}
		}
		return Optional.empty();
	}
	
	
	public Optional<ProcessInstance> getParentProcessInstance(String instanceId) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().subProcessInstanceId(instanceId).singleResult();
		if(processInstance!=null) {
			String parentId = processInstance.getParentId();
			if(parentId!=null) {
				ProcessInstance singleResult = runtimeService.createProcessInstanceQuery().superProcessInstanceId(instanceId).singleResult();
				Execution pExecution = runtimeService.createExecutionQuery().executionId(parentId).singleResult();
				return Optional.ofNullable(singleResult);
			}
		}
		return Optional.empty();
	}

	public Optional<Execution> getExecutionById(String id) {
		Execution singleResult = runtimeService.createExecutionQuery().executionId(id).singleResult();
		return Optional.ofNullable(singleResult);
	}
	
	public Optional<ProcessInstance> getProcessInstanceById(String id) {
		ProcessInstance singleResult = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
		return Optional.ofNullable(singleResult);
	}
	
	public  String getFlowVaribleStr(String variableName,DelegateTask delegateTask) {
		Object variable = delegateTask.getVariable(variableName);
		if(variable!=null) {
			return variable.toString();
		}else {
			return "";
		}
	}
	
	/**
	 * 获得监听器属性值
	 * @param fixValue
	 * @param variableScope
	 * @return
	 */
	public String getContextKey(FixedValue fixValue,VariableScope variableScope) {
		if(fixValue!=null) {
			Object value = fixValue.getValue(variableScope);
			if(value!=null) {
				return String.valueOf(value);			
			}else {
				return "";	
			}
		}
		return "";
	}
	
	/**
	 * 设置本节点监听器
	 * @param delegateTask
	 * @param varibleName
	 * @param value
	 */
	public void setFlowLocalVari(DelegateTask delegateTask,String varibleName,Object value) {
		String id = delegateTask.getId();
		taskService.setVariableLocal(id, varibleName, value);
		
	}
	
	
	public Object getFlowVariLocal(DelegateTask delegateTask,String varibleName) {
		String id = delegateTask.getId();
		Object obj = taskService.getVariableLocal(id,varibleName);
		return obj;
	}

	public User getFlowCurrentuser(String executionId) {
		Object data = runtimeService.getVariable(executionId, FlowConstant.CURRENTUSER);
		if(null == data){
			return null;
		}
		return JSONObject.parseObject(data.toString(),User.class);
	}

    public Result<BusinessIntance> createTicket(WorkDataVOByName datas) {
		// 根据流程定义的内容，和表单内容。
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", datas.getProcessName()));
		conditions.add(QueryCondition.eq("ticketStatus", ProcessStateEnum.used));
		List<MyTicket> list = myTicketService.findAll(conditions);
		if(list.size()==1) {
			MyTicket myTicket = list.get(0);
			String guid = myTicket.getGuid();
			WorkDataVO map = mapper.map(datas, WorkDataVO.class);
			map.setProcessdefGuid(guid);
			Result<BusinessIntance> create = businessTaskService.createTicket(map);
			return create;
		}else {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "没有"+datas.getProcessName()+",请检查！");
		}
    }

	/**
	 * 发布流程
	 * processPath：流程路径
	 * @param processPath
	 */
	public DeploymentResponse deployProcess(String resourceName, String processPath) {
		File file = new File(processPath);
		if(file.exists()) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				logger.error("找不到bpmn文件", e);
			}
			Deployment deploy = repositoryService.createDeployment().addInputStream(resourceName, inputStream).deploy();
			return new DeploymentResponse(deploy);
		}else {
			throw new RuntimeException("找不到文件"+processPath+"路径");
		}
	}
}


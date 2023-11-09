package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTask;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskLog;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskLogService;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("businessLogListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BusinessLogListener implements TaskListener,ExecutionListener {

	private static Logger logger = LoggerFactory.getLogger(BusinessLogListener.class);
	@Autowired
	private BusinessTaskLogService businessTaskLogService;
	@Autowired
	private FlowService flowService;
	@Autowired
	private AuthService authService;

	@Autowired
	private BusinessTaskService businessTaskService;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 任务节点历程监听器
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
		FlowVaries flowConstantVO = new FlowVaries();
		String userId = getVaribleValue(delegateTask, FlowConstant.USERID);//用户ID
    	String action = getVaribleValue(delegateTask, FlowConstant.ACTION);//流向
    	String advice = getVaribleValue(delegateTask, FlowConstant.ADVICE);//意见处理
    	String contextKey = getVaribleValue(delegateTask,FlowConstant.CONTEXTKEY);
    	String contextId = getVaribleValue(delegateTask, FlowConstant.CONTEXTID);
    	logger.info("advice建议:{}", advice);
    	String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
    	String taskName = delegateTask.getName();
    	String processDefinitionId = delegateTask.getProcessDefinitionId();
    	String processInstanceId = delegateTask.getProcessInstanceId();
    	Object variableByExecutionId = flowService.getVariableByExecutionId(processInstanceId, FlowConstant.PARAMS);
		if(variableByExecutionId instanceof List){
			List<Map<String,Object>> params = (List<Map<String,Object>>)variableByExecutionId;
			flowConstantVO.setParams(params);
		}
    	flowConstantVO.setAdvice(advice);
    	flowConstantVO.setUserId(userId);
    	flowConstantVO.setAction(action);
    	flowConstantVO.setTaskDefineKey(taskDefinitionKey);
    	flowConstantVO.setTaskDefineName(taskName);
    	flowConstantVO.setProcessDefinitionId(processDefinitionId);
    	flowConstantVO.setProcessInstanceId(processInstanceId);
    	flowConstantVO.setContextKey(contextKey);
    	flowConstantVO.setContextId(contextId);
		this.addBusinessTaskLog(flowConstantVO);		
	}

	/**
	 * 执行流历程监听器
	 */
	@Override
	public void notify(DelegateExecution execution) {
		FlowVaries flowConstantVO = new FlowVaries();
		String userId = execution.getVariable(FlowConstant.USERID).toString();
		String action = execution.getVariable(FlowConstant.ACTION).toString();
		String advice = execution.getVariable(FlowConstant.ADVICE).toString();
		String contextKey = execution.getVariable(FlowConstant.CONTEXTKEY).toString();
		String contextId = execution.getVariable(FlowConstant.CONTEXTID).toString();
		String eventName = execution.getEventName();
		String processDefinitionId = execution.getProcessDefinitionId();
		String processInstanceId = execution.getProcessInstanceId();
		Object variableByExecutionId = flowService.getVariableByExecutionId(processInstanceId, FlowConstant.PARAMS);
		if(variableByExecutionId instanceof List){
			List<Map<String,Object>> params = (List<Map<String,Object>>)variableByExecutionId;
			flowConstantVO.setParams(params);
		}
		
		flowConstantVO.setAction(action);
		flowConstantVO.setUserId(userId);
		flowConstantVO.setAdvice(advice);
		flowConstantVO.setProcessDefinitionId(processDefinitionId);
		flowConstantVO.setTaskDefineKey(processDefinitionId);
		flowConstantVO.setTaskDefineName(eventName);
		flowConstantVO.setProcessInstanceId(processInstanceId);
		flowConstantVO.setContextId(contextId);
		flowConstantVO.setContextKey(contextKey);
		this.addBusinessTaskLog(flowConstantVO);
	}

	/**
	 * 增加每一条的历史记录
	 * @param
	 */
    private void addBusinessTaskLog(FlowVaries flowConstantVO) {
    	String userId = flowConstantVO.getUserId();
    	String advice = flowConstantVO.getAdvice();
    	String action = flowConstantVO.getAction();
    	List<Map<String,Object>> params = flowConstantVO.getParams();
    	String paramsStr = JsonMapper.toJsonString(params);
    	String processInstanceId = flowConstantVO.getProcessInstanceId();
    	String processDefinitionId = flowConstantVO.getProcessDefinitionId();
    	String taskDefineKey = flowConstantVO.getTaskDefineKey();
    	String taskDefineName = flowConstantVO.getTaskDefineName();
    	BusinessTaskLog businessTaskLog = new BusinessTaskLog();
    	businessTaskLog.setId(UUID.randomUUID().toString());
    	businessTaskLog.setPeopleId(userId);
    	businessTaskLog.setParams(paramsStr);
    	
    	User user = authService.getUserInfoByUserId(userId);
    	if(user != null) {
    		businessTaskLog.setPeopleName(user.getName());
    	} else {
    		businessTaskLog.setPeopleName(userId);
    	}
    	businessTaskLog.setTaskDefindName(taskDefineName);
    	businessTaskLog.setTaskDefineKey(taskDefineKey);
    	businessTaskLog.setProcessInstanceId(processInstanceId);
    	businessTaskLog.setProcessKey(processDefinitionId);
    	businessTaskLog.setTime(DateUtil.format(new Date()));
    	businessTaskLog.setAction(action);
    	if(advice==null){
    		advice = "该工单已处理";
    	}
    	businessTaskLog.setAdvice(advice);

		BusinessTask businessTask=new BusinessTask();
		businessTask.setTaskDefineKey(taskDefineKey);

		businessTask.setBusiId(processInstanceId);
		List<BusinessTask> businessTaskList=businessTaskService.findAll(businessTask);
		if(businessTaskList.size()>0){
			businessTask=businessTaskList.get(businessTaskList.size()-1);
			if(businessTask.getDeadlineDate()!=null){
                businessTaskLog.setDeadlineDate(businessTask.getDeadlineDate());
            }else{
				BusinessIntance businessIntance= businessTask.getInstance();
				if(null == businessIntance){
					throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), processInstanceId+"对应的流程实例不存在！");
				}
			    businessTaskLog.setDeadlineDate(businessIntance.getDeadlineDate());
            }

		}
		businessTaskLog.setFinishDate(new Date());
		businessTaskLog.setContextKey(flowConstantVO.getContextKey());
		businessTaskLog.setContextId(flowConstantVO.getContextId());
    	businessTaskLogService.save(businessTaskLog);
    }
	
    /**
     * 获得流程变量值
     * @param key
     * @return
     */
    private String getVaribleValue(DelegateTask delegateTask,String key){
    	String str = null;
    	Object obj = delegateTask.getVariable(key);
    	if(obj!=null) {
    		str = obj.toString();
    	}
    	return str;
    }
}

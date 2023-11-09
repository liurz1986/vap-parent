package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.*;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class BusinessTaskListenerAbs implements TaskListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5292796745070129393L;
	private static Logger logger = LoggerFactory.getLogger(BusinessTaskListenerAbs.class);
	
	private  FixedValue deadline;    //配置截止日期
	
	private  FixedValue actions;        // 用  竖线 | 进行分隔的动作；比如反馈|转派

	private  FixedValue assignType;
	
	private  FixedValue eventAlarmStatus; // 状态
	
	@Autowired
	protected BusinessTaskService businessTaskService;

	@Autowired
	private FlowService flowService;

	@Autowired
	private AuthService authService;
	@Autowired
	protected BusinessIntanceService businessIntanceService;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		// 根据province的组织机构节点获取对应的人，来创建任务和任务候选人
		String eventName = delegateTask.getEventName();
		logger.info("监听器任务名称"+delegateTask.getName()+":"+delegateTask.getProcessInstanceId());
		switch (eventName) {
		case "create":
			try{
				logger.info("创建任务节点"+delegateTask.getName()+"开始");
				this.createBusinessTask(delegateTask);
				logger.info("创建任务节点"+delegateTask.getName()+"成功");
			}catch (Exception e){
				logger.error("创建任务失败",e);
				throw new RuntimeException("创建任务失败",e);
			}

			break;
		case "delete":
			// 删除任务和对应的候选人实体对象
			try{
				logger.info("删除任务节点"+delegateTask.getName()+"开始");
				String taskId = delegateTask.getId();
				this.deleteTask(taskId);
				logger.info("删除任务节点"+delegateTask.getName()+"成功");
			}catch (Exception e){
				logger.error("删除任务失败",e);
				throw new RuntimeException("删除任务失败",e);
			}

			break;
		default:
			break;
		}
	}


	/**
	 * 创建一个BusinessTask任务
	 * @param delegateTask
	 */
	
	private void createBusinessTask(DelegateTask delegateTask) {
		// 创建的时候创建一个业务对象任务。 // 会签流程的时候自动会进来多次
		setContextIdAndLabelVariLocal(delegateTask);
		BusinessTask bTask = new BusinessTask();
		String bTaskId = UUID.randomUUID().toString();
		bTask.setId(bTaskId);
		bTask.setBusiKey(delegateTask.getProcessDefinitionId());
		bTask.setBusiId(delegateTask.getProcessInstanceId());
		BusinessIntance instance = flowService.getFlowInstance(delegateTask.getExecutionId());
		bTask.setInstance(instance);
		bTask.setTaskId(delegateTask.getId());
		bTask.setTaskDefineKey(delegateTask.getTaskDefinitionKey());
		bTask.setTaskDefindName(delegateTask.getName());
		bTask.setCreateDate(new Date());
		bTask.setTaskType(BusinessTaskType.node);
		if(deadline!=null){
			 String deadlineStr = deadline.getValue(delegateTask).toString();
			 Integer deadLineTime = Integer.valueOf(deadlineStr);
			 Date deadlineDate=DateUtil.addDay(new Date(), deadLineTime);
			 bTask.setDeadlineDate(deadlineDate);
		}
		if(actions != null){
			String actionStr = actions.getValue(delegateTask).toString();
			bTask.setActions(actionStr);
		} else {
			bTask.setActions("");   // 默认没有值的情况下，界面显示确认操作
		}
		Set<BusinessTaskCandidate> businessCandidateSet = getBusinessCandidates(delegateTask, bTaskId);
		setBusinessInstaceStateVaribless(delegateTask, businessCandidateSet);
		bTask.setCandidates(businessCandidateSet);
		setAsignVarible(delegateTask, bTask);  //TODO 设置对应的contextId和contextKey
		logger.info(instance.getProcessDefName() + "流程，对应事件id" + instance.getGuid() + "监听器执行节点创建");
		logger.info("创建节点名称" + bTask.getTaskDefindName() + "创建节点task_id:" + bTask.getTaskId());
		businessTaskService.save(bTask);
		logger.info("新增任务" + delegateTask.getName() + "成功，对应事件id" + instance.getGuid());
	}

	/**
	 * 设置流程实例状态变量
	 * @param delegateTask
	 * @param businessCandidateSet
	 */
	private void setBusinessInstaceStateVaribless(DelegateTask delegateTask, Set<BusinessTaskCandidate> businessCandidateSet) {
		if(businessCandidateSet.size()==0){ //说明该节点的候选人为空
			flowService.setVariables(delegateTask.getProcessInstanceId(),FlowConstant.PROCESSINSTANCESTATE,BusinessInstanceStatEnum.pending);
		}else{
			flowService.setVariables(delegateTask.getProcessInstanceId(),FlowConstant.PROCESSINSTANCESTATE,BusinessInstanceStatEnum.dealing);
		}
	}
	/**
	 * 设置对应本地流程变量
	 * @param delegateTask
	 */
	private void setContextIdAndLabelVariLocal(DelegateTask delegateTask) {
		Object variable = delegateTask.getVariable(FlowConstant.PARAMS);
		if(variable instanceof List<?>) {
			List<Map<String,Object>> params = (List<Map<String,Object>>)variable;
			for (Map<String, Object> map : params) {
				if(map.containsKey("key")&&map.get("key")!=null&&map.containsKey("value")&&map.get("value")!=null){
					String key = map.get("key").toString();
					if(key.equals("contextId")) {
						String contextId = map.get("value").toString();
						delegateTask.setVariableLocal("contextId", contextId);
					}
					if(key.equals("contextLabel")) {
						String contextLabel = map.get("value").toString();
						delegateTask.setVariableLocal("contextLabel", contextLabel);
					}
				}
		    }
		}
	}

	/**
	 * 获得businessCandidates
	 * @param delegateTask
	 * @param bTaskId
	 * @return
	 */
	private Set<BusinessTaskCandidate> getBusinessCandidates(DelegateTask delegateTask, String bTaskId) {
		// 获取一组人
		Set<String> userIds = getTaskCandidates(delegateTask);
		// 把当前任务节点处理人存起来，为了后面邮件监听器、短信监听器给节点处理人发邮件、发短信用
		flowService.setVariable(delegateTask.getId(), "nodeHandleUser",userIds);
		Set<BusinessTaskCandidate> businessCandidateSet = new HashSet<>();
		Map<String,Object> userMsg = null;
		for (String userId : userIds) {
			logger.info("userId: "+userId);
			if(!userId.equals(CandidateType.BUSINESTYPE)){
				BusinessTaskCandidate businessTaskCandidate = new BusinessTaskCandidate();
				businessTaskCandidate.setId(UUID.randomUUID().toString());  // 和上面的任务同id
				businessTaskCandidate.setBusiTaskId(bTaskId);
				businessTaskCandidate.setTaskId(delegateTask.getId());
				businessTaskCandidate.setCandidate(userId);
				User user=authService.getUserInfoByUserId(userId);
				if(user!=null){
					userMsg = new HashMap<String,Object>();
					businessTaskCandidate.setCandidateName(user.getName());
				}
				businessTaskCandidate.setCreateDate(new Date());
				if(userIds.contains(CandidateType.BUSINESTYPE)&&assignType!=null){
					Object assignTypeObj=assignType.getValue(delegateTask);
					int assignTypeInt=Integer.parseInt(assignTypeObj.toString());
					businessTaskCandidate.setAssignType(assignTypeInt);
				}else{
					businessTaskCandidate.setAssignType(1);
				}
				businessCandidateSet.add(businessTaskCandidate);

			}
		}
		return businessCandidateSet;
	}

	/**
	 * 完成任务，转移到下一任务
	 * @param taskId
	 */
	private void deleteTask(String taskId){
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("taskId", taskId));
		List<BusinessTask> list = businessTaskService.findAll(conditions);
		for (BusinessTask businessTask : list) {
			logger.info("删除任务节点,任务id："+businessTask.getId()+"==对应事件id："+businessTask.getInstance().getGuid());
			businessTaskService.delete(businessTask);
		}
	}

	
	/**
	 * 获得任务的候选人
	 * @param delegateTask
	 * @return
	 */
	protected abstract Set<String> getTaskCandidates(DelegateTask delegateTask);

	/**
	 * 设置会签变量
	 * @param delegateTask
	 * @param bTask
	 */
	protected abstract void setAsignVarible(DelegateTask delegateTask,BusinessTask bTask);



}

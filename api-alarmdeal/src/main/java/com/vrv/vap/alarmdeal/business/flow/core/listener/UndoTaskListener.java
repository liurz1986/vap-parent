package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTask;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskCandidate;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskType;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.log.LoggerUtil;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 撤回任务监听器
 * @author lijihong
 *
 */
@Service("undoTaskListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UndoTaskListener implements ExecutionListener {

	private static LoggerUtil logger = LoggerUtil.getLogger(UndoTaskListener.class);
			
	private FixedValue candidateType;   // 可以指定上一动作的执行者
	private FixedValue candidate;      // TODO 根据逗号分隔。进一步可以考虑使用流程变量占位符。
	private FixedValue actions;        // 用  竖线 | 进行分隔的动作；比如撤回。非必填
	private FixedValue signal;        // 任务中要发送的信号量
	private FixedValue taskCode;      // 任务编码，对应在删除任务的时候可以通过编码来识别
	
	@Autowired
	private AuthService authService;
	@Autowired
	private FlowService flowService;
	
	@Autowired
	private BusinessTaskService businessTaskService;

	@Autowired
	private BusinessIntanceService businessIntanceService;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		logger.info("撤销任务" + execution.getEventName() +"  "+ execution.getCurrentActivityId()  +"  "+ execution.getCurrentActivityName() + "  " + execution.getId());
		createBusinessTask(execution);
	}
	
	/**
	 * 创建一个撤销的BusinessTask任务
	 * @param delegateExection
	 */
	private void createBusinessTask(DelegateExecution delegateExection) {
		// 创建的时候创建一个业务对象任务。 // 会签流程的时候自动会进来多次
		BusinessTask bTask = new BusinessTask();
		String bTaskId = UUID.randomUUID().toString();
		bTask.setId(bTaskId);
		bTask.setBusiKey(delegateExection.getProcessDefinitionId());
		bTask.setBusiId(delegateExection.getProcessInstanceId());
		bTask.setExecutionId(delegateExection.getId());
		BusinessIntance instance = businessIntanceService.getByInstanceId(delegateExection.getProcessInstanceId());
		bTask.setInstance(instance);
		if(null == signal){
			throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "signal没有配置，请联系管理员操作！");
		}
		bTask.setRelatedInfos(signal.getValue(delegateExection).toString());
		bTask.setTaskDefindName("撤销任务");
		bTask.setTaskType(BusinessTaskType.undo);
		if(taskCode != null) {
			bTask.setTaskCode(taskCode.getValue(delegateExection).toString());
		}
		bTask.setCreateDate(new Date());
		if(actions != null) {
			String actionStr = actions.getValue(delegateExection).toString();
			bTask.setActions(actionStr);
		} else {
			bTask.setActions("撤回");   // 默认没有值的情况下，界面显示确认操作
		}
		Set<BusinessTaskCandidate> businessCandidateSet = getBusinessCandidates(delegateExection, bTaskId);
		bTask.setCandidates(businessCandidateSet);
		businessTaskService.save(bTask);
		logger.info("新增撤回任务成功");
	}

	private Set<BusinessTaskCandidate> getBusinessCandidates(DelegateExecution delegateTask, String bTaskId) {
		Set<String> userIds = getTaskCandidates(delegateTask);
		Set<BusinessTaskCandidate> businessCandidateSet = new HashSet<>();
		for (String userId : userIds) {
			BusinessTaskCandidate candidate = new BusinessTaskCandidate();
			candidate.setId(UUID.randomUUID().toString());  // 和上面的任务同id
			candidate.setBusiTaskId(bTaskId);
			candidate.setTaskId(delegateTask.getId());
			candidate.setCandidate(userId);
			candidate.setCreateDate(new Date());
			User user=authService.getUserInfoByUserId(userId);
			if(user!=null){
				candidate.setCandidateName(user.getName());
			}
			businessCandidateSet.add(candidate);
		}
		return businessCandidateSet;
	}

	private Set<String> getTaskCandidates(DelegateExecution delegateTask) {
		List<String> users = getUsers(delegateTask);
		Set<String> result = new HashSet<>();
		for (String string : users) {
			result.add(string);
		}
		
		return result;
	}

	private List<String> getUsers(DelegateExecution delegateTask) {
		List<String> users = null;
		switch (candidateType.getValue(delegateTask).toString()) {
		case "role":
			users = authService.getUsersByRole(candidate.getValue(delegateTask).toString());
			break;
		case "user":
			String userStr = candidate.getValue(delegateTask).toString(); // TODO 特别注意，用户最好不要重复
			String[] split = userStr.split(",");
			users = Arrays.asList(split);
			break;
		case "r_arg":
			users = this.getRargCandidate(delegateTask, users);
			break;
		case CandidateType.F_LASTACTIONUSER:
			String userId = flowService.getVariableByExecutionId(delegateTask.getId(), FlowConstant.USERID).toString();
			users = new ArrayList<>();
			users.add(userId);
			break;
		default:
			break;
		}
		
		return users;
	}
	
	/**
     * 获得特定字段获得对应的Candidate
     * @param delegateTask
     * @param users
     * @return
     */
	private List<String> getRargCandidate(DelegateExecution delegateTask, List<String> users) {
		Object busiArgs = flowService.getBusiArgs(delegateTask.getId());
		String field = candidate.getValue(delegateTask).toString();
		Field field2;
		try {
			field2 = busiArgs.getClass().getDeclaredField(field);
			ReflectionUtils.makeAccessible(field2);;
			String createUserIds = field2.get(busiArgs).toString();
			String[] split2 = createUserIds.split(",");
			users = Arrays.asList(split2);	
		} catch (NoSuchFieldException e) {
			logger.error("没有该属性值", e);
		} catch (SecurityException e) {
			logger.error("反射安全性异常", e);
		} catch (IllegalArgumentException e) {
			logger.error("IllegalArgumentException", e);
		} catch (IllegalAccessException e) {
			logger.error("非法反射异常", e);
		}
		return users;
	}

}

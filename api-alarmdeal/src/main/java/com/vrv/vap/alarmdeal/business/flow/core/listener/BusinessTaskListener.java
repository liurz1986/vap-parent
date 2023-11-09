package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTask;
import com.vrv.vap.alarmdeal.business.flow.core.model.FixModel;
import com.vrv.vap.alarmdeal.business.flow.core.service.CandidateService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 配置用于自定义的业务对象任务的数据更新
 * 
 * @author lijihong
 *
 */
@Service("businessTaskListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BusinessTaskListener extends BusinessTaskListenerAbs {

	private FixedValue candidateType;
	private FixedValue candidate; // TODO 根据逗号分隔。进一步可以考虑使用流程变量占位符。
	private FixedValue secParam; // TODO 安全域附属参数
	private FixedValue roleParam; // TODO 角色附属参数

	private FixedValue roleValue; // 角色，根据逗号分隔 2021-09-10

	private FixedValue userValue; // 用户，根据逗号分隔 2021-09-10

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private FlowService flowService;

	private static final long serialVersionUID = 5073884666743994137L;

	public FixedValue getCandidateType() {
		return candidateType;
	}

	public void setCandidateType(FixedValue candidateType) {
		this.candidateType = candidateType;
	}

	public FixedValue getCandidate() {
		return candidate;
	}

	public void setCandidate(FixedValue candidate) {
		this.candidate = candidate;
	}

	@Override
	protected Set<String> getTaskCandidates(DelegateTask delegateTask) {
		FixModel fixModel = initFixModel(delegateTask);
		List<String> users = candidateService.getUsers(delegateTask, fixModel);
		delegateTask.addCandidateUsers(users);
		//TODO 业务节点不再需要判断人，采用外部触发机制；
		Set<String> result = new HashSet<>();
		for (String string : users) {
			result.add(string);
		}
		return result;
	}

	public FixModel initFixModel(DelegateTask delegateTask) {
		FixModel fixModel = new FixModel();
		fixModel.setCandidate(candidate);
		fixModel.setCandidateType(candidateType);
		fixModel.setRoleParam(roleParam);
		fixModel.setSecParam(secParam);
		fixModel.setTaskId(delegateTask.getId());
		fixModel.setExecutionId(delegateTask.getExecutionId());
		fixModel.setIntanceId(delegateTask.getProcessDefinitionId());
		fixModel.setUserValue(userValue);
		fixModel.setRoleValue(roleValue);
		Object busiArgs = flowService.getBusiArgs(delegateTask.getId());
		fixModel.setBusiArgs(busiArgs);
		return fixModel;
	}

	@Override
	protected void setAsignVarible(DelegateTask delegateTask, BusinessTask bTask) {
		String contextKey = flowService.getFlowVaribleStr("contextKey", delegateTask);
		String contextlabel = flowService.getFlowVaribleStr("contextLabel", delegateTask);
		bTask.setContextLabel(contextlabel);
		bTask.setContextKey(contextKey);
		if (StringUtils.isEmpty(contextKey)) {
			bTask.setContextId("");
		} else {
			String contextId = flowService.getFlowVaribleStr("contextId", delegateTask);
			String[] contextKeyArr = contextKey.split("\\/");
			String[] contextIdArr = contextId.split("\\/");
			if (contextKeyArr.length == contextIdArr.length) {
				bTask.setContextId(contextId);
			} else if (contextKeyArr.length < contextIdArr.length) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < contextKeyArr.length; i++) {
					if (i+1 == contextKeyArr.length) {
						sb.append(contextIdArr[i]);
					} else {
						sb.append(contextIdArr[i] + "/");
					}
					
				}					
				bTask.setContextId(sb.toString());
			}else {
				throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "数据存在问题请检查！");
			}
		}
	}

}

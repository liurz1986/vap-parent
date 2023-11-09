package com.vrv.vap.alarmdeal.business.flow.core.listener.asign;

import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.business.flow.core.listener.BusinessTaskListenerAbs;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTask;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.el.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年10月22日 下午2:51:28 
* 类说明     角色会签监听器
*/
@Service("roleAsignsListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RoleAsignsListener extends BusinessTaskListenerAbs {

	private static final long serialVersionUID = 1L;

	@Autowired
	private AuthService authService;
	@Autowired
	private FlowService flowService;
	
	private FixedValue contextKey;
	// 会签变量名
	private FixedValue signerKey;
	
	private static Logger logger = LoggerFactory.getLogger(RoleAsignsListener.class);
	
	@Override
	protected Set<String> getTaskCandidates(DelegateTask delegateTask) {
		businessTaskService.setAssignCompleteCondition(delegateTask);
		String signerKeyValue = flowService.getContextKey(signerKey,delegateTask);
		logger.info("会签变量名："+signerKeyValue);
		String role = delegateTask.getVariable(signerKeyValue).toString();
		logger.info("角色会签监听器Id:{}", role);
		Set<String> users = new HashSet<>();
		List<String> usersByRole = authService.getUsersByRole(role);
		users.addAll(usersByRole);
		return users;
	}

	@Override
	protected void setAsignVarible(DelegateTask delegateTask, BusinessTask bTask) {
		String signerKeyValue = flowService.getContextKey(signerKey,delegateTask);
		businessTaskService.setSubFlowVariByContextId(delegateTask, bTask,signerKeyValue);
		businessTaskService.setSubFlowVariByContextKey(delegateTask,contextKey,bTask);
		setAssignRoleContextLabel(bTask);
	}



	/**
	 * 设置人员会签监听器的contextLabel
	 * @param bTask
	 */
	private void setAssignRoleContextLabel(BusinessTask bTask) {
		String contextId = bTask.getContextId();
		String curContextId = null;
		if(contextId.contains("/")) {
			curContextId = contextId.substring(contextId.lastIndexOf("/")+1, contextId.length());
		}else {
			curContextId = contextId;
		}
		logger.info("当前用户Id：{}", curContextId);
		Role role = authService.getRoleInfoByRoleCode(curContextId);
		if(role!=null) {
			bTask.setContextLabel(role.getName());
		}else {
			bTask.setContextLabel("");
		}
	}
	
	
}

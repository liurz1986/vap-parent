package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTask;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskService;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("undoTaskClearListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UndoTaskClearListener implements ExecutionListener {
	private FixedValue taskCode;      // 任务编码，对应在删除任务的时候可以�?�过编码来识�?
	
	@Autowired
	private BusinessTaskService businessTaskService;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		String instanceId = execution.getId();
		if(null == taskCode){
			throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "taskCode没有配置，请联系管理员操作！");
		}
		String code = taskCode.getValue(execution).toString();
		List<BusinessTask> tasks = businessTaskService.getByInstanceIdAndTaskcode(instanceId, code);
		
		for (BusinessTask businessTask : tasks) {
			
			businessTaskService.delete(businessTask);
		}
	}
}

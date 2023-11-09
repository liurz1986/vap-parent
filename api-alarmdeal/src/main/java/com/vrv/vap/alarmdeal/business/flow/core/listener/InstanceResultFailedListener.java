package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.JuelExpression;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * 使用这个监听器来确认流程失败结束
 * @author lijihong
 *
 */
@Service("instanceResultFailedListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class InstanceResultFailedListener implements ExecutionListener {

	private JuelExpression result;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
      if(result==null){
    	  execution.setVariable(FlowConstant.PROCESSRESULT, false);
      }else {
    	  execution.setVariable(FlowConstant.PROCESSRESULT, result.getValue(execution));
      }
	}

}

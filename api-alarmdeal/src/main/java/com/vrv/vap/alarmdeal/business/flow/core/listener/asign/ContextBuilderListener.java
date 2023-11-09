package com.vrv.vap.alarmdeal.business.flow.core.listener.asign;

import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 子流程ContextKey执行监听器
 * @author wd-pc
 *
 */
@Service("contextBuilderListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ContextBuilderListener implements ExecutionListener  {

	private FixedValue contextKey;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LoggerFactory.getLogger(ContextBuilderListener.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String eventName = execution.getEventName();
		logger.info("eventName :"+eventName);
		if(StringUtils.isNotEmpty(eventName)) {
			switch (eventName) { 
			case "start":
				setContextKeyLocal(execution);
				break;
			default:
				break;
				
			}			
		}
		
	}

	/**
	 * 设置子流程的contextKey的流程变量
	 * @param execution
	 */
	private void setContextKeyLocal(DelegateExecution execution) {
		String contextKey = getContextKey(execution);
		Object variable = execution.getVariable("contextKey");
		if(variable != null) {
			String var = variable.toString();
			if(var.contains("/")) {
				String tmp = var.substring(var.lastIndexOf("/")+1, var.length());
				if(!tmp.equals(contextKey)) {
					execution.setVariableLocal("contextKey", variable.toString() + "/" + contextKey);						
				}						
			}else {
				if(!var.equals(contextKey)) {
					execution.setVariableLocal("contextKey", variable.toString() + "/" + contextKey);						
				}	
			}
		} else {
			execution.setVariableLocal("contextKey", contextKey);
		}
	}
	
	private String getContextKey(VariableScope variableScope) {
		Object value = contextKey.getValue(variableScope);
		if(contextKey!=null) {
			return String.valueOf(value);			
		}
		throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "当前子流程的contextKey为空，请检查！");
	}

}

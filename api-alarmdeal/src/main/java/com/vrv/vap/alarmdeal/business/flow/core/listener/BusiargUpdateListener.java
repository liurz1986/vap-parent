package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.BusinessInteractionMode;
import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.BusinessParamVO;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.json.JsonMapper;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三当监听器：主要用于kafka、http发送消息
 *
 *
 */

@Service("busiargUpdateListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BusiargUpdateListener implements TaskListener {

	private static Logger logger = LoggerFactory.getLogger(BusiargUpdateListener.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
 	private FixedValue routeKey;    //发送方式（kafka or http）
	private FixedValue extraParamURL; //http请求地址
	private FixedValue extraParamType;  //http请求类型（GET/POST）
	private FixedValue extraParamTopic; //kafka请求主题
	private FixedValue extraParam; //额外参数


	@Resource
	private FlowService flowService;

	@Resource
	private BusinessInteractionMode httpInteractionModeImpl;
	@Resource
	private BusinessInteractionMode kafkaInteractionModeImpl;

	@Override
	public void notify(DelegateTask delegateTask) {
		logger.info("BusiargUpdateListener业务监听器");
		//1.构造对应BusinessParamVO，主要是构造requestParams数据
		BusinessParamVO businessParamVO = contructBusinessParamsVO(delegateTask);
		//2.判断是采用kafka还是采用http的形式进行
		//3.根据构造的数据执行对应的与第三方通信的方法
		if(routeKey==null){
			throw  new RuntimeException("routeKey is null，please check it");
		}
		String mode = routeKey.getValue(delegateTask).toString();
		switch (mode) {
			case "kafka":
				kafkaInteractionModeImpl.interationModeImpl(businessParamVO);
                break;
			case "http":
				httpInteractionModeImpl.interationModeImpl(businessParamVO);
				break;
			default:
				break;
		}
		logger.info("the operator is successful！");
	}

	private BusinessParamVO contructBusinessParamsVO(DelegateTask delegateTask) {
		BusinessParamVO businessParamVO = new BusinessParamVO();

		String requestParams = constructRequestParams(delegateTask);
		businessParamVO.setRequestParam(requestParams);

		if(extraParamURL!=null){
			String url = extraParamURL.getValue(delegateTask).toString();
			logger.info("url:{}",url);
			businessParamVO.setExtraParamURL(url);
		}
		if(extraParamType!=null){
			String type = extraParamType.getValue(delegateTask).toString();
			logger.info("requestType:{}",type);
			businessParamVO.setExtraParamType(type);
		}

		if(extraParamTopic!=null){
			String topic = extraParamTopic.getValue(delegateTask).toString();
			logger.info("topic:{}",topic);
			businessParamVO.setExtraParamTopic(topic);
		}
		return businessParamVO;
	}

	/**
	 * 构造请求数据
	 * @param delegateTask
	 * @return
	 */
	private String constructRequestParams(DelegateTask delegateTask) {
		Gson gson = new Gson();
		Map<String,String> extraParams = new HashMap<>();
		Object busiArgs = flowService.getBusiArgsByExecutionId(delegateTask.getExecutionId());
		String busiArgsStr = JsonMapper.toJsonString(busiArgs);
		extraParams.put("businessArg",busiArgsStr);

		User user = flowService.getFlowCurrentuser(delegateTask.getExecutionId());
		if(user!=null){
			extraParams.put("currentUserId",String.valueOf(user.getId()));
		}

		String processInstanceId = delegateTask.getProcessInstanceId();  //流程实例ID
		extraParams.put("businessId",processInstanceId);

		if(extraParam!=null){
			Object paramValue = extraParam.getValue(delegateTask);
			extraParams.put("extraParam",paramValue.toString());
		}
        //对应节点操作：
		String action = getVaribleValue(delegateTask, FlowConstant.ACTION);
		extraParams.put("action",action);

		String requestParams = gson.toJson(extraParams);
		logger.info("the requestParams is:{}",requestParams);
	    return requestParams;
	}


	/**
	 * 获得对应的流程变量
	 * @param delegateTask
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

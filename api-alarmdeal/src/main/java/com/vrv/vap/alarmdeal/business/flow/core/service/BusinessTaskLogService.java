package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.vrv.vap.alarmdeal.business.flow.core.repository.BusinessTaskLogRepository;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskLog;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BusinessTaskLogService extends BaseServiceImpl<BusinessTaskLog, String> {

	@Autowired
	private BusinessTaskLogRepository businessTaskLogRepository;

	@Override
	public BusinessTaskLogRepository getRepository(){
		return businessTaskLogRepository;
	}
	
	public void saveStartLog(String processdefId, String userName,String userId, ProcessInstance processInstance) {
		BusinessTaskLog businessTaskLog = new BusinessTaskLog();
    	businessTaskLog.setId(UUID.randomUUID().toString());
    	businessTaskLog.setPeopleId(userId);
    	businessTaskLog.setPeopleName(userName);
    	businessTaskLog.setTaskDefindName("开启");
    	businessTaskLog.setTaskDefineKey("start");
    	businessTaskLog.setProcessInstanceId(processInstance.getId());
    	businessTaskLog.setProcessKey(processdefId);
    	businessTaskLog.setTime(DateUtil.format(new Date()));
    	businessTaskLog.setAction("开启");
    	businessTaskLog.setAdvice("工单创建");
		save(businessTaskLog);
	}

	public List<BusinessTaskLog> queryByInstanceIdAndNode(String instanceId,String nodeName){
		BusinessTaskLog businessTaskLog=new BusinessTaskLog();
		businessTaskLog.setProcessInstanceId(instanceId);
		businessTaskLog.setTaskDefindName(nodeName);
		Sort sort = Sort.by(Sort.Direction.DESC, "time");
		List<BusinessTaskLog> businessTaskLogList=findAll(businessTaskLog,sort);
		return  businessTaskLogList;
	}
}

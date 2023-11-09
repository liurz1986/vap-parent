package com.vrv.vap.alarmdeal.business.flow.core.config;

import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessInstanceStatEnum;

public interface InstanceEndListener {

	void end(String processInstanceId, BusinessInstanceStatEnum endcanceled);

}

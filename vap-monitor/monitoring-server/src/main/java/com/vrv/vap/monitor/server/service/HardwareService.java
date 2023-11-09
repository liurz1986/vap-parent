package com.vrv.vap.monitor.server.service;


import com.vrv.vap.monitor.server.vo.LocalHostInfoVO;
import com.vrv.vap.monitor.server.vo.TModulesVO;

import java.util.List;

public interface HardwareService {

	public LocalHostInfoVO getHostInfoDetail();

	/**
	 * 根据服务名称重启服务
	 * @param serviceName
	 * @return
	 */
	public boolean reStartService(String serviceName);

	List<TModulesVO> getServiceInfo();

	boolean checkService(String serviceName);

	boolean  checkServiceStatus(String serviceName);
}

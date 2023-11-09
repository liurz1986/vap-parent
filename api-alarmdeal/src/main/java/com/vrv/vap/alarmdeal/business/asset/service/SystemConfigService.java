package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.contract.SystemConfig;

import java.util.List;



public interface SystemConfigService {
	public String getStaticConfig(String key,List<SystemConfig> systemConfigs);
	
	public List<SystemConfig> getStaticConfigs();
	
	/**
	 * 当前项目名称
	 * @return
	 */
	public String getCurrentConfig(List<SystemConfig> systemConfigs) ;

	/**
	 * 通过conf_id 查询配置项的值
	 * @param confId
	 * @return
	 */
	public String getSysConfigById(String confId);
}

package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vrv.vap.alarmdeal.business.analysis.model.DealCommonLog;
import com.vrv.vap.alarmdeal.business.analysis.repository.DealCommonLogRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.DealCommonLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;

@Service
public class DealCommonLogServiceImpl extends BaseServiceImpl<DealCommonLog, String> implements DealCommonLogService {

	@Autowired
	private DealCommonLogRespository dealCommonLogRespository;
	
	@Override
	public DealCommonLogRespository getRepository() {
		return dealCommonLogRespository;
	}

	@Override
	public void addDealLog(String alarmItemGuid, String instanceId, String jsonInfo, String type) {
		refreshMailDealLog(instanceId);
		addNewDealLog(alarmItemGuid, instanceId, jsonInfo, type);
	}

	
	
	/**
     * 添加新的日志数据
     * @param alarmItemGuid
     * @param instanceId
     * @param jsonInfo
     * @param type
     */
	private void addNewDealLog(String alarmItemGuid, String instanceId, String jsonInfo, String type) {
		DealCommonLog dealcommonlog = new DealCommonLog();
		dealcommonlog.setGuid(UUIDUtils.get32UUID());
		dealcommonlog.setHappenTime(DateUtil.format(new Date()));
		dealcommonlog.setItemType(type);
		dealcommonlog.setDealInstanceId(instanceId);
		dealcommonlog.setLastversionFlag("new");
		dealcommonlog.setJsonInfo(jsonInfo);
		dealcommonlog.setItemGuid(alarmItemGuid);
		save(dealcommonlog);
	}
	
	/**
	 * 将旧的日志信息进行更新
	 * @param instanceId
	 */
	private void refreshMailDealLog(String instanceId) {
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();
		conditions.add(QueryCondition.eq("dealInstanceId", instanceId));
		conditions.add(QueryCondition.eq("lastversionFlag", "new"));
		List<DealCommonLog> list = findAll(conditions);
		for (DealCommonLog dealcommonlog : list) {
			dealcommonlog.setLastversionFlag("old");
			save(dealcommonlog);
		}
	}
	
}

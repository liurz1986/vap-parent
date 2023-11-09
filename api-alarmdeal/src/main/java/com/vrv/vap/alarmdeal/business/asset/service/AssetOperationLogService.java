package com.vrv.vap.alarmdeal.business.asset.service;


import com.vrv.vap.alarmdeal.business.asset.enums.OperationTypeEnum;
import com.vrv.vap.alarmdeal.business.asset.model.AssetOperationLog;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.jpa.baseservice.BaseService;

public interface AssetOperationLogService extends BaseService<AssetOperationLog, String> {
	public void addLog(OperationTypeEnum _operationType, AssetVO assetVO);
	public void addLog(OperationTypeEnum _operationType, Asset asset);
}

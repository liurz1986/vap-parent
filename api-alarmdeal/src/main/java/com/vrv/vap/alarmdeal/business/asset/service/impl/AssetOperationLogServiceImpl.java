package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.enums.OperationTypeEnum;
import com.vrv.vap.alarmdeal.business.asset.model.AssetOperationLog;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetOperationLogRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetOperationLogService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AssetOperationLogServiceImpl  extends BaseServiceImpl<AssetOperationLog, String>  implements AssetOperationLogService {

	@Autowired
	private AssetOperationLogRepository assetOperationLogRepository;
	
	@Override
	public BaseRepository<AssetOperationLog, String> getRepository() {
		// TODO Auto-generated method stub
		return assetOperationLogRepository;
	}
	
	
	
	public void addLog(OperationTypeEnum _operationType, AssetVO assetVO) {
		AssetOperationLog log=new AssetOperationLog();
		log.setGuid(UUIDUtils.get32UUID());
		log.setOperateTime(new Date());
		log.setOperationType(_operationType.getCode().toString());
		
		log.setAssetGuid(assetVO.getGuid());
		String description="";
		description+= _operationType.getName()+ "资产成功";
		description+=" , 名称:"+assetVO.getName();
		description+=" ,IP:"+assetVO.getIp();
		description+=",资产ID:"+assetVO.getGuid();
		log.setDescription(description);
		
		
		this.save(log);
	}
	
	public void addLog(OperationTypeEnum _operationType, Asset asset) {
		AssetOperationLog log=new AssetOperationLog();
		log.setGuid(UUIDUtils.get32UUID());
		log.setOperateTime(new Date());
		log.setOperationType(_operationType.getCode().toString());
		
		log.setAssetGuid(asset.getGuid());
		String description="";
		description+= _operationType.getName()+ "资产成功";
		description+=" , 名称:"+asset.getName();
		description+=" ,IP:"+asset.getIp();
		description+=",资产ID:"+asset.getGuid();
		log.setDescription(description);
		
		
		this.save(log);
	}
}

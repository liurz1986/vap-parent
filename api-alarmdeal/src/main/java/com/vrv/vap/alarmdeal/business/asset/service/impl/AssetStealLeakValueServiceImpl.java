package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetStealLeakValue;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetExtendRepository;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetStealLeakValueRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetExtendService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetStealLeakValueService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 这段代码想办法再继续进行抽象
 * @author wd-pc
 *
 */
@Service
public class AssetStealLeakValueServiceImpl extends BaseServiceImpl<AssetStealLeakValue, String> implements AssetStealLeakValueService {

	@Autowired
	private AssetStealLeakValueRepository assetStealLeakValueRepository;
	
	
	@Override
	public AssetStealLeakValueRepository getRepository() {
		return assetStealLeakValueRepository;
	}

	@Override
	public void setAssetValue(List<AssetVO> list) {
		if (list.size()>0){
			for (AssetVO assetVO:list) {
				List<QueryCondition> conditions=new ArrayList<>();
				conditions.add(QueryCondition.eq("ip",assetVO.getIp()));
				List<AssetStealLeakValue> all = findAll(conditions);
				if (all.size()>0){
					assetVO.setAssetStealLeakValue(all.get(0).getStealLeakValue());
				}else {
					assetVO.setAssetStealLeakValue(0);
				}
			}
		}
	}
}

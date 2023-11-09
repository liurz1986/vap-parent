package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetExtendRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetExtendService;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 这段代码想办法再继续进行抽象
 * @author wd-pc
 *
 */
@Service
public class AssetExtendServiceImpl extends BaseServiceImpl<AssetExtend, String> implements AssetExtendService {

	@Autowired
	private AssetExtendRepository assetExtendRepository;
	
	
	@Override
	public AssetExtendRepository getRepository() {
		return assetExtendRepository;
	}


	
	public <T> T deserializationVO(AssetExtend assetExtend, Class<T> tclass){
		if(assetExtend!=null){
			String extendInfos = assetExtend.getExtendInfos();
			Gson gson = new Gson();
			T t = gson.fromJson(extendInfos, tclass);
			return t;
		}
		return null;
	}
}

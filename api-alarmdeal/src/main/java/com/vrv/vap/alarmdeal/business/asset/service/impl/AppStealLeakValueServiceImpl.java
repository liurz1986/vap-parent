package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.model.AppStealLeakValue;
import com.vrv.vap.alarmdeal.business.asset.model.AssetStealLeakValue;
import com.vrv.vap.alarmdeal.business.asset.repository.AppStealLeakValueRepository;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetStealLeakValueRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AppStealLeakValueService;
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
public class AppStealLeakValueServiceImpl extends BaseServiceImpl<AppStealLeakValue, String> implements AppStealLeakValueService {

	@Autowired
	private AppStealLeakValueRepository appStealLeakValueRepository;
	
	
	@Override
	public AppStealLeakValueRepository getRepository() {
		return appStealLeakValueRepository;
	}


}

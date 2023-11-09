package com.vrv.vap.alarmdeal.business.asset.service;


import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetStealLeakValue;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface AssetStealLeakValueService extends BaseService<AssetStealLeakValue, String> {

    void setAssetValue(List<AssetVO> list);
}

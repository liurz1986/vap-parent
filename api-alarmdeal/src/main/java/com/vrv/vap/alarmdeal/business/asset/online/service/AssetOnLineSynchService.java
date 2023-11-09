package com.vrv.vap.alarmdeal.business.asset.online.service;

import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetOnLineVO;

import java.util.List;


public interface AssetOnLineSynchService {

    public void excSynchData(List<AssetOnLineVO> assetOnLineVos);
}

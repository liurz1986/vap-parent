package com.vrv.vap.alarmdeal.business.appsys.service.query;

import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;

import java.util.List;
import java.util.Map;

public interface NetInfoManageQueryService {

    public Map<String, Object> baseInfo();

    public List<AssetStatisticsVO> netInfoType();

    public List<AssetStatisticsVO> secretlevelTotal();

    public List<AssetStatisticsVO> protectLevelTotal();

    public List<AssetStatisticsVO> domainTotal();
}

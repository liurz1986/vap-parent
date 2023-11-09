package com.vrv.vap.monitor.service;

import com.vrv.vap.monitor.entity.AssetStatistic;
import com.vrv.vap.monitor.entity.Monitor2AssetIndicatorView;
import com.vrv.vap.monitor.entity.Monitor2AssetInfo;
import com.vrv.vap.monitor.entity.Monitor2Indicator;
import com.vrv.vap.monitor.entity.Monitor2IndicatorView;
import com.vrv.vap.monitor.model.AssetType;
import com.vrv.vap.monitor.vo.Monitor2AssetIndicatorViewQuery;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;

import java.util.List;
import java.util.Map;

public interface AssetMonitorInfoV2Service extends BaseCommonService<Monitor2AssetInfo> {

    List<Map<String, Object>> selectAssetConnectStatus();

    void updateConnectStatus(Monitor2AssetInfo assetInfo);

    List<Monitor2Indicator> queryIndicatorAll(Query query);

    List<Monitor2IndicatorView> queryIndicatorViewAll(Query query);

    List<Monitor2AssetIndicatorView> queryAssetIndicatorViewAll(Monitor2AssetIndicatorViewQuery query);

    int saveAssetIndicatorViewAll(Monitor2AssetIndicatorView record);

    VData<List<AssetType>> getAssetType();

    VData<List<AssetType>> getMonitorAssetTypeTree();

    VData<AssetStatistic> statisticAssetCount();

    List<Map<String, Object>> selectAssetOnlineTop();
}

package com.vrv.vap.alarmdeal.business.asset.datasync.service;

import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetSyncVO;

import java.util.List;

public interface AssetHandSyncService {
    /**
     * 执行kafka资产数据入库处理入口
     * @param assetSyncVOs
     */
    public void excAssetDataSync(List<AssetSyncVO> assetSyncVOs);
}

package com.vrv.vap.alarmdeal.business.asset.datasync.service;

import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetQueryVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetSyncVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;

import java.util.List;

public interface AssetSyncService {
    /**
     * 执行kafka资产数据入库处理入口
     * @param assetSyncVOs
     */
    public void excAssetDataSync(List<AssetSyncVO> assetSyncVOs);

    /**
     * 判断数据是不是存在(asset表中)
     * 非usb进行ip判断
     * usb进行序列号判断
     * @param ip
     * @param serialNumber
     * @param isUsb
     * @param assets
     * @return
     */
    public AssetQueryVO batchDataExistAsset(String ip,String serialNumber, boolean isUsb,List<AssetQueryVO> assets);


}

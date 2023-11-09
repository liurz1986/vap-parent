package com.vrv.vap.amonitor.service.canvas;

import com.vrv.vap.amonitor.entity.AssetCanvasInfo;
import com.vrv.vap.amonitor.service.BaseCommonService;

public interface CanvasInfoService  extends BaseCommonService<AssetCanvasInfo> {
    int setDefaultItem(AssetCanvasInfo assetCanvasInfo);
}

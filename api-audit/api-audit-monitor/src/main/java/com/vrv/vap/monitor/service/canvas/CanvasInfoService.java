package com.vrv.vap.monitor.service.canvas;

import com.vrv.vap.monitor.entity.AssetCanvasInfo;
import com.vrv.vap.monitor.service.BaseCommonService;

public interface CanvasInfoService  extends BaseCommonService<AssetCanvasInfo> {
    int setDefaultItem(AssetCanvasInfo assetCanvasInfo);
}

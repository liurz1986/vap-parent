package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import lombok.Data;

@Data
public class AssetVerifyCompareVO {
    private AssetVerify assetVerify;
    private String  extendInfos;// 扩展信息 json格式
    private boolean isAsset; // 是不是用正式库覆盖
    private String assetGuid; // 用正式库覆盖情况，正式库资产guid

}

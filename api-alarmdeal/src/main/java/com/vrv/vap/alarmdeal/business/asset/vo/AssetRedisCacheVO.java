package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import lombok.Data;

/**
 * 资产缓存
 * 2022-10-08
 */
@Data
public class AssetRedisCacheVO extends Asset {

    private String groupName;

    private String typeName;

}

package com.vrv.vap.alarmdeal.business.asset.online.vo;

import lombok.Data;

@Data
public class AssetQueryVO {

    private String ip;  // ip

    private String typeGuid; // 二级资产类型guid

    private String typeName; // 二级资产类型的名称
}

package com.vrv.vap.alarmdeal.business.asset.online.vo;

import lombok.Data;

@Data
public class AssetTypeByIpVO {
    private String ip; // ip地址

    private String typeGuid;  // 资产类型guid
}

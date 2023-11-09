package com.vrv.vap.alarmdeal.business.asset.online.vo;

import lombok.Data;


@Data
public class AssetWorthVO {

    private String guid; // 资产guid

    private String name; // 资产名称

    private String ip;  // 资产ip

    private String assetType;

    private String worth;  // 资产价值

    private String orgName;

    private String orgCode;
    private String responsibleName;
    private String responsibleCode;

}

package com.vrv.vap.alarmdeal.business.asset.online.vo;

import lombok.Data;

@Data
public class AssetSearchNewVO {
    private String orgName; // 部门名称

    private String orgCode; // 部门code

    private String guid;// 资产guid

    private String ip; //资产IP

    private String responsibleName; // 责任人名称

    private String responsibleCode;// 责任人code

    private String typeGuid;// 资产类型的guid

}

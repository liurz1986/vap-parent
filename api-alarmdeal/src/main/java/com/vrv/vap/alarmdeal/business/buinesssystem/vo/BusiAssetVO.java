package com.vrv.vap.alarmdeal.business.buinesssystem.vo;

import lombok.Data;

/**
 * 业务系统关联资产信息
 */
@Data
public class BusiAssetVO {

    private String guid; // 资产guid

    private String busiGuid; // 业务系统guid

    private String name; // 资产名称

    private String ip; // 资产ip

    private String typeName; // 资产类型名称

    private String responsibleName; // 资产责任人

    private String orgName;// 单位

    private int assetOrder; // 资产展示的序号




}

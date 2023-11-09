package com.vrv.vap.alarmdeal.business.asset.online.vo;

import lombok.Data;

import java.util.List;

@Data
public class AssetIpByAssetGroupVO {
    private String name;  // 一级资产名称

    private List<String> ips; // ip
}

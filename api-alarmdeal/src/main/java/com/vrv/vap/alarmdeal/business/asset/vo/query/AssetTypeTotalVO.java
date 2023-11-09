package com.vrv.vap.alarmdeal.business.asset.vo.query;

import lombok.Data;

/**
 * 资产分类统计：终端总数$，服务器总数$，网络设备总数$，安全设备总数$，其他设备数$。
 */
@Data
public class AssetTypeTotalVO {

    private int assetHost; // 终端总数
    private int assetService; // 服务器总数
    private int assetSafeDevice; // 安全设备总数
    private int assetNetworkDevice;// 网络设备总数
    private int otherAsset;// 其他设备总数
}

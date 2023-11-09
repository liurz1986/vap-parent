package com.vrv.vap.alarmdeal.business.asset.vo.query;

import lombok.Data;

/**
 * 安全设备信息列表
 */
@Data
public class SafeDeviceListVO {
    private String name; // 设备名称
    private String type; // 设备型号
    private String version; // 设备版本号
    private String companyName;//厂商名称
}

package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年02月21日 10:36
 */
@Data
public class AssetExtendBean {
    // 操作系统类型
    private String extendSystem;

    // 软件系统版本号
    private String sysSno;

    // 设备品牌型号
    private String extendVersionInfo;

    // 硬盘序列号
    private String extendDiskNumber;
}

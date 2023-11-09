package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年06月08日 15:55
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AssetCacheVo {
    private String guid;
    private String ip;
    private String mac;
    private Integer equipmentIntensive;
    private String name;
    private String orgName;
    private String orgCode;
    private String assetType;
    private String responsibleCode;
    private String securityGuid;
}

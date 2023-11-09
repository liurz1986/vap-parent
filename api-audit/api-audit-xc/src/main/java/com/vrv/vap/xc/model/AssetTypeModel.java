package com.vrv.vap.xc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssetTypeModel extends PageModel {
    /**
     * 资产类型code
     */
    private String assetTypeCode;
    /**
     * ip/mac/名称
     */
    private String assetInfo;
    /**
     * 关注资产
     */
    boolean focusAssets;
    /**
     * 窃泄密值开始区间
     */
    private String beginValue;
    /**
     * 窃泄密值结束区间
     */
    private String endValue;
    /**
     * 责任人姓名
     */
    private String responsibleName;
    /**
     * 部门名称
     */
    private String orgName;
    /**
     * 涉密等级 绝密0，机密1，秘密2，内部3，非密4
     */
    private String equipmentIntensive;
    /**
     * 资产类型列表
     */
    private List<String> guidList;
    /**
     * 关注的资产
     */
    private List<String> focusAssetsList;
}

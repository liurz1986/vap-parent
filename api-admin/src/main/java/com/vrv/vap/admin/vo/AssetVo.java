package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.Asset;


public class AssetVo extends Asset {

    private String assetExtendInfo;

    private String groupName;

    private String typeName;

    private String treeCode;

    public String getAssetExtendInfo() {
        return assetExtendInfo;
    }

    public void setAssetExtendInfo(String assetExtendInfo) {
        this.assetExtendInfo = assetExtendInfo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }
}

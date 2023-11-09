package com.vrv.vap.alarmdeal.business.asset.enums;

public enum AssetTrypeGroupEnum {

    ASSETHOSt("assetHost","asset-Host"),
    ASSETSERVICE("assetService","asset-service"),
    ASSETNET("assetNetworkDevice","asset-NetworkDevice"),
    ASSETSAFE("assetSafeDevice","asset-SafeDevice"),
    ASSETMAINTEN("assetMaintenHost","asset-MaintenHost"),
    OTHERASSET("otherAsset","asset-OfficeDevice-WriterMachine,asset-OfficeDevice-printer,asset-USBMemory-classified");


    private String name;
    private String treeCode;
    AssetTrypeGroupEnum(String name ,String treeCode){
        this.name = name;
        this.treeCode=treeCode;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }
}

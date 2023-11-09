package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetExtendVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import lombok.Data;

import java.util.Date;


@Data
public class AssetValidateVO {
    private boolean isExistOld; // 是否存在旧的数据(正对主表)

    private Date osSetuptime; //旧的系统安装时间

    private String osList; //旧的操作系统

    private Asset asset;

    private AssetVerify assetVerify;  // 待审库数据

    private boolean checkSucess; // 校验是否成功

    private String assetGuid ;// 资产的guid

    private Date curOsSetuptime; //当前系统安装时间

    private String assetTypeTreeCode; //二级资产类型treeCode

    private AssetExtend assetExtend; // 扩展信息

    private AssetExtendVerify assetExtendVerify; // 扩展信息
}

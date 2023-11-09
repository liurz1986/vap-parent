package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import lombok.Data;

@Data
public class AssetExportInitDataVO extends Asset {

    private String typeName; // 二级资产名称

    private String osSetuptimeStr; // 操作系统安装时间

}

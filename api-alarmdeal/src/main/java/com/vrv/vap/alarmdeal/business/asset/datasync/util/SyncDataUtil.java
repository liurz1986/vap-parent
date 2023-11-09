package com.vrv.vap.alarmdeal.business.asset.datasync.util;

import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.util.AssetUtil;
import org.apache.commons.lang3.StringUtils;

public class SyncDataUtil {

    public  static void initAsset(Asset asset){
        String ip = asset.getIp();
        // 一些固定值处理
        if (StringUtils.isNotEmpty(ip)) {
            asset.setIpNum(AssetUtil.ip2int(ip));
        }
        asset.setProtocol("");
        asset.setCanMonitor("off");
        asset.setCore(false);
        asset.setHeight(0);
        asset.setCanRCtrl("off");
        asset.setSpecial("off");
        asset.setWorth("1");
        asset.setEmployeeCode1(asset.getResponsibleCode());
        asset.setOrg(asset.getOrgCode());
    }
}

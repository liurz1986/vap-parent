package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.AppSysManager;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BasePersonZjg;
import com.vrv.vap.admin.model.BaseSecurityDomain;
import com.vrv.vap.admin.vo.AppSysManagerVO;
import com.vrv.vap.admin.vo.AssetVo;

public interface NetflowBaseDataService {

   void initBaseData();

   void updatePersonData();

   void updateAssetData();

   void updateAppData();

   void updateOrgData();

   void updateSecData();

   BasePersonZjg fixPersonIpCache(String ip);

   AssetVo fixAssetIpCache(String ip);

   AppSysManager fixAppIpCache(String ip);

   AppSysManager fixAppUrlCache(String url);

   AssetVo fixAppAssetCache(String appNo);

   BaseKoalOrg fixOrgIpCache(String ip);

   BaseSecurityDomain fixSecIpCache(String ip);

   BaseSecurityDomain fixSecCodeCache(String id);
}

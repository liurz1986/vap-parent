package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;

import java.util.List;

public interface AssetBaseDataService {


    /**
     * 获取所有人信息
     * @return
     */
    public List<BasePersonZjg> queryAllPerson();

    /**
     * 获取所有人信息（缓存）
     * @return
     */
    public List<BasePersonZjg> queryAllPersonCache();

    /**
     * 获取所有安全域信息
     */
    public List<BaseSecurityDomain> queryAllDomain();

    /**
     * 获取所有安全域信息(缓存)
     */
    public List<BaseSecurityDomain> queryAllDomainCache();

    /**
     * 获取所有一级资产类型
     * @return
     */
    public List<AssetTypeGroup> queyAllAssetTypeGroup();
    /**
     * 获取所有二级资产类型
     * @return
     */
    public List<AssetType> queryAllAssetType();

    /**
     * 终端资产类型unicode
     */
    public List<String> queryAllAssetHostTypeUnicode();

    /**
     * 获取所有偏好配置信息
     *
     */
    public List<AssetSystemAttributeSettings> queryAllAssetSystemAttributeSetting();


    /**
     * 资产涉密等级
     * @return
     */
    public  List<BaseDictAll> queryAssetSecretLevels();
}

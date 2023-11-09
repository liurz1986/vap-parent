package com.vrv.vap.alarmdeal.business.asset.datasync.service;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifyVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.util.AssetValidateUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSystemAttributeSettingsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 资产校验处理(数据同步用到)
 *
 * 2022-06-03
 */
public abstract  class AssetValidateServiceAbs {
    private static Logger logger= LoggerFactory.getLogger(AssetValidateServiceAbs.class);

    protected List<BasePersonZjg> persons;    // 所有用户

    protected List<BaseSecurityDomain> allDomain;  // 所有安全域

    protected  List<AssetType> assetTypes;  // 所有资产类型

    protected List<AssetTypeGroup> assetTypeGroups;  // 所有一级资产类型

    protected List<String> typeUnicodes ;  // 所有资产类型unicode

    protected List<AssetSystemAttributeSettings> assetSystemAttributeSettings; // 所有偏好配置信息

    protected  List<BaseDictAll>  secretLevels; // 所有涉密等级




    /**
     * 单个资产数据校验
     * 1.必填子段校验
     * 2.格式\重复性校验：IP、mac、序列号
     * 3.有效性校验：责任人、责任单位、安全域、涉密等级校验：code进行校验，校验ok进行名称填充
     * @param asset
     * @param assetGuid
     * @return
     */
   public Result<String> assetDataValidate(Asset asset, String assetGuid){
       String assetTypeGuid = asset.getAssetType();
       if(StringUtils.isEmpty(assetTypeGuid)){
           return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产类型不能为空");
       }
       AssetType assetType = getAssetTypeByGuid(assetTypeGuid);
       if (null == assetType){
           return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产类型不存在");
       }
       asset.setTypeUnicode(assetType.getUniqueCode());
       // 资产类型对应的一级资产类型是否存在
       String treeCode = assetType.getTreeCode();
       int indexTwo = treeCode.lastIndexOf('-');
       String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
       AssetTypeGroup group = getAssetTypeGroup(treeCodeGroup,assetTypeGroups);
       if(null == group){
           return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产类型对应的一级资产类型不存在");
       }
       // 获取偏好配置
       List<CustomSettings> systemAttributeCustomSettings= getSystemAttributeCustomSettings(treeCodeGroup);
       if(null == systemAttributeCustomSettings || systemAttributeCustomSettings.size() ==0){
           return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"偏好配置内容为空，"+treeCodeGroup);
       }
       // 必填子段校验
       Result<String>  validateMustResult =validateMust(asset,systemAttributeCustomSettings);
       if (validateMustResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
           return validateMustResult;
       }
       // 格式校验:IP、mac、序列号
       Result<String>  formatValidateResult =formatValidate(asset);
       if (formatValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
           return formatValidateResult;
       }
       // 重复性校验：IP、mac、序列号
       Result<String>  repeatabilityValidateResult =repeatabilityValidate(asset,assetGuid);
       if (repeatabilityValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
           return repeatabilityValidateResult;
       }
       // 有效性校验：责任人、安全域、涉密等级、国产校验：code进行校验，校验ok进行名称填充
       Result<String>  fvalidityValidateResult =validityValidate(asset);
       if (fvalidityValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
           return fvalidityValidateResult;
       }
       return ResultUtil.success("success");
   }


    private  AssetTypeGroup getAssetTypeGroup(String treeCodeGroup, List<AssetTypeGroup> assetTypeGroups){
       for(AssetTypeGroup group : assetTypeGroups){
           if(treeCodeGroup.equals(group.getTreeCode())){
               return group;
           }
       }
        return null;
    }

    /**
     * 获取资产类型
     * @param assetGuid
     * @return
     */
    protected AssetType getAssetTypeByGuid(String assetGuid) {
        for(AssetType assetType : assetTypes){
            if(assetGuid.equals(assetType.getGuid())){
                return assetType;
            }
        }
        return null;
    }

    /**
     * 获取偏好配置数据
     * @param assetTreeCode
     * @return
     */
    protected List<CustomSettings> getSystemAttributeCustomSettings(String assetTreeCode) {
        List<CustomSettings> systemAttributeCustomSettings=new ArrayList<>();
        List<AssetSystemAttributeSettings> queys = getCycleAssetSystemAttributeSettings(assetTreeCode);
        // 添加系统控件
        for (AssetSystemAttributeSettings attribute : queys) {
            AssetSystemAttributeSettingsVO setting = new AssetSystemAttributeSettingsVO(attribute);
            CustomSettings customSettings = setting.getCustomSettings();
            // 列展示，须是显示的
            if (attribute.getVisible() && customSettings != null) {
                if (customSettings.getChildrenControl() == null || customSettings.getChildrenControl().isEmpty()) {
                    customSettings.setAttributeType("system");
                    systemAttributeCustomSettings.add(customSettings);
                } else {
                    for (CustomSettings child : customSettings.getChildrenControl()) {
                        if (Boolean.TRUE.equals(child.getVisible())) {
                            child.setAttributeType("system");
                            systemAttributeCustomSettings.add(child);
                        }
                    }
                }
            }
        }
        return systemAttributeCustomSettings;
    }

    /**
     * 获取偏好配置数据
     * @param treeCode
     * @return
     */
    private List<AssetSystemAttributeSettings> getCycleAssetSystemAttributeSettings(String treeCode){
        String key="AssetPanels_"+treeCode;
        List<AssetSystemAttributeSettings> queys = new ArrayList<>();
        for(AssetSystemAttributeSettings settings : assetSystemAttributeSettings){
            if(key.equals(settings.getAssetSettingsGuid())){
                queys.add(settings);
            }
        }
        if(queys.size() > 0){
            return queys;
        }
        int index = treeCode.lastIndexOf('-');
        // index为-1时，代表就是最顶层的资产类型了,不用再往上找了
        if(-1 == index){
            for(AssetSystemAttributeSettings settings : assetSystemAttributeSettings){
                if("AssetPanels".equals(settings.getAssetSettingsGuid())){
                    queys.add(settings);
                }
            }
            return queys;
        }
        String groupTreeCode = treeCode.substring(0, index);
        return getCycleAssetSystemAttributeSettings(groupTreeCode);
    }
    /**
     * 格式校验：IP、mac、序列号
     * @param asset
     * @param asset
     * @return
     */
    private Result<String> formatValidate(Asset asset){
        // ip格式格式校验
        Result<String> ipValidate =AssetValidateUtil.ipFormat(asset.getIp());
        if (ipValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return ipValidate;
        }
        // mac格式校验
        Result<String> macValidate = AssetValidateUtil.macFormat(asset.getMac());
        if (macValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return macValidate;
        }
        // 序列号格式(不允许输入汉字和特殊字符)
        return AssetValidateUtil.serialNumberFormat(asset.getSerialNumber());
    }
    /**
     * 重复性校验：IP、mac、序列号
     * @param asset
     * @param assetGuid
     * @return
     */
    private  Result<String> repeatabilityValidate(Asset asset, String assetGuid) {
        // ip唯一性校验
        Result<String>  validateIpResult =validateIp(asset.getIp(),assetGuid);
        if (validateIpResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validateIpResult;
        }
        // mac唯一性校验
        Result<String>  validateMacResult =validateMac(asset.getMac(),assetGuid);
        if (validateMacResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validateMacResult;
        }
        // 序列号唯一性校验
        Result<String>  validateSerialNumberResult =validateSerialNumber(asset.getSerialNumber(),assetGuid);
        if (validateSerialNumberResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validateSerialNumberResult;
        }
        return ResultUtil.success("success");
    }

    /**
     *  有效性校验：责任人、安全域、涉密等级、是否安装终端客、终端类型、国产校验：code进行校验，校验ok进行名称填充
     * @param asset
     * @return
     */
    private Result<String> validityValidate(Asset asset) {
        // 责任人处理
        Result<String>  responsibleCodeValidateResult = responsibleCodeValidate(asset,persons);
        if (responsibleCodeValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return responsibleCodeValidateResult;
        }
        // 安全域处理
        Result<String>  domainCcdeValidateResult =domainCcdeValidate(asset,allDomain);
        if (domainCcdeValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return domainCcdeValidateResult;
        }
        // 涉密等级校验
        Result<String>  classifiedLevelValidateResult =classifiedLevelValidate(asset);
        if (classifiedLevelValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return classifiedLevelValidateResult;
        }
        // 是否国产：1：表示国产 2：非国产
        String termType = asset.getTermType();
        if(StringUtils.isNotEmpty(termType) && !AssetValidateUtil.termTypeCodeValidate(termType)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"是否国产的值不合法,"+termType);
        }
        return ResultUtil.success("success");

    }



    /**
     * 责任人code有效性校验，有效的话，进行责任人名称，组织code。组织名称进行重新填充
     *
     * @param asset
     * @param persons
     * @return
     */
    private Result<String> responsibleCodeValidate(Asset asset, List<BasePersonZjg> persons) {
        String responsibleCode = asset.getResponsibleCode();
        if(StringUtils.isEmpty(responsibleCode)){
            return ResultUtil.success("success");
        }
        if(null == persons || persons.size() ==0 ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"所有用户数据为空");
        }
        BasePersonZjg person = getPersonByCode(responsibleCode, persons);
        if (null == person) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"责任人code不存在，"+responsibleCode);
        }
        // code存在进行name，组织机构信息覆盖处理
        asset.setResponsibleName(person.getUserName());
        asset.setOrgCode(person.getOrgCode());
        asset.setOrgName(person.getOrgName());
        return ResultUtil.success("success");
    }

    /**
     * 涉及等级有效性校验
     *
     * @param asset
     * @return
     */
    private Result<String> classifiedLevelValidate(Asset asset) {
        String equipmentIntensive = asset.getEquipmentIntensive();
        if(StringUtils.isEmpty(equipmentIntensive)){
            return ResultUtil.success("success");
        }
        List<String> codes =getSercretLevelCodes();
        if(!codes.contains(equipmentIntensive.trim())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密等级的值不合法,"+equipmentIntensive);
        }
        return ResultUtil.success("success");
    }

    private List<String> getSercretLevelCodes() {
        List<String> codes = new ArrayList<>();
        for(BaseDictAll data :secretLevels){
            codes.add(data.getCode());
        }
        return codes;
    }


    /**
     * 安全域code校验：存在自动填充名称、subcode
     * @param asset
     * @param allDomain
     * @return
     */
    private Result<String> domainCcdeValidate(Asset asset, List<BaseSecurityDomain> allDomain) {
        String  domainCcde = asset.getSecurityGuid();
        if(StringUtils.isEmpty( domainCcde)){
            return ResultUtil.success("success");
        }
        if(null == allDomain || allDomain.size() ==0 ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"所有安全域数据为空");
        }
        BaseSecurityDomain domain = getDomainByCode(domainCcde,allDomain);
        if(null == domain){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"安全域code不存在，"+domainCcde);
        }
        // 自动填充名称，subcode
        asset.setDomainName(domain.getDomainName());
        asset.setDomainSubCode(domain.getSubCode());
        return ResultUtil.success("success");
    }

    private BaseSecurityDomain getDomainByCode(String domainCcde, List<BaseSecurityDomain> allDomain) {
        for (BaseSecurityDomain domain : allDomain) {
            if (domainCcde.equals(domain.getCode())) {
                return domain;
            }
        }
        return null;
    }

    private BasePersonZjg getPersonByCode(String userNo, List<BasePersonZjg> basePersonZjgList) {
        for (BasePersonZjg zig : basePersonZjgList) {
            if (userNo.equalsIgnoreCase(zig.getUserNo())) {
                return zig;
            }
        }
        return null;
    }


    /**
     * ip地址唯一性校验
     * @param ip
     * @param guid :guid主要是针对编辑时，新增时guid为null
     * @return
     */

    protected abstract Result<String> validateIp(String ip, String guid );

    /**
     * mac地址唯一性校验
     * @param mac
     * @param guid:guid主要是针对编辑时，新增时guid为null
     * @return
     */

    protected abstract Result<String> validateMac(String mac, String guid);

    /**
     * 序列号唯一性校验
     * @param serialNumber
     * @param guid:guid主要是针对编辑时，新增时guid为null
     * @return
     */

    protected abstract Result<String> validateSerialNumber(String serialNumber, String guid);

    /**
     * 根据偏好配置校验必填项
     * @param asset 当前数据
     * @param customSettings
     * @return
     *
     * 终端类型、客户端安装 、操作系统 、操作系统安装时间去掉非必填校验  2023.1.30
     */
    
    public Result<String> validateMust(Asset asset, List<CustomSettings> customSettings) {
        try{
            Field[] fields = asset.getClass().getDeclaredFields();
            fields[0].getName();
            for(CustomSettings cus:customSettings){
                if(!cus.getIsMust()){
                    continue;
                }
                String name = cus.getName();
                if(hostSpecialHandle(name)){  // 终端类型、客户端安装 、操作系统 、操作系统安装时间去掉非必填校验 2023.1.30
                    continue;
                }
                // 特殊处理的: 资产类型、安全域、其中责任人、单位校验code不能为空
                switch (name){
                    case "assetTypeName":
                        name="assetType";
                        break;
                    case "securityName":
                        name="securityGuid";
                        break;
                    case "responsibleName":
                        name="responsibleCode";
                        break;
                    case "orgName":
                        name="orgCode";
                        break;
                }
                Field field = asset.getClass().getDeclaredField(name);
                field.setAccessible(true);
                Object value = field.get(asset);
                if(null == value){
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),cus.getTitle()+"为必填项");
                }
                if(StringUtils.isEmpty(String.valueOf(value))){
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),cus.getTitle()+"为必填项");
                }
            }
        }catch(Exception e){
            logger.error("必填校验异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"必填校验异常");
        }
        return ResultUtil.success("success");
    }

    // 终端类型、客户端安装 、操作系统 、操作系统安装时间去掉非必填校验
    private  boolean hostSpecialHandle(String name){
        switch (name){
            case "isMonitorAgent":  // 是否安装终端客户端 1.已安装；2.未安装
                return true;
            case "osSetuptime": // 操作系统安装时间
                return true;
            case "terminalType":// 终端类型 ： 1. 用户终端,2.运维终端 3.应用服务器
                return true;
            case "osList": // 安装操作系统
                return true;
            default:
                return false;
        }
    }
}

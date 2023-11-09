package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.constant.AssetDataSyncConstant;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetExtendVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetValidateServiceAbs;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.SyncDataUtil;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetValidateVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量资产校验
 *
 * 2022-06-09
 */
@Service
public class BatchAssetValidateServiceImpl extends AssetValidateServiceAbs {
    private static Logger logger= LoggerFactory.getLogger(BatchAssetValidateServiceImpl.class);
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private AssetBaseDataService assetBaseDataCacheService;
    @Autowired
    private MapperUtil mapper;

    private List<Map<String, Object>> assets; // 获取所有资产信息：ip、mac、序列号、二级资产类型treeCode、品牌型号名称、资产guid


    /**
     * 批量数据校验(kakfa自动入库)
     * 1。初始化数据
     * 2. 数据校验
     * @param typeUnicodes
     * @param assetTypes
     * @param assetList
     * @return
     */
    public void batchValidateAssetAutomatic(List<String> typeUnicodes, List<AssetType> assetTypes,List<AssetValidateVO> assetList,List<AssetTypeGroup> assetTypeGroups){
        if(assetList.size() == 0){
            return;
        }
        // 初始化数据
        initBaseDataSynch(typeUnicodes,assetTypes,assetTypeGroups);
        // 批量校验处理
        for(AssetValidateVO data : assetList){
            Asset asset = data.getAsset();
            String treeCode =data.getAssetTypeTreeCode();
            int indexTwo = treeCode.lastIndexOf('-');
            String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
            String guid = null;
            if(data.isExistOld()){
                guid = asset.getGuid();
            }
            Result<String> validateAssetResult = assetDataValidate(asset,guid);
            if (validateAssetResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
                data.getAssetVerify().setSyncMessage(validateAssetResult.getMsg());
                data.getAssetVerify().setSyncStatus(AssetDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
                data.getAssetVerify().setAssetId(null);
                data.getAssetVerify().setTags(treeCodeGroup);
                data.setCheckSucess(false);
            }else{
                data.getAssetVerify().setSyncMessage("入库成功");
                data.getAssetVerify().setSyncStatus(AssetDataSyncConstant.SYNCSTATUSSUCCESS); // 入库成功
                data.setCheckSucess(true);
                data.getAssetVerify().setAssetId(asset.getGuid()); // 关联资产id
                data.getAssetVerify().setTags(treeCodeGroup);
                data.getAsset().setTags(treeCodeGroup);
            }
        }
    }

    /**
     * 初始化数据：界面批量入库
     */
    public void initBaseData() {
        // 获取所有用户
        persons= assetBaseDataCacheService.queryAllPerson();
        // 获取所有安全域
        allDomain = assetBaseDataCacheService.queryAllDomain();
        // 获取所有一级资产类型
        assetTypeGroups = assetBaseDataCacheService.queyAllAssetTypeGroup();
        // 获取所有资产类型
        assetTypes = assetBaseDataCacheService.queryAllAssetType();
        // 所有偏好配置信息
        assetSystemAttributeSettings = assetBaseDataCacheService.queryAllAssetSystemAttributeSetting();
        // 获取所有资产信息：ip、mac、序列号、二级资产类型treeCode
        assets = assetDao.allAssetDataValidata();
        // 终端下所有二级资产类型的uniqueCode
        typeUnicodes = assetBaseDataCacheService.queryAllAssetHostTypeUnicode();
        // 资产涉密等级
        secretLevels = assetBaseDataCacheService.queryAssetSecretLevels();
    }
    /**
     * 初始化数据：kafka数据
     * @param typeUnicodes
     * @param assetTypes
     */
    private void initBaseDataSynch(List<String> typeUnicodes, List<AssetType> assetTypes,List<AssetTypeGroup> assetTypeGroups) {
        this.typeUnicodes = typeUnicodes;
        this.assetTypes = assetTypes;
        this.assetTypeGroups = assetTypeGroups;
        // 获取所有用户
        persons= assetBaseDataCacheService.queryAllPersonCache();
        // 获取所有安全域
        allDomain = assetBaseDataCacheService.queryAllDomainCache();
        // 所有偏好配置信息
        assetSystemAttributeSettings = assetBaseDataCacheService.queryAllAssetSystemAttributeSetting();
        // 获取所有资产信息：ip、mac、序列号、二级资产类型treeCode
        assets = assetDao.allAssetDataValidata();
        // 资产涉密等级
        secretLevels = assetBaseDataCacheService.queryAssetSecretLevels();
    }

    /**
     * 批量数据校验(待审库批量入库)
     * 1。初始化数据
     * 2. 数据校验
     * @param datas
     * @param assets
     * @param assetGuids
     * @return
     */
    public void batchValidateAsset(List<AssetValidateVO> datas, List<Asset> assets, List<String> assetGuids, List<AssetVerify> assetVerifyList,List<AssetExtend> assetExtends){
        // 初始化数据
        initBaseData();
        // 批量校验处理
        AssetExtend assetExtend = null;
        for(AssetValidateVO assetValidateVO : datas){
            AssetVerify assetVerify = assetValidateVO.getAssetVerify();
            String assetGuid= assetVerify.getAssetId();
            Asset asset = mapper.map(assetVerify, Asset.class);
            Result<String> validateAssetResult = assetDataValidate(asset,assetGuid);
            if (validateAssetResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
                assetVerify.setSyncMessage(validateAssetResult.getMsg());
                assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
                assetVerify.setAssetId(null);
                assetValidateVO.setCheckSucess(false);
            }else{
                if(StringUtils.isEmpty(assetGuid)){
                    assetGuid = UUIDUtils.get32UUID();
                }else{
                    assetGuids.add(assetGuid);
                }
                asset.setGuid(assetGuid);
                asset.setCreateTime(new Date());
                SyncDataUtil.initAsset(asset);
                assets.add(asset);
                assetVerify.setSyncMessage("入库成功");
                assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSSUCCESS); // 入库成功
                assetValidateVO.setCheckSucess(true);
                // 组装资产扩展属性 2022-07-12
                addAssetExtend(assetValidateVO,assetExtends,assetGuid);
            }
            assetVerifyList.add(assetVerify);
        }
    }

    /**
     *  组装资产扩展属性 2022-07-12
     * @param assetValidateVO
     * @param assetExtends
     * @param assetGuid
     */
    private void addAssetExtend(AssetValidateVO assetValidateVO, List<AssetExtend> assetExtends,String assetGuid) {
        AssetExtendVerify assetExtendVerify = assetValidateVO.getAssetExtendVerify();
        AssetExtend  assetExtend = new AssetExtend();
        assetExtend.setAssetGuid(assetGuid);
        if(null != assetExtendVerify){
            assetExtend.setExtendInfos(assetExtendVerify.getExtendInfos());
        }else{ //为空处理,资产扩展内容不能为空
            Map<String,String> param = new HashMap<>();
            param.put("guid",assetGuid);
            assetExtend.setExtendInfos(JSON.toJSONString(param));
        }
        assetExtends.add(assetExtend);
    }


    @Override
    protected Result<String> validateIp(String ip, String guid) {
        // 为空不处理
        if (StringUtils.isEmpty(ip)) {
            return ResultUtil.success("true");
        }
        for (Map<String, Object> asset : assets) {
            if (StringUtils.isEmpty(guid)) {
                if (ip.equals(asset.get("ip"))) {
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前ip已经存在");
                }
            }else{ // 数据存在的情况校验:除当前数据外是不是有ip存在
                if (ip.equals(asset.get("ip"))&&!guid.equals(asset.get("guid"))) {
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前ip已经存在");
                }
            }
        }
        return ResultUtil.success("true");
    }

    @Override
    protected Result<String> validateMac(String mac, String guid) {
        // 为空不处理
        if (StringUtils.isEmpty(mac)) {
            return ResultUtil.success("true");
        }
        for (Map<String, Object> asset : assets) {
            if (StringUtils.isEmpty(guid)) {
                if (mac.equals(asset.get("mac"))) {
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前mac已经存在");
                }
            }else{ // 数据存在的情况校验:除当前数据外是不是有mac存在
                if (mac.equals(asset.get("mac"))&&!guid.equals(asset.get("guid"))) {
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前mac已经存在");
                }
            }
        }
        return ResultUtil.success("true");
    }

    @Override
    protected Result<String> validateSerialNumber(String serialNumber, String guid) {
        // 为空不处理
        if (StringUtils.isEmpty(serialNumber)) {
            return ResultUtil.success("true");
        }
        //硬件资产类型中已存在该IP的资产，这是一级类型
        for (Map<String, Object> asset : assets) {
            if (StringUtils.isEmpty(guid)) {
                if (serialNumber.equals(asset.get("serialNumber"))) {
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前序列号已经存在");
                }
            }else{ // 数据存在的情况校验:除当前数据外是不是有mac存在
                if (serialNumber.equals(asset.get("serialNumber"))&&!guid.equals(asset.get("guid"))) {
                    return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前序列号已经存在");
                }
            }
        }
        return ResultUtil.success("true");
    }

}

package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetValidateServiceAbs;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSystemAttributeSettingsService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


/**
 * 资产校验处理(单个数据校验,界面上入库操作)
 *
 * 2022-06-09
 */
@Service
public  class AssetValidateServiceImpl extends AssetValidateServiceAbs {
    private static Logger logger= LoggerFactory.getLogger(AssetValidateServiceImpl.class);
    @Autowired
    private AssetSystemAttributeSettingsService assetSystemAttributeSettingsService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private AssetBaseDataService assetBaseDataCacheService;


    /**
     * 单个资产数据校验
     * 1.必填子段校验
     * 2.格式\重复性校验：IP、mac、序列号
     * 3.有效性校验：责任人、责任单位、安全域、涉密等级校验：code进行校验，校验ok进行名称填充
     * @param asset
     * @param assetGuid 有值表示修改
     * @return
     */
    public Result<String> validateAsset(Asset asset, String assetGuid){
        // 执行数据校验
        initBaseData();
        // 数据校验处理
        return  assetDataValidate(asset,assetGuid);
    }

    private void initBaseData() {
        // 获取所有用户
        persons= assetBaseDataCacheService.queryAllPerson();
        // 获取所有安全域
        allDomain = assetBaseDataCacheService.queryAllDomain();
        // 获取所有资产类型
        assetTypes = assetBaseDataCacheService.queryAllAssetType();
        // 获取所有一级资产类型
        assetTypeGroups = assetBaseDataCacheService.queyAllAssetTypeGroup();
        // 终端下所有二级资产类型的uniqueCode
        typeUnicodes =assetBaseDataCacheService.queryAllAssetHostTypeUnicode();
        // 所有偏好配置信息
        assetSystemAttributeSettings = assetBaseDataCacheService.queryAllAssetSystemAttributeSetting();
        // 资产涉密等级
        secretLevels = assetBaseDataCacheService.queryAssetSecretLevels();
    }

    /**
     * ip地址唯一性校验
     * @param ip
     * @param guid :guid主要是针对编辑时，新增时guid为null
     * @return
     */
    @Override
    protected Result<String> validateIp(String ip, String guid ) {
        if(StringUtils.isEmpty(ip)){
            return ResultUtil.success("true");
        }
        // ip唯一性校验
        List<QueryCondition> queryConditions=new ArrayList<>();
        if(StringUtils.isNotEmpty(guid)){
            queryConditions.add(QueryCondition.notEq("guid",guid));// 不等于当前数据
        }
        queryConditions.add(QueryCondition.eq("ip",ip));
        long countIp = assetService.count(queryConditions);
        if(countIp > 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前ip已经存在");
        }
        return ResultUtil.success("true");
    }

    /**
     * mac地址唯一性校验
     * @param mac
     * @param guid:guid主要是针对编辑时，新增时guid为null
     * @return
     */
    @Override
    protected Result<String> validateMac(String mac, String guid) {
        if(StringUtils.isEmpty(mac)){
            return ResultUtil.success("true");
        }
        // mac唯一性校验
        List<QueryCondition> queryConditions=new ArrayList<>();
        if(StringUtils.isNotEmpty(guid)){
            queryConditions.add(QueryCondition.notEq("guid",guid));// 不等于当前数据
        }
        queryConditions.add(QueryCondition.eq("mac",mac));
        long countIp = assetService.count(queryConditions);
        if(countIp > 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前mac已经存在");
        }
        return ResultUtil.success("true");
    }

    /**
     * 序列号唯一性校验
     * @param serialNumber
     * @param guid:guid主要是针对编辑时，新增时guid为null
     * @return
     */
    @Override
    protected Result<String> validateSerialNumber(String serialNumber, String guid) {
        if(StringUtils.isEmpty(serialNumber)){
            return ResultUtil.success("true");
        }
        // 序列号唯一性校验
        List<QueryCondition> queryConditions=new ArrayList<>();
        if(StringUtils.isNotEmpty(guid)){
            queryConditions.add(QueryCondition.notEq("guid",guid));// 不等于当前数据
        }
        queryConditions.add(QueryCondition.eq("serialNumber",serialNumber));
        long countIp = assetService.count(queryConditions);
        if(countIp > 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前序列号已经存在");
        }
        return ResultUtil.success("true");
    }
}

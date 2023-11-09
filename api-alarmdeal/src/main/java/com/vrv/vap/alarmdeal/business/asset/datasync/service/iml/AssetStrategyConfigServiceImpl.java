package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetStrategyConfigService;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 资产策略配置处理
 * 2022-05-16
 */
@Service
@Transactional
public class AssetStrategyConfigServiceImpl implements AssetStrategyConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetStrategyConfigServiceImpl.class);

    @Autowired
    private TbConfService tbConfService;

    // 资产策略
    private String[] configs={"sync_asset_data_source_order","sync_asset_out_source_order","sync_asset_data_repair_order",
            "sync_asset_data_repair_person_default","sync_asset_data_repair_security_default","sync_asset_data_repair_org_order",
            "sync_asset_data_repair_secrecy","sync_asset_data_local_default"};
    /**
     * 数据补全策略-新增
     *  补全优先级:无能怎么配置，只能按照统一补全进行处理，因为没有现有资产数据
     * @param assetVerify
     * @return
     */
    public  Result<String> supplementData(AssetVerify assetVerify, List<TbConf> tbConfs) {
        try{
            // 责任人数据处理
            responsibleCodeSupplement(assetVerify,tbConfs);
            // 安全域数据处理
            securityGuidSupplement(assetVerify,tbConfs);
            // 涉密等级数据处理
            equipmentIntensiveSupplement(assetVerify,tbConfs);
            // 是否国产处理
            termTypeSupplement(assetVerify,tbConfs);
            return ResultUtil.success("success");
        }catch (Exception e){
            LOGGER.error("数据补全策异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据补全策异常");
        }
    }
    /**
     * 数据补全策略--修改
     * 1.保留guid
     * 2. 补全优先级:现有库信息补全、统一补全
     *     配置为：现有库信息补全,统一补全。先按现有库信息补全进行处理，为空的再按照统一补全进行补全
     *     配置为：统一补全,现有库信息补全。先按统一补全进行处理，为空的再按现有库信息补全进行补全
     * @param assetVerify
     * @return
     * 2023.1.31
     */
    @Override
    public Result<String> supplementDataUpdate(AssetVerify assetVerify, List<TbConf> tbConfs, AssetVerify assetVerifyOld,String dataRepairOrder) {
        // asset标识现在资产库，base标识统一补全
        try {
            assetVerify.setGuid(assetVerifyOld.getGuid());
            String[] orders = dataRepairOrder.split(",");
            String data = orders[0];
            String againData = orders[1];
            if(!("base".equals(data)||"asset".equals(data)||"base".equals(againData)||"asset".equals(againData))){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"补全优先级配置数据值错误");
            }
            // 优选使用补全策略数据
            if ("base".equals(data)) {
                // 使用补全策略进行补全
                supplementData(assetVerify, tbConfs);
                // 为空的按照现在资产库进行补全
                againSupplementData(assetVerify, assetVerifyOld, null, "1");
            }
            // 优先使用现有数据补全
            if ("asset".equals(data)) {
                // 使用现有数据补全
                updateAutomaticDataByOld(assetVerify, assetVerifyOld);
                // 为空的使用补全策略进行补全
                againSupplementData(assetVerify, null, tbConfs, "2");
            }
            return ResultUtil.success("success");
        }catch (Exception e){
            LOGGER.error("数据补全策异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据补全策异常");
        }
    }

    /**
     * 使用现有数据补全
     * @param assetVerify
     * @param assetVerifyOld
     */
    private void updateAutomaticDataByOld(AssetVerify assetVerify, AssetVerify assetVerifyOld) {
        // 组织 orgCode
        assetVerify.setOrgCode(assetVerifyOld.getOrgCode());
        assetVerify.setOrgName(assetVerifyOld.getOrgName());
        // 安全域
        assetVerify.setSecurityGuid(assetVerifyOld.getSecurityGuid());
        assetVerify.setDomainName(assetVerifyOld.getDomainName());
        assetVerify.setDomainSubCode(assetVerifyOld.getDomainSubCode());
        // 责任人
        assetVerify.setResponsibleCode(assetVerifyOld.getResponsibleCode());
        assetVerify.setResponsibleName(assetVerifyOld.getResponsibleName());
        // 涉密等级
        assetVerify.setEquipmentIntensive(assetVerifyOld.getEquipmentIntensive());
        // 是否国产
        assetVerify.setTermType(assetVerifyOld.getTermType());
    }

    /**
     * 再次补全
     * @param assetVerify
     * @param assetVerifyOld
     * @param tbConfs
     * @param type
     */
    private void againSupplementData(AssetVerify assetVerify, AssetVerify assetVerifyOld, List<TbConf> tbConfs, String type) {
        // type为1表示：第一次补全为base，type为1表示第一次为asset
        switch(type){
            case "1":  // 第一次补全为base
                assetAgainSupplementData(assetVerify,assetVerifyOld);
                break;
            case "2":  // 第一次为asset
                baseAgainSupplementData(assetVerify,tbConfs);
                break;
            default:
                break;
        }
    }
    // 第一次补全为base,再次补全，原则时为空补全
    private void assetAgainSupplementData(AssetVerify assetVerify, AssetVerify assetVerifyOld) {
        // 责任人数据处理：责任人、组织机构
        if(StringUtils.isEmpty(assetVerify.getResponsibleCode())){
            assetVerify.setResponsibleCode(assetVerifyOld.getResponsibleCode());
            assetVerify.setResponsibleName(assetVerifyOld.getResponsibleName());
            assetVerify.setOrgCode(assetVerifyOld.getOrgCode());
            assetVerify.setOrgName(assetVerifyOld.getOrgName());
        }
        // 安全域
        if(StringUtils.isEmpty(assetVerify.getSecurityGuid())){
            assetVerify.setSecurityGuid(assetVerifyOld.getSecurityGuid());
            assetVerify.setDomainName(assetVerifyOld.getDomainName());
            assetVerify.setDomainSubCode(assetVerifyOld.getDomainSubCode());
        }
        // 涉密等级
        if(StringUtils.isEmpty(assetVerify.getEquipmentIntensive())){
            assetVerify.setEquipmentIntensive(assetVerifyOld.getEquipmentIntensive());
        }
        // 是否国产
        if(StringUtils.isEmpty(assetVerify.getTermType())){
            assetVerify.setTermType(assetVerifyOld.getTermType());
        }

    }
    // 第一次补全为asset,再次补全，原则时为空补全
    private void baseAgainSupplementData(AssetVerify assetVerify, List<TbConf> tbConfs) {
        // 责任人数据处理：责任人、组织机构
        if(StringUtils.isEmpty(assetVerify.getResponsibleCode())){
            responsibleCodeSupplement(assetVerify,tbConfs);
        }
        // 安全域
        if(StringUtils.isEmpty(assetVerify.getSecurityGuid())){
            securityGuidSupplement(assetVerify,tbConfs);
        }
        // 涉密等级
        if(StringUtils.isEmpty(assetVerify.getEquipmentIntensive())){
            equipmentIntensiveSupplement(assetVerify,tbConfs);
        }
        // 是否国产
        if(StringUtils.isEmpty(assetVerify.getTermType())){
            termTypeSupplement(assetVerify,tbConfs);
        }
    }




    /**
     * 数据来源优选级
     *  现有数据dataSourceType 与存在的数据 dataSourceType  根据配制数据来源优先级进行比对，其中历史的dataSourceType为空，以最高的优先级来处理(目前配制的手动输入为最高)
     * @param currentDataSourceType 当前的数据来源
     * @param oldDataSourceType 历史数据的数据来源
     * @return
     */
    @Override
    public Result<String> dataSourcePriorityStrategy(int currentDataSourceType, int oldDataSourceType, List<TbConf> tbConfs) {
        // 获取配置的数据来源优选级
        String dataSourceOrder =this.syncAssetDataSourceOrder(tbConfs);
        if(StringUtils.isEmpty(dataSourceOrder)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据来源优选级没有配制");
        }
        String[] orders = dataSourceOrder.split(",");
        if(!Arrays.asList(orders).contains(currentDataSourceType+"")){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前数据来源的数据不符合要求:"+currentDataSourceType);
        }
        // 存在的数据数据来源：其中存在的数据数据来源为空默认为手动输入（1、手动录入；2 数据同步；3资产发现）
        String dataSourceTypeOld = "";
        if(oldDataSourceType > 0){
            dataSourceTypeOld = oldDataSourceType+"";
        }else{
            dataSourceTypeOld="1"; //  为空默认为手动输入
        }
        // 当前数据来源为数据同步
        String dataSourceTypeCurent = currentDataSourceType+"";
        int indexOld = getDataIndex(orders,dataSourceTypeOld);
        int indexNew = getDataIndex(orders,dataSourceTypeCurent);
        if(indexNew > indexOld){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据来源优选级不符合要求,数据不处理,当前数据来源为："+dataSourceTypeCurent+";历史数据来源为："+dataSourceTypeOld);
        }
        return ResultUtil.success("success");
    }

    /**
     * 外部数据同步优选级
     *  现有数据syncSource 与存在的数据 syncSource  根据外部同步优先级进行比对,其中历史的syncSource为空，以最高的优先级来处理(目前配制的北信源融一)
     * @param currentSyncSource 当前外部来源信息
     * @param oldtSyncSource  历史外部来源信息
     * @return
     */
    @Override
    public Result<String> outSourcePriorityStrategy(String currentSyncSource, String oldtSyncSource,List<TbConf> tbConfs) {
        String outSource =this.syncAssetOutSourceOrder(tbConfs);
        if(StringUtils.isEmpty(outSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部同步优先级没有配制");
        }
        String[] orders = outSource.split(",");
        if(!Arrays.asList(orders).contains(currentSyncSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前外部数据的数据不符合要求:"+currentSyncSource);
        }
        if(StringUtils.isEmpty(oldtSyncSource)){ // 历史的syncSource为空，以最高的优先级来处理
            oldtSyncSource = orders[0];
        }
        int indexOld = getDataIndex(orders,oldtSyncSource);
        int indexNew = getDataIndex(orders,currentSyncSource);
        if(indexNew > indexOld){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部数据同步优选级不符合要求,数据不处理，当前外部数据同步优选级为："+currentSyncSource+";历史外部数据同步优选级为："+oldtSyncSource);
        }
        return ResultUtil.success("success");
    }
    /**
     * 涉密等级数据补全
     * @param assetVerify
     */
    private void equipmentIntensiveSupplement(AssetVerify assetVerify, List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(assetVerify.getEquipmentIntensive())){
            return ;
        }
        String secrecy = this.syncAssetDataRepairSecrecy(tbConfs);
        if(StringUtils.isEmpty(secrecy)){
            throw new AlarmDealException(-1,"涉密等级没有配制默认值");
        }
        Map<String,Object> map= JSONObject.parseObject(secrecy,Map.class);
        assetVerify.setEquipmentIntensive(String.valueOf(map.get("code")));
        return ;
    }
    /**
     *  是否国产处理
     * @param assetVerify
     * @param tbConfs
     */
    private void termTypeSupplement(AssetVerify assetVerify, List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(assetVerify.getTermType())){
            return ;
        }
        // sync_asset_data_local_default
        String termType = this.syncAssetDataLocalDefault(tbConfs);
        if(StringUtils.isEmpty(termType)){
            throw new AlarmDealException(-1,"是否国产没有配制默认值");
        }
        assetVerify.setTermType(termType);
        return ;
    }




    /**
     * 安全域数据补全
     * @param assetVerify
     */
    private void securityGuidSupplement(AssetVerify assetVerify,List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(assetVerify.getSecurityGuid())){
            return;
        }
        String data = this.syncAssetDataRepairSecurityDefault(tbConfs);
        if(StringUtils.isEmpty(data)){
            throw new AlarmDealException(-1,"安全域没有配制默认值");
        }
        try{
            Map<String,Object> map=JSONObject.parseObject(data,Map.class);
            assetVerify.setSecurityGuid(String.valueOf(map.get("code")));
            assetVerify.setDomainName(String.valueOf(map.get("name")));
            assetVerify.setDomainSubCode(String.valueOf(map.get("subCode")));
        }catch (Exception e){
            LOGGER.error("安全域数据补全异常",e);
            throw new AlarmDealException(-1,"安全域数据补全异常");
        }

    }

    /**
     * 责任人、组织机构数据补全
     * @param assetVerify
     */
    private void responsibleCodeSupplement(AssetVerify assetVerify,List<TbConf> tbConfs) {
        // 责任人code 和组织机构code都不为空，有一个为空采用默认值
        if(StringUtils.isNotEmpty(assetVerify.getResponsibleCode())&&StringUtils.isNotEmpty(assetVerify.getOrgCode())){
            return;
        }
        // {"code":"001",“name”:"白建屏","orgCode":"JG000001",“orgName”:"汉口区保密办"}
        String data = this.syncAssetDataRepairPersonDefault(tbConfs);
        if(StringUtils.isEmpty(data)){
            throw new AlarmDealException(-1,"责任人没有配制默认值");
        }
        try{
            Map<String,Object> map=JSONObject.parseObject(data,Map.class);
            assetVerify.setResponsibleCode(String.valueOf(map.get("code")));
            assetVerify.setEmployeeCode1(String.valueOf(map.get("code")));
            assetVerify.setResponsibleName(String.valueOf(map.get("name")));
            assetVerify.setOrgCode(String.valueOf(map.get("orgCode")));
            assetVerify.setOrgName(String.valueOf(map.get("orgName")));
        }catch (Exception e){
            LOGGER.error("责任人数据补全异常",e);
            throw new AlarmDealException(-1,"责任人数据补全异常");
        }

    }


    private int getDataIndex(String[] orders, String name) {
        for(int i=0;i<orders.length;i++){
            if(orders[i].equals(name)){
                return i;
            }
        }
        return 99;
    }

    // 数据源优先级 ：1、手动录入；2 数据同步；3资产发现(多个数据，逗号分隔，优先级从前往后) 1,2,3  数据逗号分隔
    private String syncAssetDataSourceOrder(List<TbConf> tbConfs){
        return getAssetStrateryValue(tbConfs,"sync_asset_data_source_order");
    }

    // 外部同步优先级 ：bxy-ry,bxy-zr,bxy-zs //北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs(多个数据，逗号分隔，优先级从前往后)
    private String syncAssetOutSourceOrder(List<TbConf> tbConfs){
        return getAssetStrateryValue(tbConfs,"sync_asset_out_source_order");

    }
    /**
     *  补全优先级:现有库信息补全、统一补全
     * 配置为：现有库信息补全,统一补全。先按现有库信息补全进行处理，为空的再按照统一补全进行补全
     * 配置为：统一补全,现有库信息补全。先按统一补全进行处理，为空的再按现有库信息补全进行补全
     * @param tbConfs
     * @return
     */
    @Override
    public String syncAssetDataRepairOrder(List<TbConf> tbConfs){
        return getAssetStrateryValue(tbConfs,"sync_asset_data_repair_order");
    }
    // 默认责任人及组织机构 {"code":"001",“name”:"白建屏","orgCode":"JG000001",“orgName”:"汉口区保密办"}数据json格式
    private String syncAssetDataRepairPersonDefault(List<TbConf> tbConfs){
        return getAssetStrateryValue(tbConfs,"sync_asset_data_repair_person_default");
    }
    // 安全域  {"code":"cb99e054-ea3b-45c3-a43c-e1989bac5b05",“name”:"用户域","subCode":"001005"}  数据json格式
    private String syncAssetDataRepairSecurityDefault(List<TbConf> tbConfs){
        return getAssetStrateryValue(tbConfs,"sync_asset_data_repair_security_default");
    }

    // 涉密等级  {"code":"1","name":"机密"}  数据json格式
    private String syncAssetDataRepairSecrecy(List<TbConf> tbConfs){
        return getAssetStrateryValue(tbConfs,"sync_asset_data_repair_secrecy");
    }
    // 是否国产配置 sync_asset_data_local_default     1：国产 2：非国产
    private String syncAssetDataLocalDefault(List<TbConf> tbConfs) {
        return getAssetStrateryValue(tbConfs,"sync_asset_data_local_default");
    }


    private String getAssetStrateryValue(List<TbConf> datas ,String key){
        for(TbConf tbConf : datas){
            if(key.equals(tbConf.getKey())){
                return tbConf.getValue();
            }
        }
        return  null;
    }

    /**
     * 更新策略配置
     * @param tbConfS
     */
    @Override
    public void updateStrategyConfig(List<TbConf> tbConfS){
        tbConfService.save(tbConfS);
    }

    /**
     * 获取现有资产策略配置
     */
    @Override
    public List<TbConf> getAssetStrategyConfigs(){
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("key",configs));
        List<TbConf> datas = tbConfService.findAll(conditions);
        return datas;
    }


}

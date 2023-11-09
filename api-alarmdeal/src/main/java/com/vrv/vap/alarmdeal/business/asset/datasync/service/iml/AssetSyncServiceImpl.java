package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.constant.AssetDataSyncConstant;
import com.vrv.vap.alarmdeal.business.asset.datasync.dao.AssetSyncDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetExtendVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.*;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.SyncDataUtil;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetQueryVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetSyncVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetValidateVO;
import com.vrv.vap.alarmdeal.business.asset.model.*;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.service.impl.AssetAlarmServiceImpl;
import com.vrv.vap.alarmdeal.business.asset.util.AssetValidateUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.AlarmEventMsgVO;
import com.vrv.vap.alarmdeal.business.asset.vo.TerminalAssteInstallTimeJobVO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * 外部资产同步数据处理
 * 2022-06-01
 */
@Service
@Transactional
public class AssetSyncServiceImpl implements AssetSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetSyncServiceImpl.class);
    @Autowired
    private AssetStrategyConfigService strategyConfigService;

    @Autowired
    private AssetVerifyService assetVerifyService;

    @Autowired
    private BatchAssetValidateServiceImpl batchAssetValidateServiceImpl;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetExtendService assetExtendService;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private AssetBaseDataService assetBaseDataCacheService;
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private AssetSyncDao assetSyncDao;
    @Autowired
    private TerminalAssteInstallTimeService terminalAssteInstallTimeService;
    @Autowired
    private AssetAlarmService assetAlarmService;

    @Autowired
    private AssetExtendVerifyService assetExtendVerifyService;

    @Autowired
    private TerminalAssetInstallService terminalAssetInstallService;

    @Autowired
    private BaseDataRedisCacheService baseDataRedisCacheService;



    // 获取所有一级资产类型
    List<AssetTypeGroup> assetTypeGroups = null;
    // 获取所有二级资产类型
    List<AssetType> assetTypes = null;
    // 获取所有二级终端资产类型Unicode
    List<String> hostTypeUnicodes = null;
    // 获取所有策略配置信息
    List<TbConf> tbConfs = null;
    // 获取所有资产信息：ip、mac、序列号、二级资产类型guid、资产guid、组织机构、责任人、安全域、涉密等级、数据来源、外部数据来源
    private List<AssetQueryVO> assets = null;
    // 获取所有资产待申表信息：ip、mac、序列号、二级资产类型guid、资产guid、组织机构、责任人、安全域、涉密等级、数据来源、外部数据来源
    private List<AssetQueryVO> assetVerifys = null;
    // 获取所有系统安装时间
    private List<TerminalAssteInstallTime> terminals =null;


    private List<AssetValidateVO> assetList = new ArrayList<>();  //资产表保存数据

    /**
     *  批量处理kafka资产数据入口
     *  去掉手动入库全为自动入库  ---20230403
     * @param assetSyncVos
     */
    @Override
    public void excAssetDataSync(List<AssetSyncVO> assetSyncVos){
        LOGGER.debug("=========同步外部资产数据开始=======");
        try{
            LOGGER.warn("同步外部资产数据开始处理的数量："+assetSyncVos.size());
            // 初始化数据
            initData();
            // 基础校验处理
            List<AssetSyncVO> assetSyncs = getBaseValidateSuccess(assetSyncVos);
            if(assetSyncs.size() == 0){
                return;
            }
            LOGGER.warn("同步外部资产数据基础校验处理完后的数量："+assetSyncs.size());
            //数据去重处理（USB类型序列号，非USBip地址，重复的取最后的一个，前面的抛弃掉）
            List<AssetSyncVO> duplicateDatas =duplicateDatahandle(assetSyncs);
            // 自动补全，构造数据
            constructDataAndAutomatic(duplicateDatas,this.tbConfs);
            // 入库方式为自动入库，校验数据，校验成功入主库，校验失败，待申表上记录失败原因
            batchValidateHandle();
            // 数据入库处理：入待审表、主表、系统安装时间、csv文件、发kafka消息
            LOGGER.warn("同步外部资产数据处理完入库数量："+assetList.size());
            dataWarehousingHandle();
            LOGGER.debug("=========同步外部资产数据结束=======");
        }catch (Exception e){
            LOGGER.error("同步外部资产数据异常",e);
        }
        assetSyncVos.clear();
    }

    /**
     * 自动补全，构造数据
     * @param datas
     * @param tbConfs
     */
    private void constructDataAndAutomatic(List<AssetSyncVO> datas, List<TbConf> tbConfs) {
        for(AssetSyncVO assetSyncVO : datas){
            //判断数据是不是存在
            Map<String, Object> asset = dataExist(assetSyncVO,assetSyncVO.isUsb());
            // 自动补全，构造数据
            Result<String> result = excAssetDataHandle(asset,assetSyncVO,tbConfs);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
                LOGGER.error("资产自动补全，构造数据异常："+result.getMsg());
            }
        }
    }


    /**
     * 初始化数据
     */
    private void initData() {
        // 获取所有一级资产类型
        assetTypeGroups = assetBaseDataCacheService.queyAllAssetTypeGroup();
        // 获取所有二级资产类型
        assetTypes = assetBaseDataCacheService.queryAllAssetType();
        // 获取所有二级终端资产类型Unicode
        hostTypeUnicodes = assetBaseDataCacheService.queryAllAssetHostTypeUnicode();
        // 获取所有策略配置信息
        tbConfs =strategyConfigService.getAssetStrategyConfigs();
        // 获取所有资产信息：ip、mac、序列号、二级资产类型guid、资产guid、组织机构、责任人、安全域、涉密等级、系统安装时间、数据来源、外部数据来源、是否国产、终端类型、客户端安装、操作系统、操作系统安装时间
        assets = assetDao.getAllAssetSync();
        // 获取所有资产待申表信息：ip、mac、序列号、二级资产类型guid、资产guid、组织机构、责任人、安全域、涉密等级、数据来源、外部数据来源、是否国产、终端类型、客户端安装、操作系统、操作系统安装时间
        assetVerifys = assetSyncDao.getAllAssetVerifySync();
        // 获取所有系统安装时间
        terminals = terminalAssteInstallTimeService.findAll();
        assetList = new ArrayList<>();  //资产表保存数据
    }

    /**
     * 执行基础校验：不符合数据不处理
     *  1. 资产类型guid必须、有效性
     *  2. 数据来源类型必填、有效性
     *  3. 外部数据来源必填、有效性
     *  4. 非USB类型ip不能为空
     *  5. USB类型序列号不能为空
     * @param assetSyncVos
     * @return
     */
    private List<AssetSyncVO> getBaseValidateSuccess(List<AssetSyncVO> assetSyncVos) {
        List<AssetSyncVO> validateSuccess = new ArrayList<>();
        for(AssetSyncVO assetSyncVO : assetSyncVos){
            Result<String> validateResult = assetDataValidate(assetSyncVO);
            if(ResultCodeEnum.SUCCESS.getCode().equals(validateResult.getCode())){
                validateSuccess.add(assetSyncVO);
            }else{ // 校验失败数据，信息日志数据
                LOGGER.error("资产数据同步校验失败原因:{}",validateResult.getMsg());
            }
        }
        return validateSuccess;
    }


    /**
     *  数据去重处理（USB类型序列号，非USBip地址，重复的取最后的一个，前面的抛弃掉）
     *  队列是先进先出
     * @param assetSyncVos
     */
    private List<AssetSyncVO> duplicateDatahandle(List<AssetSyncVO> assetSyncVos) {
        // 获取usb或非usb数据
        List<AssetSyncVO> usbDatas = new ArrayList<>();
        List<AssetSyncVO> noUsbDatas = new ArrayList<>();
        for(AssetSyncVO data : assetSyncVos){
           if(data.isUsb()){
               usbDatas.add(data);
           }else{
               noUsbDatas.add(data);
           }
        }
        List<AssetSyncVO> usbDataCopys = new ArrayList<>();
        List<AssetSyncVO> noUsbDataCopys = new ArrayList<>();
        usbDataCopys.addAll(usbDatas);
        noUsbDataCopys.addAll(noUsbDatas);
        List<AssetSyncVO> datas = new ArrayList<>();
        //usb去重处理
        List<AssetSyncVO> usbDataSucess = usbDuplicateDatahandle(usbDatas,usbDataCopys);
        if(usbDataSucess.size() > 0){
            datas.addAll(usbDataSucess);
        }
        //非usb去重处理
        List<AssetSyncVO> nousbDataSucess = noUsbDuplicateDatahandle(noUsbDatas,noUsbDataCopys);
        if(nousbDataSucess.size() > 0){
            datas.addAll(nousbDataSucess);
        }
        return  datas;
    }



    /**
     * usb去重处理 :序列号去重处理,取最后一个重复的作为有效的数据
     * @param usbDatas
     * @param usbDataCopys
     * @return
     */
    private List<AssetSyncVO> usbDuplicateDatahandle(List<AssetSyncVO> usbDatas, List<AssetSyncVO> usbDataCopys) {
        if(usbDatas.size() == 0){
            return new ArrayList<>();
        }
        List<AssetSyncVO> delDatas = new ArrayList<>();
        List<String> serialNumbers = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        for(AssetSyncVO assetSych : usbDatas){
            String serialNumber = assetSych.getSerialNumber();
            if(serialNumbers.contains(serialNumber)){   // 表示存在相同序列号，已经处理过，不处理
                continue;
            }
            serialNumbers.add(serialNumber);
            usbDataCopys.remove(assetSych); // 移除当前的
            List<AssetSyncVO> datas = getSerialNumberDuplicateDatas(serialNumber,usbDataCopys);
            if(null != datas && datas.size() > 0){
                // 获取最后一个作为有效数据，其他全部删除处理
                usbDataCopys.removeAll(datas); // 移除重复的数据
                datas.remove(datas.get(datas.size()-1)); // 移除最后一个，其他作为删除
                delDatas.add(assetSych); // 删除当前数据
                delDatas.addAll(datas);  // 删除重复的(最后一个除外)
                result.put(serialNumber,datas.size());
            }
        }
        if(delDatas.size() > 0){
            usbDatas.removeAll(delDatas);
            LOGGER.debug("usb去重处理,序列号重复数据："+JSON.toJSONString(result));
        }
        return  usbDatas;
    }

    /**
     * 非usb去重处理：ip重复，取最后一个重复的作为有效的数据
     * @param noUsbDatas
     * @param noUsbDataCopys
     * @return
     */
    private List<AssetSyncVO> noUsbDuplicateDatahandle(List<AssetSyncVO> noUsbDatas, List<AssetSyncVO> noUsbDataCopys) {
        if(noUsbDatas.size() == 0){
            return new ArrayList<>();
        }
        List<AssetSyncVO> delDatas = new ArrayList<>();
        List<String> ips = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        for(AssetSyncVO assetSych : noUsbDatas){
            String ip = assetSych.getIp();
            if(ips.contains(ip)){   // 表示存在相同ip，已经处理过，不处理
                continue;
            }
            ips.add(ip);
            noUsbDataCopys.remove(assetSych); // 移除当前的
            List<AssetSyncVO> datas = getIpDuplicateDatas(ip,noUsbDataCopys);
            if(null != datas && datas.size() > 0){
                // 获取最后一个作为有效数据，其他全部删除处理
                noUsbDataCopys.removeAll(datas); // 移除重复的数据
                datas.remove(datas.get(datas.size()-1)); // 移除最后一个，其他作为删除
                delDatas.add(assetSych); // 删除当前数据
                delDatas.addAll(datas);  // 删除重复的(最后一个除外)
                result.put(ip,datas.size());
            }
        }
        if(delDatas.size() > 0){
            noUsbDatas.removeAll(delDatas);
            LOGGER.debug("非usb去重处理,ip重复数据："+ JSON.toJSONString(result));
        }
        return  noUsbDatas;
    }



    private List<AssetSyncVO> getSerialNumberDuplicateDatas(String serialNumber, List<AssetSyncVO> usbDataCopys) {
        if(usbDataCopys.size() == 0){
            return null;
        }
        List<AssetSyncVO> datas = new ArrayList<>();
        for(AssetSyncVO assetSych : usbDataCopys){
            if(serialNumber.equals(assetSych.getSerialNumber())){
                datas.add(assetSych);
            }
        }
        return datas;
    }

    private List<AssetSyncVO> getIpDuplicateDatas(String ip, List<AssetSyncVO> noUsbDataCopys) {
        if(noUsbDataCopys.size() == 0){
            return null;
        }
        List<AssetSyncVO> datas = new ArrayList<>();
        for(AssetSyncVO assetSych : noUsbDataCopys){
            if(ip.equals(assetSych.getIp())){
                datas.add(assetSych);
            }
        }
        return datas;
    }


    /**
     * 入库方式为自动入库，校验数据，校验成功入主库，校验失败，待申表上记录失败原因
     */
    private void batchValidateHandle() {
        // 执行数据校验
        batchAssetValidateServiceImpl.batchValidateAssetAutomatic(hostTypeUnicodes,assetTypes,assetList,assetTypeGroups);
    }

    /**
     * 数据入库处理：入待审表、主表、资产变动触发告警事件、终端设置统计审计客户端安装情况、全量更新资产相关缓存、入扩展信息
     */
    private void dataWarehousingHandle() {
        if(assetList.size() == 0){
            return;
        }
        // 数据处理
        List<AssetVerify> verifies = new ArrayList<>();
        List<AssetExtendVerify> verifyExtends = new ArrayList<>();
        List<Asset> assets = new ArrayList<>();
        List<AssetExtend> assetExtends = new ArrayList<>();
        List<AssetValidateVO> terms = new ArrayList<>();
        List<AlarmEventMsgVO> eventMsgs = new ArrayList<>();
        for(AssetValidateVO data : assetList){
            verifies.add(data.getAssetVerify());
            verifyExtends.add(data.getAssetExtendVerify());
            if(data.isCheckSucess()){
                assets.add(data.getAsset());
                assetExtends.add(data.getAssetExtend());
                terms.add(data);
                eventMsgs.add(getEventMsg(data));
            }
        }
        // 入待审表、
        if(verifies.size() > 0){
            assetVerifyService.save(verifies);
        }
        // 扩展信息待审表 2022-07-12
        if(verifyExtends.size() > 0){
            assetExtendVerifyService.save(verifyExtends);
        }
        // 入主表
        if(assets.size() > 0){
            assetService.save(assets);
            // 资产变动触发告警事件 2022-07-11
            assetAlarmService.assetChangeSendAlarmEvnets(eventMsgs);
            // 终端设置统计审计客户端安装情况 2022-07-13
            terminalAssetInstallService.sendCountKafkaMsg();
            // 全量更新资产相关缓存 2022-08-09
            baseDataRedisCacheService.updateAllAssetCache();
            // 更新操作系统安装时间
            terminalAssteInstallTimeAddQue();
        }
        // 入扩展信息  2022-07-12
        if(assetExtends.size() > 0){
            assetExtendService.save(assetExtends);
        }
    }

    private void terminalAssteInstallTimeAddQue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    TerminalAssteInstallTimeJobVO terminalAssteInstallTimeJobVO = new TerminalAssteInstallTimeJobVO();
                    terminalAssteInstallTimeJobVO.setType("4");
                    terminalAssteInstallTimeService.excTerminalAssteInstallTime(terminalAssteInstallTimeJobVO);
                }catch(Exception e){
                    LOGGER.error("操作系统安装时间处理异常",e);
                }
            }
        }).start();

    }

    private AlarmEventMsgVO getEventMsg(AssetValidateVO data) {
        AlarmEventMsgVO event = new AlarmEventMsgVO();
        if(data.isExistOld()){
            event.setOsType(AssetAlarmServiceImpl.OSTYPEEDIT);
        }else{
            event.setOsType(AssetAlarmServiceImpl.OSTYPESAVE);
        }
        event.setSyncSource(data.getAsset().getSyncSource());
        event.setTypeTreeCode(data.getAssetTypeTreeCode());
        event.setIp(data.getAsset().getIp());
        event.setOsSetuptime(data.getAsset().getOsSetuptime());
        event.setOsList(data.getAsset().getOsList());
        event.setOsSetuptimeOld(data.getOsSetuptime());
        event.setOsListOld(data.getOsList());
        return event;
    }

    /**
     * 基础校验：不符合数据不处理
     *  1. 资产类型guid必须、有效性
     *  2. 数据来源类型必填、有效性
     *  3. 外部数据来源必填、有效性
     *  4. 非USB类型ip不能为空
     *  5. USB类型序列号不能为空
     * @param assetSyncVO
     * @return
     */
    private Result<String> assetDataValidate(AssetSyncVO assetSyncVO) {
        //根据typeGuid区分资产类型
        String typeGuid = assetSyncVO.getTypeGuid();
        if(StringUtils.isEmpty(typeGuid)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"typeGuid为空数据不处理");
        }
        // 获取二级资产类型
        AssetType assetType =getAssetTypeByGuid(typeGuid);
        if(null == assetType){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"typeGuid对应的资产类型不存在");
        }
        assetSyncVO.setTypeUnicode(assetType.getUniqueCode()); //自动填充
        assetSyncVO.setAssetType(assetType); // 后面校验用
        // 数据来源类型
        int dataSourceType = assetSyncVO.getDataSourceType();
        // 获取配置的数据来源优选级
        String dataSourceConfig = getStrategyConfigValueByKey(tbConfs,"sync_asset_data_source_order");
        if(StringUtils.isEmpty(dataSourceConfig)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据来源优选级没有配制");
        }
        String[] orders = dataSourceConfig.split(",");
        if(!Arrays.asList(orders).contains(dataSourceType+"")){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前数据来源的数据不符合要求:"+dataSourceType);
        }
        // 外部来源信息
        String syncSource = assetSyncVO.getSyncSource();
        if(StringUtils.isEmpty(syncSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部来源信息不能为空");
        }
        String outSource = getStrategyConfigValueByKey(tbConfs,"sync_asset_out_source_order");
        if(StringUtils.isEmpty(outSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部同步优先级没有配制");
        }
        String[] outOrders = outSource.split(",");
        if(!Arrays.asList(outOrders).contains(syncSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前外部来源信息不符合要求:"+syncSource);
        }
        assetSyncVO.setUsb(AssetValidateUtil.isUsb(assetType.getTreeCode()));  // 是不是usb校验
        // 数据是否为空处理：ip、序列号为空判断
        Result<Boolean> isHandle = isEmptyHandle(assetSyncVO.isUsb(),assetSyncVO);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(isHandle.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),isHandle.getMsg());
        }
        return ResultUtil.success("success");
    }

    /**
     * 获取资产类型
     * @param assetGuid
     * @return
     */
    private AssetType getAssetTypeByGuid(String assetGuid) {
        for(AssetType assetType : assetTypes){
            if(assetGuid.equals(assetType.getGuid())){
                return assetType;
            }
        }
        return null;
    }

    private String getStrategyConfigValueByKey(List<TbConf> tbConfs, String key) {
        for(TbConf conf : tbConfs){
            if(key.equals(conf.getKey())){
                return conf.getValue();
            }
        }
        return "";
    }


    /**
     * 自动补全，构造数据
     * 1. 数据存在的，
     *    kafka数据覆盖存在数据，保留原本guid
     *    优先级策略处理
     *    数据覆盖处理(要看是否配制覆盖选项)
     *    自动补全处理
     * 2。数据不存在，kafka数据直接新增处理
     *     自动补全处理
     * @param asset
     * @param assetSyncVO
     */
    private Result<String> excAssetDataHandle(Map<String, Object> asset, AssetSyncVO assetSyncVO, List<TbConf> tbConfs) {
        String status =String.valueOf(asset.get("status"));
        if ("error".equals(status)){
            // 数据不存在处理
            return assetNewHandle(assetSyncVO,assetSyncVO.getAssetType(),tbConfs);
        }else{
            // 数据存在处理
            return assetUpdateHandle(asset,assetSyncVO,assetSyncVO.getAssetType(),tbConfs);
        }
    }



    /**
     * 新增数据
     *      自动补全处理
     * @param assetSyncVO
     * @param assetType
     */
    private Result<String> assetNewHandle(AssetSyncVO assetSyncVO, AssetType assetType, List<TbConf> tbConfs) {
        // 组装数据
        AssetVerify assetVerify = mapper.map(assetSyncVO, AssetVerify.class);
        assetVerify = getAssetVerify(assetVerify,assetType);
        // 自动补全处理
        Result<String> result = dataAutomaticCompletion(assetVerify,true,null,null,tbConfs,assetType);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),result.getMsg());
        }
        // 入审核库
        assetVerify.setType(getAssetVerifyType(assetType));
        // 新增场景构造数据
        constructDataNew(assetVerify,assetType,assetSyncVO.getExtendInfos());
        return  ResultUtil.success("success");
    }



    /**
     * 获取待申表type的值：一级资产类型名称-二级资产类型名称
     * @param assetType
     * @return
     */
    public String getAssetVerifyType(AssetType assetType) {
        String assetTypeName= assetType.getName();
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        AssetTypeGroup assetTypeGroup = getAssetTypeGroupByTreeCode(treeCodeGroup,assetTypeGroups);
        String assetTypeGroupName ="未知";
        if(null != assetTypeGroup){
            assetTypeGroupName = assetTypeGroup.getName();
        }
        String type = assetTypeGroupName+"-"+assetTypeName;
        return type;
    }

    private AssetTypeGroup getAssetTypeGroupByTreeCode(String treeCodeGroup, List<AssetTypeGroup> assetTypeGroups) {
        for(AssetTypeGroup group : assetTypeGroups){
            if(treeCodeGroup.equals(group.getTreeCode())){
                return group;
            }
        }
        return  null;
    }

    /**
     * 数据存在执行数据
     *  分asset表中存在 、待审表中存在
     *    kafka数据覆盖存在数据，保留原本guid
     *    优先级策略处理
     *    数据覆盖处理(要看是否配制覆盖选项)
     *    自动补全处理
     *    入待审库
     *    入库方式为自动入库，校验数据，校验成功入主库，校验失败，待申表上记录失败原因
     * @param asset 历史数据
     * @param assetSyncVO kafka 数据
     * @param assetType
     */
    private Result<String> assetUpdateHandle(Map<String, Object> asset, AssetSyncVO assetSyncVO, AssetType assetType, List<TbConf> tbConfs) {
        // kafka数据
        AssetVerify assetVerify = mapper.map(assetSyncVO, AssetVerify.class);
        String type =String.valueOf(asset.get("type"));
        AssetVerify assetVerifyOld = (AssetVerify)asset.get("data");
        // 数据自动补全处理
        Result<String> result = dataAutomaticCompletion(assetVerify,false,assetVerifyOld,type,tbConfs,assetType);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),result.getMsg());
        }
        String assetGuid = assetVerify.getGuid();
        Date oldOsSetuptime = assetVerifyOld.getOsSetuptime();
        String extendInfos = assetSyncVO.getExtendInfos();
        // 三种场景：主表中存在，待审表不存在；主表中不存在，待审表存在；主表、待审表同时存在
        if("1".equals(type)){ // 主表中存在，待审表不存在：kafka数据覆盖主表 ，保留guid，新增待审表
            AssetVerify assetVerifyNew = getAssetVerify(assetVerify,assetType);
            assetVerifyNew.setType(getAssetVerifyType(assetType));
            // 修改场景构造数据
            constructDataUpdate(assetGuid,assetVerifyNew,oldOsSetuptime,assetType,extendInfos);
        } else if("2".equals(type)){ //主表中不存在，待审表存在：kafka数据覆盖待审表，保留guid，新增主表
            AssetVerify assetVerifyUpdate =getAssetVerifyUpdate(assetVerify,assetType,assetGuid);
            assetVerifyUpdate.setType(getAssetVerifyType(assetType));
            // 新增场景构造数据
            constructDataNew(assetVerify,assetType,extendInfos);
        }else{ // 主表、待审表同时存在：kafka数据覆盖待审表、主表，保留guid
            AssetVerify assetVOld = (AssetVerify)asset.get("assetVerify");  // 待审表
            AssetVerify assetVerifyUpdate =getAssetVerifyUpdate(assetVerify,assetType,assetVOld.getGuid());
            assetVerifyUpdate.setType(getAssetVerifyType(assetType));
            // 修改场景构造数据
            constructDataUpdate(assetGuid,assetVerifyUpdate,oldOsSetuptime,assetType,extendInfos);
        }
        return ResultUtil.success("success");
    }


    /**
     * 新增场景构造数据
     * @param newData
     * @param assetType 资产类型
     * @param extendInfos 扩展信息
     */
    private void constructDataNew(AssetVerify newData, AssetType assetType,String extendInfos) {
        AssetValidateVO assetValidateVO = new AssetValidateVO();
        assetValidateVO.setAssetVerify(newData);
        String assetGuid = UUIDUtils.get32UUID();
        Asset asset = mapper.map(newData, Asset.class);
        asset.setGuid(assetGuid);
        asset.setCreateTime(new Date());
        SyncDataUtil.initAsset(asset);
        assetValidateVO.setAsset(asset);
        assetValidateVO.setExistOld(false);
        assetValidateVO.setAssetTypeTreeCode(assetType.getTreeCode());
        assetValidateVO.setAssetExtend(getAssetExtend(extendInfos,assetGuid));
        assetValidateVO.setAssetExtendVerify(getAssetExtendVerify(extendInfos,newData.getGuid()));
        assetList.add(assetValidateVO);
    }

    private AssetExtendVerify getAssetExtendVerify(String extendInfos, String guid) {
        AssetExtendVerify verify = new AssetExtendVerify();
        verify.setAssetGuid(guid);
        //为空处理,资产扩展内容不能为空
        if(StringUtils.isEmpty(extendInfos)){
            Map<String,String> param = new HashMap<>();
            param.put("guid",guid);
            verify.setExtendInfos(JSON.toJSONString(param));
        }else{
            verify.setExtendInfos(extendInfos);
        }
        return verify;
    }

    private AssetExtend getAssetExtend(String extendInfos, String assetGuid) {
        AssetExtend assetExtend = new AssetExtend();
        assetExtend.setAssetGuid(assetGuid);
        //为空处理,资产扩展内容不能为空
        if(StringUtils.isEmpty(extendInfos)){
            Map<String,String> param = new HashMap<>();
            param.put("guid",assetGuid);
            assetExtend.setExtendInfos(JSON.toJSONString(param));
        }else{
            assetExtend.setExtendInfos(extendInfos);
        }
        return assetExtend;
    }

    /**
     * 修改场景构造数据
     * @param assetGuid
     * @param assetVerify
     * @param osSetuptime
     */
    private void constructDataUpdate(String assetGuid,AssetVerify assetVerify,Date osSetuptime,AssetType assetType,String extendInfos) {
        AssetValidateVO assetValidateVO = new AssetValidateVO();
        Asset asset= mapper.map(assetVerify, Asset.class);
        asset.setCreateTime(new Date());
        asset.setGuid(assetGuid);  //保留主表guid
        SyncDataUtil.initAsset(asset);
        assetValidateVO.setAsset(asset);
        assetValidateVO.setAssetVerify(assetVerify);
        assetValidateVO.setExistOld(true);
        assetValidateVO.setOsSetuptime(osSetuptime);
        assetValidateVO.setAssetTypeTreeCode(assetType.getTreeCode());
        assetValidateVO.setAssetExtend(getAssetExtend(extendInfos,assetGuid));
        assetValidateVO.setAssetExtendVerify(getAssetExtendVerify(extendInfos,assetVerify.getGuid()));
        assetList.add(assetValidateVO);
    }

    /**
     * 数据自动补全
     * @param assetVerify 数据
     * @param isAdd  是不是新增处理(数据不存场景)
     * @param assetVerifyOld 存在的数据
     * @param type 标识主表存在，还是待审表存在
     */
    private  Result<String> dataAutomaticCompletion(AssetVerify assetVerify, boolean isAdd ,AssetVerify assetVerifyOld,String type,List<TbConf> tbConfs,AssetType assetType) {
        // 数据存在的：优先级策略和对数据进行覆盖操作
         if(!isAdd){
             // 优先级策略处理
             Result<String> result = priorityStrategy(assetVerify,assetVerifyOld,type,tbConfs);
             if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
                 return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"优先级策略处理失败："+result.getMsg());
             }
             // 补全优先级处理
             String dataRepairOrder = strategyConfigService.syncAssetDataRepairOrder(tbConfs);
             if(StringUtils.isEmpty(dataRepairOrder)){
                 return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"补全优先级没有配制");
             }
             // 修改场景：数据补全策略
            return  strategyConfigService.supplementDataUpdate(assetVerify,tbConfs,assetVerifyOld,dataRepairOrder);
         }
         // 新增场景：按策略配置中标识统一补全
        return strategyConfigService.supplementData(assetVerify,tbConfs);
    }


    /**
     * 优先级策略：数据存在的情况
     * 数据来源优先级
     *     现有数据dataSourceType 与存在的数据 dataSourceType  根据配制数据来源优先级进行比对
     * 外部同步优先级：bxy-ry,bxy-zr,bxy-zs
     *    现有数据syncSource 与存在的数据 syncSource  根据外部同步优先级进行比对
     *    syncSource
     * @param assetVerify
     * @return
     */
    private Result<String> priorityStrategy(AssetVerify assetVerify ,AssetVerify assetVerifyOld,String type,List<TbConf> tbConfs) {
        // 数据来源优先级
        int dataSourceType = assetVerify.getDataSourceType();
        int dataSourceTypeOld = assetVerifyOld.getDataSourceType();
        Result<String> dataSourcePriorityStrategyResult = strategyConfigService.dataSourcePriorityStrategy(dataSourceType,assetVerifyOld.getDataSourceType(),tbConfs);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(dataSourcePriorityStrategyResult.getCode())){
            return dataSourcePriorityStrategyResult;
        }
        // 外部数据同步优选级：存在数据来源为外部的情况下(非手动输入)
        if(1 == dataSourceTypeOld){ // 手动输入不进行外部数据同步优先级比较
            return ResultUtil.success("success");
        }
        return strategyConfigService.outSourcePriorityStrategy(assetVerify.getSyncSource(),assetVerifyOld.getSyncSource(),tbConfs);
    }



    /**
     * 新增数据：数据增加创建时间、同步时间、状态、资产类型guid、资产类型unicode、待审记录的guid
     * @param assetVerify
     * @param assetType
     * @return
     */
    private AssetVerify getAssetVerify(AssetVerify assetVerify, AssetType assetType) {
        assetVerify.setAssetType(assetType.getGuid());
        assetVerify.setTypeUnicode(assetType.getUniqueCode());
        assetVerify.setSyncTime(new Date());
        assetVerify.setCreateTime(new Date());
        assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
        assetVerify.setGuid(UUIDUtils.get32UUID());
        return assetVerify;
    }


    /**
     * 更新数据：数据增加创建时间、同步时间、状态、资产类型、更新时间
     * @param assetVerify
     * @param assetType
     * @param assetVerifyGuid
     */
    private AssetVerify getAssetVerifyUpdate(AssetVerify assetVerify, AssetType assetType,String assetVerifyGuid) {
        assetVerify.setSyncTime(new Date());
        assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
        assetVerify.setUpdateTime(new Date());
        assetVerify.setAssetType(assetType.getGuid());
        assetVerify.setTypeUnicode(assetType.getUniqueCode());
        assetVerify.setGuid(assetVerifyGuid);
        assetVerify.setCreateTime(new Date());
        return assetVerify;
    }


    /**
     * 非usb类型ip为空，数据不处理；usb类型序列号为空，数据不处理
     * @param isUsb
     * @param assetSyncVO
     * @return
     */
    private Result<Boolean> isEmptyHandle(boolean isUsb, AssetSyncVO assetSyncVO) {
        // 数据是否处理
        if (!isUsb){ //非usb
            String ip = assetSyncVO.getIp();
            if(StringUtils.isEmpty(ip)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"非USB类型ip为空，数据不处理!"+assetSyncVO.getSyncUid());
            }
        }else{ //usb
            String serialNumber = assetSyncVO.getSerialNumber();
            if(StringUtils.isEmpty(serialNumber)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"USB类型序列号为空，数据不处理!"+assetSyncVO.getSyncUid());
            }
        }
        return ResultUtil.success(true);
    }


    /**
     * 判断数据是不是存在
     * @param assetSyncVO
     * @param isUsb
     * @return
     */
    private Map<String, Object> dataExist(AssetSyncVO assetSyncVO, boolean isUsb) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "error");
        // 主表存在
        AssetQueryVO asset = batchDataExistAsset(assetSyncVO.getIp(),assetSyncVO.getSerialNumber(), isUsb,assets);
        if (null != asset) {
            AssetVerify assetVerify = mapper.map(asset, AssetVerify.class);
            data.put("type", "1"); // 主表数据存在
            data.put("data", assetVerify);
            data.put("status", "success");
        }
        // 待审表存在
        AssetQueryVO assetVerify = dataExistAssetVerify(assetSyncVO, isUsb);
        if (null != assetVerify) {
            AssetVerify assetVer = mapper.map(assetVerify, AssetVerify.class);
            data.put("status", "success");
            // 主表、待审表同时存在，以主表数据为准
            if(null != asset){
                data.put("type", "3");
                data.put("assetVerify", assetVer);
            }else{ // 主表不存在、待审表存在
                data.put("type", "2");
                data.put("data", assetVer);
            }
            return data;
        }
        return data;
    }

    /**
     * 判断数据是不是存在(asset表中)
     * 非usb进行ip判断
     * usb进行序列号判断
     * @param ip
     * @param serialNumber
     * @param isUsb
     * @param assets
     * @return
     */
    @Override
    public AssetQueryVO batchDataExistAsset(String ip,String serialNumber, boolean isUsb,List<AssetQueryVO> assets) {
        if (!isUsb){ // 非usb
            return getAssetByIp(ip,assets);
        }else{ //usb(USB存储介质、USB外设 )
            return getAssetBySerialNumber(serialNumber,assets);
        }
    }


    private AssetQueryVO getAssetByIp(String ip, List<AssetQueryVO> assets) {
        for(AssetQueryVO asset : assets){
            if(ip.equals(asset.getIp())){
                return asset;
            }
        }
        return null;
    }

    private AssetQueryVO getAssetBySerialNumber(String serialNumber, List<AssetQueryVO> assets) {
        for(AssetQueryVO asset : assets){
            if(serialNumber.equals(asset.getSerialNumber())){
                return asset;
            }
        }
        return null;
    }

    /**
     * 判断数据是不是存在(asset_verify表中)
     * @param assetSyncVO
     * @param isUsb
     * @return
     */
    private AssetQueryVO dataExistAssetVerify(AssetSyncVO assetSyncVO, boolean isUsb) {
        if (!isUsb){ // 非usb
            return getAssetByIp(assetSyncVO.getIp(),assetVerifys);
        }else{ //usb(USB存储介质、USB外设 )
            return getAssetBySerialNumber(assetSyncVO.getSerialNumber(),assetVerifys);
        }
    }

}

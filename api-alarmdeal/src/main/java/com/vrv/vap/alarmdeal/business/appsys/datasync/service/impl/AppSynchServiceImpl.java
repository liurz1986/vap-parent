package com.vrv.vap.alarmdeal.business.appsys.datasync.service.impl;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.appsys.datasync.constant.AppDataSyncConstant;
import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.alarmdeal.business.appsys.datasync.repository.AppVerifyRepository;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppStrategyConfigService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppSynchService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppVerifyValidateService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppSysManagerSynchVo;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppValidateVO;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.OrgService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.service.BaseDataRedisCacheService;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 审核数据处理
 *  2022-07
 */
@Service
@Transactional
public class AppSynchServiceImpl extends BaseServiceImpl<AppSysManagerVerify, Integer> implements AppSynchService {
    private static Logger logger = LoggerFactory.getLogger(AppSynchServiceImpl.class);
    @Autowired
    private AppVerifyRepository appVerifyRepository;
    @Autowired
    private MapperUtil mapper;

    @Autowired
    private OrgService orgService;
    @Autowired
    private ClassifiedLevelService classifiedLevelService;

    @Autowired
    private AppSysManagerService appSysManagerService;

    @Autowired
    private AppStrategyConfigService appStrategyConfigService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AppVerifyValidateService appVerifyValidateService;

    @Autowired
    private BaseDataRedisCacheService baseDataRedisCacheService;
    private List<BaseKoalOrg> baseKoalOrgs ;// 缓存中获取所有单位
    private List<BaseDictAll> baseDictAlls ; //缓存中获取应用系统涉密等级
    private List<AppSysManager> appSysManagerList; //应用系统主表所有信息
    private List<AppSysManagerVerify> appSysManagerVerifyList ;//应用系统待申表所有信息

    private int appSysMaxId =0;   // 当前应用系统最大的id

    // 获取所有策略配置信息
    private List<TbConf> tbConfs = null;


    List<AppValidateVO> appValidateList = null;

    @Override
    public BaseRepository<AppSysManagerVerify, Integer> getRepository() {
        return appVerifyRepository;
    }

    /**
     * kafka数据处理
     * @param appSysDatas
     */
    @Override
    public void excDataSync(List<AppSysManagerSynchVo> appSysDatas){
        logger.debug("=========同步外部应用系统数据开始=======");
        try{
            // 初始化数据
            initData();
            // Kafka数据初步筛选：不符合要求不处理
            List<AppSysManagerSynchVo> appDatas = getBaseValidateSuccess(appSysDatas);
            if(appDatas.size() == 0){
                return;
            }
            // 数据去重处理(应用编号)
            List<AppSysManagerSynchVo> duplicateDatas =appVerifyValidateService.duplicateDatahandle(appDatas);
            // 自动补全，构造数据
            constructDataAndAutomatic(duplicateDatas,tbConfs);
            // 获取入库方式
            String importType = getAssetStrateryValue(tbConfs,"sync_app_data_import_type");;
            // 入库方式为自动入库，校验数据，校验成功入主库，校验失败，待申表上记录失败原因
            batchValidateHandle(importType);
            // 数据入库处理：入待审表、主表、发kafka消息
            dataWarehousingHandle(importType);
            logger.debug("=========同步外部应用系统数据结束=======");
        }catch (Exception e){
            logger.error("同步外部应用系统数据异常",e);
        }
        appSysDatas.clear();
    }




    /**
     * 初始化数据：
     * 1.获取现有所有应用系主表、待审表信息
     * 2.获取所有应用系统策略配置
     */
    private void initData() {
        appSysManagerList =appSysManagerService.findAll(); //应用系统主表所有信息
        appSysManagerVerifyList =this.findAll(); //应用系统待申表所有信息
        tbConfs =appStrategyConfigService.getStrategyConfigs();  // 获取所有策略配置信息
        appValidateList = new ArrayList<>();
    }

    /**
     * Kafka数据初步筛选：不符合要求不处理
     * 1.应用编号非空
     * 2.数据来源优选级策略中是否配置
     * 3.数据来源必填，有效性判断
     * 4.外部来源必填，有效性判断
     * @param appSysDatas
     * @return
     */
    private List<AppSysManagerSynchVo> getBaseValidateSuccess(List<AppSysManagerSynchVo> appSysDatas) {
        List<AppSysManagerSynchVo> validateSuccess = new ArrayList<>();
        for(AppSysManagerSynchVo data : appSysDatas){
            Result<String> validateResult = appVerifyValidateService.appBaseValidate(data, tbConfs);
            if(ResultCodeEnum.SUCCESS.getCode().equals(validateResult.getCode())){
                validateSuccess.add(data);
            }else{ // 校验失败数据，信息日志数据
                logger.error("getBaseValidateSuccess:{}",validateResult.getMsg());
            }
        }
        return validateSuccess;
    }


    /**
     * 自动补全，构造数据
     * @param datas
     * @param tbConfs
     */
    private void constructDataAndAutomatic( List<AppSysManagerSynchVo> datas, List<TbConf> tbConfs) {
        for(AppSysManagerSynchVo data : datas){
            //判断数据是不是存在
            Map<String, Object> dataExistResult = dataExist(data);
            // 自动补全，构造数据
            Result<String> result = excAppDataHandle(dataExistResult,data,tbConfs);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
                logger.error("应用系统自动补全，构造数据异常："+result.getMsg());
            }
        }
    }


    /**
     * 判断数据是不是存在
     * @param data
     * @return
     */
    private Map<String, Object> dataExist(AppSysManagerSynchVo data) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        // 主表存在
        AppSysManager app = getAppSysManagerByAppNo(data.getAppNo(),appSysManagerList);
        if (null != app) {
            result.put("type", "1"); // 主表数据存在
            result.put("data", app);
            result.put("status", "success");
        }
        // 待审表存在
        AppSysManagerVerify appVerify = dataExistAppVerify(data.getAppNo(), appSysManagerVerifyList);
        if (null != appVerify) {
            result.put("status", "success");
            // 主表、待审表同时存在，以主表数据为准
            if(null != app){
                result.put("type", "3");
                result.put("appVerify", appVerify);
            }else{ // 主表不存在、待审表存在
                result.put("type", "2");
                result.put("appVerify", appVerify);
            }
            return result;
        }
        return result;
    }


    private AppSysManagerVerify dataExistAppVerify(String appNo, List<AppSysManagerVerify> appSysManagerVerifyList) {
        if(null == appSysManagerVerifyList || appSysManagerVerifyList.size() == 0){
            return null;
        }
        for(AppSysManagerVerify data : appSysManagerVerifyList){
            if(appNo.equalsIgnoreCase(data.getAppNo())){
                return data;
            }
        }
        return null;
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
     * @param reslut
     * @param synchData
     * @param tbConfs
     */
    private Result<String> excAppDataHandle(Map<String, Object> reslut, AppSysManagerSynchVo synchData, List<TbConf> tbConfs) {
        String status =String.valueOf(reslut.get("status"));
        if ("error".equals(status)){
            // 数据不存在处理
            return appNewHandle(synchData,tbConfs);
        }else{
            // 数据存在处理
            return appUpdateHandle(reslut,synchData,tbConfs);
        }
    }



    /**
     * 新增数据
     *      自动补全处理
     * @param data
     * @param tbConfs
     */
    private Result<String> appNewHandle(AppSysManagerSynchVo data, List<TbConf> tbConfs) {
        // kafka数据
        AppSysManagerVerify appVerify = mapper.map(data, AppSysManagerVerify.class);
        Result<String> result = dataAutomaticCompletion(appVerify,true,null,tbConfs);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),result.getMsg());
        }
        appVerify.setSyncTime(new Date());
        appVerify.setCreateTime(new Date());
        appVerify.setSyncStatus(AppDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
        constructDataNew(appVerify);
        return  ResultUtil.success("success");
    }
    /**
     * 新增场景构造数据
     * @param appVerify
     */
    private void constructDataNew(AppSysManagerVerify appVerify) {
        AppValidateVO validateVO = new AppValidateVO();
        AppSysManager appSysManager = mapper.map(appVerify, AppSysManager.class);
        appSysManager.setCreateTime(new Date());
        validateVO.setAppSysManager(appSysManager);
        validateVO.setAppSysManagerVerify(appVerify);
        validateVO.setExistData(false);
        appValidateList.add(validateVO);
    }
    /**
     * 修改场景构造数据
     * @param appId
     * @param appVerify
     */
    private void constructDataUpdate(Integer appId,AppSysManagerVerify appVerify) {
        AppValidateVO validateVO = new AppValidateVO();
        AppSysManager appSysManager = mapper.map(appVerify, AppSysManager.class);
        appSysManager.setCreateTime(new Date());
        appSysManager.setId(appId); // 保留主表存在数据的id
        validateVO.setAppSysManager(appSysManager);
        validateVO.setAppSysManagerVerify(appVerify);
        validateVO.setExistData(true);
        validateVO.setAppId(appId);
        appValidateList.add(validateVO);
    }

    private Result<String> appUpdateHandle(Map<String, Object> reslut, AppSysManagerSynchVo synchData, List<TbConf> tbConfs) {
        // kafka数据
        AppSysManagerVerify appVerify = mapper.map(synchData, AppSysManagerVerify.class);
        String type =String.valueOf(reslut.get("type"));
        AppSysManagerVerify appSysManagerVerifyOld = null;
        AppSysManager appSysManagerOld = null;
        if("1".equals(type)||"3".equals(type)){ // 主表中存在;主表、待审表同时存在
            appSysManagerOld = (AppSysManager)reslut.get("data");
            appSysManagerVerifyOld = mapper.map(appSysManagerOld, AppSysManagerVerify.class);
        }else{ // 待审表存在
            appSysManagerVerifyOld = (AppSysManagerVerify)reslut.get("appVerify");
        }
        // 数据自动补全处理
        Result<String> result = dataAutomaticCompletion(appVerify,false,appSysManagerVerifyOld,tbConfs);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),result.getMsg());
        }
       // 三种场景：主表中存在，待审表不存在；主表中不存在，待审表存在；主表、待审表同时存在
        if("1".equals(type)){ // 主表中存在，待审表不存在：kafka数据覆盖主表 ，保留guid，新增待审表
            Integer appId = appSysManagerOld.getId(); // 主表id
            AppSysManagerVerify appverify = getAppSysManagerVerify(appVerify);
            constructDataUpdate(appId,appverify);
        } else if("2".equals(type)){ //主表中不存在，待审表存在：kafka数据覆盖待审表，保留guid，新增主表
            AppSysManagerVerify appverify = getUpdateAppSysManagerVerify(appVerify,appSysManagerVerifyOld.getId());
            constructDataNew(appverify);
        }else{ // 主表、待审表同时存在：kafka数据覆盖待审表、主表，保留guid
            AppSysManagerVerify  verifyOld = (AppSysManagerVerify)reslut.get("appVerify");
            AppSysManagerVerify appverify = getUpdateAppSysManagerVerify(appVerify,verifyOld.getId());
            Integer appId = appSysManagerOld.getId(); // 主表id
            constructDataUpdate(appId,appverify);
        }
        return ResultUtil.success("success");
    }


    /**
     * 待审表不存在
     * @param appVerify
     * @return
     */
    private AppSysManagerVerify getAppSysManagerVerify(AppSysManagerVerify appVerify) {
        appVerify.setSyncTime(new Date());
        appVerify.setCreateTime(new Date());
        appVerify.setSyncStatus(AppDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
        return appVerify;
    }

    /**
     * 待审表存在
     * @param appVerify
     * @param id
     * @return
     */
    private AppSysManagerVerify getUpdateAppSysManagerVerify(AppSysManagerVerify appVerify, Integer id) {
        appVerify.setSyncTime(new Date());
        appVerify.setCreateTime(new Date());
        appVerify.setSyncStatus(AppDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
        appVerify.setId(id); // 保留存在待申表id
        return appVerify;
    }

    private  Result<String> dataAutomaticCompletion(AppSysManagerVerify appVerify, boolean isAdd ,AppSysManagerVerify appVerifyOld,List<TbConf> tbConfs) {
        // 数据存在的：优先级策略和对数据进行覆盖操作
        if(!isAdd){
            // 优先级策略处理
            Result<String> result = priorityStrategy(appVerify,appVerifyOld,tbConfs);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(result.getCode())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"优先级策略处理失败："+result.getMsg());
            }
            // 数据覆盖处理
            Result<String> updateResult =updateAutomaticDataByOld(appVerify,appVerifyOld);
            if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(updateResult.getCode())){
                return updateResult;
            }
            // 数据补全策略
            return  appStrategyConfigService.supplementData(appVerify,tbConfs);
        }
        // 数据补全策略
        return appStrategyConfigService.supplementData(appVerify,tbConfs);
    }

    /**
     * 数据覆盖：单位名称、涉密等级、应用名称、涉密厂商、应用域名
     * @param appVerify
     * @param appVerifyOld
     */
    private Result<String> updateAutomaticDataByOld(AppSysManagerVerify appVerify, AppSysManagerVerify appVerifyOld) {
        appVerify.setId(appVerifyOld.getId());
        String data = getAssetStrateryValue(tbConfs,"sync_app_data_repair_base");
        if(!"1".equals(data)){  // 不覆盖 1是，0否
            return ResultUtil.success("success");
        }
        try{
            appVerify.setDepartmentGuid(appVerifyOld.getDepartmentGuid());
            appVerify.setDepartmentName(appVerifyOld.getDepartmentName());
            appVerify.setAppName(appVerifyOld.getAppName());
            appVerify.setSecretLevel(appVerifyOld.getSecretLevel());
            appVerify.setSecretCompany(appVerifyOld.getSecretCompany());
            appVerify.setDomainName(appVerifyOld.getDomainName());
        }catch (Exception e){
            logger.error("数据覆盖异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据补全策异常");
        }
        return ResultUtil.success("success");

    }

    /**
     * 优先级策略：数据存在的情况
     * 数据来源优先级
     *     现有数据dataSourceType 与存在的数据 dataSourceType  根据配制数据来源优先级进行比对
     * 外部同步优先级：bxy-ry,bxy-zr,bxy-zs 外部数据同步优选级：存在数据来源为外部的情况下(非手动输入)
     *    现有数据syncSource 与存在的数据 syncSource  根据外部同步优先级进行比对
     *    syncSource
     * @param appVerify
     * @return
     */
    private Result<String> priorityStrategy(AppSysManagerVerify appVerify, AppSysManagerVerify appVerifyOld, List<TbConf> tbConfs) {
        // 数据来源优先级
        int dataSourceType = appVerify.getDataSourceType();
        int dataSourceTypeOld = appVerifyOld.getDataSourceType();
        Result<String> dataSourcePriorityStrategyResult = dataSourcePriorityStrategy(dataSourceType,appVerifyOld.getDataSourceType(),tbConfs);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(dataSourcePriorityStrategyResult.getCode())){
            return dataSourcePriorityStrategyResult;
        }
        // 外部数据同步优选级：存在数据来源为外部的情况下(非手动输入)
        if(1 == dataSourceTypeOld){ // 手动输入不进行外部数据同步优先级比较
            return ResultUtil.success("success");
        }
        return outSourcePriorityStrategy(appVerify.getSyncSource(),appVerifyOld.getSyncSource(),tbConfs);
        
    }
    private Result<String> dataSourcePriorityStrategy(int currentDataSourceType, int oldDataSourceType, List<TbConf> tbConfs) {
        // 获取配置的数据来源优选级
        String dataSourceOrder =getAssetStrateryValue(tbConfs,"sync_app_data_source_order");
        if(org.apache.commons.lang3.StringUtils.isEmpty(dataSourceOrder)){
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

    private Result<String> outSourcePriorityStrategy(String currentSyncSource, String oldtSyncSource,List<TbConf> tbConfs) {
        String outSource =getAssetStrateryValue(tbConfs,"sync_app_out_source_order");
        if(org.apache.commons.lang3.StringUtils.isEmpty(outSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部同步优先级没有配制");
        }
        String[] orders = outSource.split(",");
        if(!Arrays.asList(orders).contains(currentSyncSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前外部数据的数据不符合要求:"+currentSyncSource);
        }
        if(org.apache.commons.lang3.StringUtils.isEmpty(oldtSyncSource)){ // 历史的syncSource为空，以最高的优先级来处理
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
     * 入库方式为自动入库，校验数据，校验成功入主库，校验失败，待申表上记录失败原因
     * @param importType
     */
    private void batchValidateHandle(String importType) {
        if(!"2".equalsIgnoreCase(importType)){  // 手动入库不处理
            return;
        }
        if(null == appValidateList || appValidateList.size() == 0){
            return;
        }
        batchValidateAssetAutomatic(appValidateList);
    }
    /**
     * 批量数据校验(kakfa自动入库)
     * 1。初始化数据
     * 2. 数据校验
     *
     * @param appValidateList
     * @return
     */
    private void batchValidateAssetAutomatic(List<AppValidateVO> appValidateList){
        if(appValidateList.size() == 0){
            return;
        }
        // 初始化数据
        initBaseDataSynch();
        // 批量校验处理
        for(AppValidateVO data : appValidateList){
            AppSysManager appSysManager = data.getAppSysManager();
            Result<String> validateAssetResult = appVerifyValidateService.appDataValidate(appSysManager,baseKoalOrgs,baseDictAlls);
            if (validateAssetResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
                data.getAppSysManagerVerify().setSyncMessage(validateAssetResult.getMsg());
                data.getAppSysManagerVerify().setSyncStatus(AppDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
                data.getAppSysManagerVerify().setAppId(null);
                data.setCheckSucess(false);
            }else{
                data.getAppSysManagerVerify().setSyncMessage("入库成功");
                data.getAppSysManagerVerify().setSyncStatus(AppDataSyncConstant.SYNCSTATUSSUCCESS); // 入库成功
                data.setCheckSucess(true);
                // 新增的数据id，处理
                if(!data.isExistData()){   // 主表新增的场景，id处理
                    appSysMaxId++;
                    appSysManager.setId(appSysMaxId);
                }
                data.getAppSysManagerVerify().setAppId(appSysManager.getId()); // 关联应用id
            }
        }
    }




    /**
     * 自动入库校验初始化数据
     *
     * 1. 缓存中获取所有单位
     * 2. 缓存中获取应用系统涉密等级
     * 3. 应用系统中最大id，为了新增时手动加入id的值
     */
    private void initBaseDataSynch() {
        baseKoalOrgs = orgService.getOrgsCache();// 缓存中获取所有单位
        baseDictAlls = classifiedLevelService.getAppAll(); //获取应用系统涉密等级
        appSysMaxId = appSysManagerService.getCurrentMaxId(); // 当前最大的id
    }




    /**
     * 数据入库处理：入待审表、主表、发kafka消息
     * @param importType
     */
    private void dataWarehousingHandle(String importType) {
        if(appValidateList.size() == 0){
            return;
        }
        List<AppSysManagerVerify> verifList = new ArrayList<>();
        List<AppSysManager> appSysList = new ArrayList<>();
        for(AppValidateVO data : appValidateList){
            verifList.add(data.getAppSysManagerVerify());
            if(data.isCheckSucess()){
                appSysList.add(data.getAppSysManager());
            }
        }
        // 保存数据
        if(verifList.size() > 0){
            this.save(verifList);
        }

        if(!"2".equalsIgnoreCase(importType)){  // 手动入库不处理
            return;
        }
        // 入主表
        if(appSysList.size() > 0){
            appSysManagerService.save(appSysList);
            // 更新缓存  2022-08-09
            baseDataRedisCacheService.updateAllAppCache();
            // 发kafka消息
            messageService.sendKafkaMsg("app");
        }
    }
    private String getAssetStrateryValue(List<TbConf> datas ,String key){
        for(TbConf tbConf : datas){
            if(key.equals(tbConf.getKey())){
                return tbConf.getValue();
            }
        }
        return  null;
    }
    private int getDataIndex(String[] orders, String name) {
        for(int i=0;i<orders.length;i++){
            if(orders[i].equals(name)){
                return i;
            }
        }
        return 99;
    }



    private AppSysManager getAppSysManagerByAppNo(String appNo, List<AppSysManager> appSysDatas) {
        if(null == appSysDatas || appSysDatas.size() == 0){
            return null;
        }
        for(AppSysManager app : appSysDatas){
            if(appNo.equals(app.getAppNo())){
                return app;
            }
        }
        return null;
    }


}

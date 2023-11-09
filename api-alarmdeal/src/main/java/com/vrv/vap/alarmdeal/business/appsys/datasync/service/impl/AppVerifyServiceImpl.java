package com.vrv.vap.alarmdeal.business.appsys.datasync.service.impl;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.appsys.datasync.constant.AppDataSyncConstant;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppVerifyValidateService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppVerifySearchVO;
import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.OrgService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.repository.AppVerifyRepository;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppVerifyService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.service.BaseDataRedisCacheService;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 审核数据处理
 *  2022-07
 */
@Service
@Transactional
public class AppVerifyServiceImpl extends BaseServiceImpl<AppSysManagerVerify, Integer> implements AppVerifyService {
    private static Logger logger = LoggerFactory.getLogger(AppVerifyServiceImpl.class);
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
    private MessageService messageService;

    @Autowired
    private AppVerifyValidateService appVerifyValidateService;

    @Autowired
    private BaseDataRedisCacheService baseDataRedisCacheService;

    // 获取所有策略配置信息
    private List<TbConf> tbConfs = null;



    @Override
    public BaseRepository<AppSysManagerVerify, Integer> getRepository() {
        return appVerifyRepository;
    }



    /**
     * 编辑保存数据
     * 1. 数据校验
     * 2. 校验成功更改为待入库状态
     * @param appSysManagerVerify
     * @return
     */
    @Override
    public Result<String> saveEditdData(AppSysManagerVerify appSysManagerVerify) {
        Integer id = appSysManagerVerify.getId();
        AppSysManagerVerify verify = this.getOne(id);
        if(null == verify){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据不存在");
        }
        // 数据校验
        AppSysManager appSysManager = mapper.map(appSysManagerVerify, AppSysManager.class);
        List<BaseDictAll> levles = classifiedLevelService.getAppAll(); //获取应用系统涉密等级
        List<BaseKoalOrg> orgs = orgService.getOrgs();// 获取所有单位
        Result<String> validateAssetResult = appVerifyValidateService.appDataValidate(appSysManager,orgs,levles);
        if (validateAssetResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
          return validateAssetResult;
        }
        // 更新为待入库状态
        appSysManagerVerify.setSyncStatus(AppDataSyncConstant.SYNCSTATUSWAIT);
        // 编辑后将历史关联的资产id，入库信息清空
        appSysManagerVerify.setAppId(null);
        appSysManagerVerify.setSyncMessage(null);
        // 保留同步时一些数据
        appSysManagerVerify.setDataSourceType(verify.getDataSourceType());
        appSysManagerVerify.setSyncUid(verify.getSyncUid());
        appSysManagerVerify.setSyncSource(verify.getSyncSource());
        appSysManagerVerify.setSyncTime(verify.getSyncTime());
        this.save(appSysManagerVerify);  // 更新待审表状态
        return ResultUtil.success("success");
    }



    @Override
    public PageRes<AppSysManagerVerify> query(AppVerifySearchVO search) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        PageReq pager = mapper.map(search, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        addSearchCondition(queryConditions,search);
        Page<AppSysManagerVerify> page=findAll(queryConditions,pager.getPageable());
        // 将实体进行替换处理
        PageRes<AppSysManagerVerify> data = PageRes.toRes(page);
        List<AppSysManagerVerify> lists = data.getList();
        PageRes<AppSysManagerVerify> res = new PageRes();
        res.setList(lists);
        res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        res.setTotal(page.getTotalElements());
        return res;
    }


    /**
     * 查询条件
     * 应用名称、单位名称、涉密等级、状态
     * @param queryConditions
     * @param search
     */
    private void addSearchCondition(List<QueryCondition> queryConditions, AppVerifySearchVO search) {
        // 应用名称
        if(!org.springframework.util.StringUtils.isEmpty(search.getAppName())){
            queryConditions.add(QueryCondition.like("appName",search.getAppName()));
        }
        //单位名称
        if(!org.springframework.util.StringUtils.isEmpty(search.getDepartmentName())){
            queryConditions.add(QueryCondition.like("departmentName",search.getDepartmentName()));
        }
        // 涉密等级
        if(!org.springframework.util.StringUtils.isEmpty(search.getSecretLevel())){
            queryConditions.add(QueryCondition.eq("secretLevel",search.getSecretLevel()));
        }
        // 状态
        if(search.getSyncStatus() > 0){
            queryConditions.add(QueryCondition.eq("syncStatus",search.getSyncStatus()));
        }
    }

    /**
     * 忽略
     * @param id
     * @return
     */
    @Override
    public Result<String> neglect(Integer id) {
        AppSysManagerVerify appData =this.getOne(id);
        if(null == appData){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据不能存在");
        }
        appData.setSyncStatus(AppDataSyncConstant.SYNCSTATUSNEG); // 更新为忽略状态
        appData.setAppId(null);
        appData.setSyncMessage(null);
        this.save(appData);
        return ResultUtil.success("success");
    }

    /**
     *  单个入库
     * 1. 初始化数据： 获取所有单位、获取所有应用系统涉密等级
     * 2. 数据校验
     * 3. 判断数据是不是存在主表
     * 4. 更改待审库状态
     * 5. 保存主表
     * 6. 发kafka消息
     * @param id
     * @return
     */
    @Override
    public Result<String> saveApp(Integer  id) {
        AppSysManagerVerify appData =this.getOne(id);
        if(null == appData){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据不能存在");
        }
        // 是不是处于入库状态
        if(AppDataSyncConstant.SYNCSTATUSWAIT != appData.getSyncStatus()){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"该数据不符合入库条件");
        }
        AppSysManager appSysManagerNew = mapper.map(appData, AppSysManager.class);
        // 数据校验,成功入库，不成功记录失败原因
        List<BaseDictAll> levles = classifiedLevelService.getAppAll(); //获取应用系统涉密等级
        List<BaseKoalOrg> orgs = orgService.getOrgs();// 获取所有单位
        Result<String> validateAssetResult = appVerifyValidateService.appDataValidate(appSysManagerNew,orgs,levles);
        if (validateAssetResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            appData.setSyncMessage(validateAssetResult.getMsg());
            appData.setSyncStatus(AppDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
            appData.setAppId(null);
            this.save(appData);  // 更新待审表状态
        }else{
            appData.setSyncMessage("入库成功");
            appData.setSyncStatus(AppDataSyncConstant.SYNCSTATUSSUCCESS); // 入库成功
            // 判断在主表是不是存在,存在用以前的id
            Integer appId = 0;  // id
            List<QueryCondition> queryConditions=new ArrayList<>();
            queryConditions.add(QueryCondition.eq("appNo",appData.getAppNo()));
            List<AppSysManager> appSysManagers = appSysManagerService.findAll(queryConditions);
            boolean isExist = false;
            String oldAppNo = null;
            if(null != appSysManagers && appSysManagers.size() > 0){
                oldAppNo = appSysManagers.get(0).getAppNo();
                appId = appSysManagers.get(0).getId();
                isExist = true;
            }else{
                appId = appSysManagerService.getCurrentMaxId()+1;
            }
            appSysManagerNew.setId(appId);
            appSysManagerNew.setCreateTime(new Date());
            appSysManagerService.save(appSysManagerNew); // 保存主表信息
            appData.setAppId(appId);  // 关联应用id
            this.save(appData);  // 更新待审表状态
            changeRedisCache(isExist,appSysManagerNew,oldAppNo);   // 更新缓存 2022-08-09
            messageService.sendKafkaMsg("app");    // 发kafka消息
        }
        return ResultUtil.success("success");
    }

    /**
     * 更新缓存  2022-08-09
     * @param isExist
     * @param appSysManagerNew
     */
    private void changeRedisCache(boolean isExist, AppSysManager appSysManagerNew, String oldAppNo) {
        if(isExist){
            baseDataRedisCacheService.editAppSysManager(appSysManagerNew,oldAppNo);
        }else{
            baseDataRedisCacheService.addAppSysManager(appSysManagerNew);
        }
    }


    /**
     * 批量数据入库：
     *
     * 1. 获取所有待入库数据
     * 2. 初始化数据：
     *     获取所有主表数据
     *     获取所有单位
     *     获取所有应用系统涉密等级
     * 3.  数据校验
     * 4.  判断数据是不是存在主表
     * 5.  批量更新待申表状态
     * 6.  批量入主表数据
     * 7.  发kafka消息
     * @return
     */
    @Override
    public Result<String> batchSaveApp() {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("syncStatus",2));
        List<AppSysManagerVerify> appVerifys = this.findAll(queryConditions);
        if(null == appVerifys || appVerifys.size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有待入库的数据");
        }
        // 异步处理数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                excBatchSaveApp(appVerifys);
            }
        }).start();
        return ResultUtil.success("批量数据入库处理中");

    }

    /**
     * 执行批量入库
     *
     * @param appVerifys
     */
    private void excBatchSaveApp(List<AppSysManagerVerify> appVerifys) {
        try{
            // 初始化数据
            List<BaseDictAll> levles = classifiedLevelService.getAppAll(); //获取应用系统涉密等级
            List<BaseKoalOrg> orgs = orgService.getOrgs();// 获取所有单位
            List<AppSysManager> appSysManagers = appSysManagerService.findAll(); // 所有主表数据
            Integer curMaxId = appSysManagerService.getCurrentMaxId(); // 当前最大的id
            // 数据校验处理及数据是不是存在
            List<AppSysManager> appSysDatas = new ArrayList<>();
            appSysManagerHandle(appVerifys,appSysDatas,levles,orgs,appSysManagers,curMaxId);
            // 数据入库及发kafka消息
            batchSaveData(appVerifys,appSysDatas);
        }catch (Exception e){
            logger.error("应用系统待审表执行批量入库异常",e);
        }

    }



    /**
     * 数据是不是存在及数据校验处理
     *
     * @param appVerifys
     * @param appSysDatas
     * @param levles
     * @param orgs
     * @param allAppSysManagers
     * @param curMaxId
     */
    private void appSysManagerHandle(List<AppSysManagerVerify> appVerifys, List<AppSysManager> appSysDatas, List<BaseDictAll> levles, List<BaseKoalOrg> orgs, List<AppSysManager> allAppSysManagers,Integer curMaxId) {
       for(AppSysManagerVerify verify : appVerifys){
           AppSysManager appSysManagerNew = mapper.map(verify, AppSysManager.class);
           // 数据校验
           Result<String> validateAssetResult = appVerifyValidateService.appDataValidate(appSysManagerNew,orgs,levles);
           if (validateAssetResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())) {
               verify.setSyncMessage(validateAssetResult.getMsg());
               verify.setSyncStatus(AppDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
               verify.setAppId(null);
           }else{
               verify.setSyncMessage("入库成功");
               verify.setSyncStatus(AppDataSyncConstant.SYNCSTATUSSUCCESS); // 入库成功
               String appNo = appSysManagerNew.getAppNo();
               // 判断数是不是存在
               AppSysManager oldData = getAppSysManagerByAppNo(appNo,allAppSysManagers);
               int appId = 0;
               if(null != oldData){ // 存在保留存在的id
                   appId = oldData.getId();
               }else{   // 不存在用新的id
                   curMaxId++;
                   appId = curMaxId;
               }
               appSysManagerNew.setId(appId);
               appSysManagerNew.setCreateTime(new Date());
               verify.setAppId(appId);  // 关联应用id
               appSysDatas.add(appSysManagerNew);
           }
       }
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


    /**
     * 数据批量入库，发kafka消息
     * @param appVerifys
     * @param appSysDatas
     */
    private void batchSaveData(List<AppSysManagerVerify> appVerifys, List<AppSysManager> appSysDatas) {
        if(appVerifys.size() > 0){
            this.save(appVerifys);
        }
        if(appSysDatas.size() > 0){
            appSysManagerService.save(appSysDatas);
            // 更新缓存 2022-08-09
            baseDataRedisCacheService.updateAllAppCache();
            // 发kafka消息
            messageService.sendKafkaMsg("app");    // 发kafka消息
        }
    }

}

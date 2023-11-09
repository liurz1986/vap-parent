package com.vrv.vap.alarmdeal.business.appsys.datasync.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppStrategyConfigService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.util.CommonUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 应用系统策略配置
 *
 * 2022-07
 */
@Service
public class AppStrategyConfigServiceImpl implements AppStrategyConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppStrategyConfigServiceImpl.class);
    @Autowired
    private TbConfService tbConfService;
    //策略
    private String[] configs={"sync_app_data_import_type","sync_app_data_source_order","sync_app_out_source_order","sync_app_data_repair_base",
            "sync_app_data_repair_org_order","sync_app_data_repair_org_default","sync_app_data_repair_secrecy",
            "sync_app_data_repair_name_default","sync_app_data_repair_host_default","sync_app_data_repair_manufacturer_default"};


    /**
     * 数据补全策略-采用异常执行
     * 单位名称、涉密等级、应用名称、涉密厂商、应用域名为空，进行补全处理。其中单位名称以code是否为空作为判断条件
     * @param appVerify
     * @param tbConfs
     * @return
     */
    @Override
    public  Result<String> supplementData(AppSysManagerVerify appVerify, List<TbConf> tbConfs) {
        try{
            ExecutorService executorService = Executors.newCachedThreadPool();
            Future<Result<String>> futureResult = executorService.submit(new Callable<Result<String>>() {
                @Override
                public Result<String> call()  {
                    return synchSupplementData(appVerify,tbConfs);
                }
            });
            return futureResult.get();
        }catch (Exception e){
            LOGGER.error("数据补全策异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据补全策异常");
        }
    }

    /**
     * 具体数据补全策略
     * 单位名称、涉密等级、应用名称、涉密厂商、应用域名为空，进行补全处理。其中单位名称以code是否为空作为判断条件
     * @param appVerify
     * @param tbConfs
     * @return
     */
    public  Result<String> synchSupplementData(AppSysManagerVerify appVerify, List<TbConf> tbConfs) {
        try{
            orgCodeSupplement(appVerify,tbConfs);
            secretLevelSupplement(appVerify,tbConfs);
            appNameSupplement(appVerify,tbConfs);
            secretCompanySupplement(appVerify,tbConfs);
            domainNameSupplement(appVerify,tbConfs);
            return ResultUtil.success("success");
        }catch (Exception e){
            LOGGER.error("数据补全策异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据补全策异常");
        }
    }

    /**
     * 单位名称
     * sync_app_data_repair_org_default	{"code":"JG000001",“name”:"汉口区保密办"} // 默认组织结构
     *
     * @param appVerify
     * @param tbConfs
     */
    private void orgCodeSupplement(AppSysManagerVerify appVerify, List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(appVerify.getDepartmentGuid())){
            return;
        }
        String data =  CommonUtil.getAppStrateryValue(tbConfs,"sync_app_data_repair_org_default");
        if(StringUtils.isEmpty(data)){
            throw new AlarmDealException(-1,"默认组织结构没有配制");
        }
        try{
            Map<String,Object> map=JSONObject.parseObject(data,Map.class);
            appVerify.setDepartmentGuid(String.valueOf(map.get("code")));
            appVerify.setDepartmentName(String.valueOf(map.get("name")));
        }catch (Exception e){
            LOGGER.error("单位名称数据补全异常",e);
            throw new AlarmDealException(-1,"单位名称数据补全异常");
        }

    }

    /**
     *  涉密等级数据处理
     *  sync_app_data_repair_secrecy		{"code":"1","name":"机密"}	   // 涉密等级
     * @param appVerify
     * @param tbConfs
     */
    private void secretLevelSupplement(AppSysManagerVerify appVerify, List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(appVerify.getSecretLevel())){
            return ;
        }
        String secrecy =  CommonUtil.getAppStrateryValue(tbConfs,"sync_app_data_repair_secrecy");
        if(StringUtils.isEmpty(secrecy)){
            throw new AlarmDealException(-1,"涉密等级没有配制默认值");
        }
        try{
            Map<String,Object> map= JSONObject.parseObject(secrecy,Map.class);
            appVerify.setSecretLevel(String.valueOf(map.get("code")));
        }catch (Exception e){
            LOGGER.error("涉密等级数据补全异常",e);
            throw new AlarmDealException(-1,"涉密等级数据补全异常");
        }
    }

    /**
     * 应用名称数据处理
     * sync_app_data_repair_name_default  未知 // 默认应用名称
     * @param appVerify
     * @param tbConfs
     */
    private void appNameSupplement(AppSysManagerVerify appVerify, List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(appVerify.getAppName())){
            return ;
        }
        String appName =  CommonUtil.getAppStrateryValue(tbConfs,"sync_app_data_repair_name_default");
        if(StringUtils.isEmpty(appName)){
            throw new AlarmDealException(-1,"应用名称没有配制默认值");
        }
        appVerify.setAppName(appName);
    }

    /**
     * 涉密厂商
     * sync_app_data_repair_manufacturer_default  未知 //默认厂商
     * @param appVerify
     * @param tbConfs
     */
    private void secretCompanySupplement(AppSysManagerVerify appVerify, List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(appVerify.getSecretCompany())){
            return ;
        }
        String secretCompany =  CommonUtil.getAppStrateryValue(tbConfs,"sync_app_data_repair_manufacturer_default");
        if(StringUtils.isEmpty(secretCompany)){
            throw new AlarmDealException(-1,"涉密厂商没有配制默认值");
        }
        appVerify.setSecretCompany(secretCompany);
    }

    /**
     * 应用域名
     * sync_app_data_repair_host_default  未知 //域名
     * @param appVerify
     * @param tbConfs
     */
    private void domainNameSupplement(AppSysManagerVerify appVerify, List<TbConf> tbConfs) {
        if(StringUtils.isNotEmpty(appVerify.getDomainName())){
            return ;
        }
        String domainName = CommonUtil.getAppStrateryValue(tbConfs,"sync_app_data_repair_host_default");
        if(StringUtils.isEmpty(domainName)){
            throw new AlarmDealException(-1,"应用域名没有配制默认值");
        }
        appVerify.setDomainName(domainName);
    }


    /**
     * 更新策略配置
     * @param tbConfS
     */
    @Override
    public void updateStrategyConfig(List<TbConf> tbConfS) {
        tbConfService.save(tbConfS);
    }

    /**
     * 获取策略配置
     */
    @Override
    public List<TbConf> getStrategyConfigs() {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("key",configs));
        return tbConfService.findAll(conditions);
    }
}

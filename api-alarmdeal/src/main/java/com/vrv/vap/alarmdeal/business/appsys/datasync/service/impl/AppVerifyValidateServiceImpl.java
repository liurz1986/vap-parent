package com.vrv.vap.alarmdeal.business.appsys.datasync.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppVerifyValidateService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppSysManagerSynchVo;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AppVerifyValidateServiceImpl implements AppVerifyValidateService {
    private static Logger logger = LoggerFactory.getLogger(AppVerifyValidateServiceImpl.class);
    /**
     * 数据校验：
     *  1.单位名称、涉密等级、应用名称、涉密厂商、应用域名必填
     *  2.单位名称、涉密等级有效性
     * @param appSysManager
     * @param baseKoalOrgs
     * @param baseDictAlls
     * @return
     */
    @Override
    public Result<String> appDataValidate(AppSysManager appSysManager, List<BaseKoalOrg> baseKoalOrgs, List<BaseDictAll> baseDictAlls) {
        // 必填子段校验
        Result<String>  validateMustResult =validateMust(appSysManager);
        if (validateMustResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validateMustResult;
        }
        // 有效性
        Result<String>  validityValidateResult =validityValidate(appSysManager,baseKoalOrgs,baseDictAlls);
        if (validityValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validityValidateResult;
        }
        return ResultUtil.success("success");
    }


    /**
     * Kafka数据初步筛选：不符合要求不处理
     * 1.应用编号非空
     * 2.数据来源优选级策略中是否配置
     * 3.数据来源必填，有效性判断
     * 4.外部来源必填，有效性判断
     * @param data
     * @param tbConfs
     * @return
     */
    @Override
    public Result<String> appBaseValidate(AppSysManagerSynchVo data, List<TbConf> tbConfs) {
        // 应用编号非空
        if(StringUtils.isEmpty(data.getAppNo())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"应用编号为空数据不处理");
        }
        // 数据来源类型
        int dataSourceType = data.getDataSourceType();
        // 获取配置的数据来源优选级
        String dataSourceConfig = getStrategyConfigValueByKey(tbConfs,"sync_app_data_source_order");
        if(org.apache.commons.lang3.StringUtils.isEmpty(dataSourceConfig)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据来源优选级没有配制");
        }
        String[] orders = dataSourceConfig.split(",");
        if(!Arrays.asList(orders).contains(dataSourceType+"")){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前数据来源的数据不符合要求:"+dataSourceType);
        }
        // 外部来源信息
        String syncSource = data.getSyncSource();
        if(org.apache.commons.lang3.StringUtils.isEmpty(syncSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部来源信息不能为空");
        }
        String outSource = getStrategyConfigValueByKey(tbConfs,"sync_app_out_source_order");
        if(org.apache.commons.lang3.StringUtils.isEmpty(outSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部同步优先级没有配制");
        }
        String[] outOrders = outSource.split(",");
        if(!Arrays.asList(outOrders).contains(syncSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前外部来源信息不符合要求:"+syncSource);
        }
        return ResultUtil.success("success");
    }


    /**
     * 数据去重处理(应用编号)
     * @param appDatas
     * @return
     */
    @Override
    public List<AppSysManagerSynchVo> duplicateDatahandle(List<AppSysManagerSynchVo> appDatas){
        Map<String,Object> result = new HashMap<>();
        List<AppSysManagerSynchVo> appDatasCopy = new ArrayList<>();
        List<AppSysManagerSynchVo> delDatas = new ArrayList<>();
        appDatasCopy.addAll(appDatas);
        List<String> appNos = new ArrayList<>();
        for(AppSysManagerSynchVo data : appDatas){
            String appNo = data.getAppNo();
            if(appNos.contains(appNo)){   // 表示存在相同应用编号
                continue;
            }
            appNos.add(appNo);
            appDatasCopy.remove(data); // 移除当前的
            List<AppSysManagerSynchVo> datas = getAppNoDuplicates(appNo,appDatasCopy);
            if(null != datas && datas.size() > 0){
                // 获取最后一个作为有效数据，其他全部删除处理
                appDatasCopy.removeAll(datas); // 移除重复的数据
                datas.remove(datas.get(datas.size()-1)); // 移除最后一个，其他作为删除
                delDatas.add(data); // 删除当前数据
                delDatas.addAll(datas);  // 删除重复的(最后一个除外)
                result.put(appNo,datas.size());
            }
        }
        if(delDatas.size() > 0){
            appDatas.removeAll(delDatas);
            logger.debug("应用编号去重处理,序列号重复数据："+ JSON.toJSONString(result));
        }
        return appDatas;
    }



    private Result<String> validateMust(AppSysManager appSysManager) {
        try{
            // 单位名称、涉密等级、应用名称、涉密厂商、应用域名必填
            if(StringUtils.isEmpty(appSysManager.getDepartmentGuid())||StringUtils.isEmpty(appSysManager.getDepartmentName())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"单位名称不能为空");
            }
            if(StringUtils.isEmpty(appSysManager.getSecretLevel())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密等级不能为空");
            }
            if(StringUtils.isEmpty(appSysManager.getAppName())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"应用名称不能为空");
            }
            if(StringUtils.isEmpty(appSysManager.getSecretCompany())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密厂商不能为空");
            }
            if(StringUtils.isEmpty(appSysManager.getDomainName())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"域名不能为空");
            }
            return ResultUtil.success("success");
        }catch(Exception e){
            logger.error("必填校验异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"必填校验异常");
        }

    }

    /**
     * 有效性校验：单位名称、涉密等级有效性
     * @param appSysManager
     * @return
     */
    private Result<String> validityValidate(AppSysManager appSysManager,List<BaseKoalOrg> baseKoalOrgs,List<BaseDictAll> baseDictAlls){
        // 单位名称
        Result<String>  orgValidityValidateResult =orgValidityValidate(appSysManager,baseKoalOrgs);
        if (orgValidityValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return orgValidityValidateResult;
        }
        // 涉密等级
        Result<String>  secretLevelValidityValidateResult =secretLevelValidityValidate(appSysManager,baseDictAlls);
        if (secretLevelValidityValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return secretLevelValidityValidateResult;
        }
        return ResultUtil.success("success");
    }

    /**
     * 单位校验
     * @param appSysManager
     * @return
     */
    private Result<String> orgValidityValidate(AppSysManager appSysManager,List<BaseKoalOrg> baseKoalOrgs){
        String orgCode = appSysManager.getDepartmentGuid();
        if(null == baseKoalOrgs || baseKoalOrgs.size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取所有单位数据为空");
        }
        BaseKoalOrg org = getOrgByCode(baseKoalOrgs,orgCode);
        if(null == org){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"单位编码不存在");
        }
        appSysManager.setDepartmentName(org.getName()); // 重新更新单位名称
        return ResultUtil.success("success");
    }
    private BaseKoalOrg getOrgByCode(List<BaseKoalOrg> baseKoalOrgs, String orgCode) {
        for(BaseKoalOrg org : baseKoalOrgs){
            if(orgCode.equals(org.getCode())){
                return  org;
            }
        }
        return null;
    }

    /**
     * 涉密等级校验
     * @param appSysManager
     * @return
     */
    private Result<String> secretLevelValidityValidate(AppSysManager appSysManager,List<BaseDictAll> baseDictAlls) {
        if(null == baseDictAlls || baseDictAlls.size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"获取应用系统涉密等级数据为空");
        }
        String secretLevel = appSysManager.getSecretLevel();
        BaseDictAll data = getSecretLevel(baseDictAlls,secretLevel);
        if(null == data){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密等级不存在");
        }
        return ResultUtil.success("success");
    }

    private BaseDictAll getSecretLevel(List<BaseDictAll> baseDictAlls, String secretLevel) {
        for(BaseDictAll data : baseDictAlls){
            if(secretLevel.equals(data.getCode())){
                return  data;
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

    private List<AppSysManagerSynchVo> getAppNoDuplicates(String appNo, List<AppSysManagerSynchVo> appDatasCopy) {
        List<AppSysManagerSynchVo> list = new ArrayList<>();
        for(AppSysManagerSynchVo data : appDatasCopy){
            if(appNo.equalsIgnoreCase(data.getAppNo())){
                list.add(data);
            }
        }
        return  list;
    }

}

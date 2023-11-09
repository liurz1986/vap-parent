package com.vrv.vap.alarmdeal.business.appsys.datasync.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppSysManagerSynchVo;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.jpa.web.Result;

import java.util.List;

public interface AppVerifyValidateService {

    /**
     * 数据校验：
     *  1.单位名称、涉密等级、应用名称、涉密厂商、应用域名必填
     *  2.单位名称、涉密等级有效性
     * @param appSysManager
     * @param baseKoalOrgs
     * @param baseDictAlls
     * @return
     */
    public Result<String> appDataValidate(AppSysManager appSysManager, List<BaseKoalOrg> baseKoalOrgs, List<BaseDictAll> baseDictAlls);

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
    public Result<String> appBaseValidate(AppSysManagerSynchVo data, List<TbConf> tbConfs);


    /**
     * 数据去重处理(应用编号)
     * @param appDatas
     * @return
     */
    public List<AppSysManagerSynchVo> duplicateDatahandle(List<AppSysManagerSynchVo> appDatas);
}

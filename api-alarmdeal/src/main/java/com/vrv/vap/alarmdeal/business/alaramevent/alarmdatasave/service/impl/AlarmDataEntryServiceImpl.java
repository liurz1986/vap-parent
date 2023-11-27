package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.AssetExtendBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventLogDstBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmBaseDataService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataEntryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AppSysManagerCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月18日 18:01
 */
@Service
public class AlarmDataEntryServiceImpl implements AlarmDataEntryService {
    private final Logger logger = LoggerFactory.getLogger(AlarmDataEntryServiceImpl.class);

    @Autowired
    private AlarmDataHandleService alarmDataHandleService;

    @Autowired
    private AlarmBaseDataService alarmBaseDataService;

    @Autowired
    private EventTabelService eventTabelService;

    /**
     * 原始日志表 信息
     */
    private Map<String, List<EventTable>> eventTableMap = new ConcurrentHashMap<>();

    /**
     * 告警规则表 信息
     */
    private Map<String, List<RiskEventRule>> riskEventRuleMap = new ConcurrentHashMap<>();

    /**
     * 事件分类表 信息
     */
    private Map<String, List<EventCategory>> eventCategoryMap = new ConcurrentHashMap<>();

    /**
     * 告警事件配置
     */
    private Map<String, List<EventAlarmSetting>> eventAlarmSettingMap = new ConcurrentHashMap<>();

    /**
     * 初始化数据
     */
    public void initEventData() {
        // 原始日志信息
        eventTableMap = alarmDataHandleService.getEventTableMap();

        // 事件告警规则信息
        riskEventRuleMap = alarmDataHandleService.getRiskEventRuleMap();

        // 事件分类信息
        eventCategoryMap = alarmDataHandleService.getEventCategoryMap();

        // 事件告警配置
        eventAlarmSettingMap = alarmDataHandleService.getEventAlarmSettingMap();
    }

    /**
     * 处理数据
     *
     * @return List
     */
    @Override
    public List<AlarmEventAttribute> handleAlarmDataEntry(List<WarnResultLogTmpVO> warnResultLogTmpVos) {
        initEventData();
        logger.info("AlarmDataSaveJob handleAlarmData start data");
        List<AlarmEventAttribute> result = new ArrayList<>();
        for(WarnResultLogTmpVO warnResultLogTmpVo : warnResultLogTmpVos){
            // 告警结果数据
            AlarmEventAttribute doc = new AlarmEventAttribute();
            handleAlarmBaseData(warnResultLogTmpVo,doc);
            handleAlarmLogData(doc);
            result.add(doc);
        }
        logger.debug("AlarmDataSaveJob handleAlarmData end result size={}", result.size());
        return  result;
    }

    /**
     * 处理告警基础信息
     * @param warnResultLogTmpVo
     * @param doc
     */
    public void handleAlarmBaseData(WarnResultLogTmpVO warnResultLogTmpVo, AlarmEventAttribute doc) {
        // 1、处理基础数据
        handleBaseData(warnResultLogTmpVo, doc);
        // 2、补充日志数据
        handleLogData(doc, warnResultLogTmpVo);
        logger.info("AlarmDataSaveJob handleAlarmData saveBaseData getLogData success");
    }

    /**
     * 处理告警日志信息合设备信息
     * @param doc
     */
    public void handleAlarmLogData( AlarmEventAttribute doc) {
        // 3、补充日志中的基础数据
        EventLogDstBean eventLogDstBean = new EventLogDstBean();
        saveBaseData(eventLogDstBean, doc);
        alarmDataHandleService.handleLogData(eventLogDstBean, doc);
        logger.info("AlarmDataSaveJob handleAlarmData saveBaseData");
        countForLog(doc);
        String aggField = doc.getEventType() + "@#@#@" + doc.getEventName() + "@#@#@" + doc.getDstIps() + "@#@#@" + doc.getSrcIps();
        doc.setAggField(aggField);
    }

    public void handleLogData(AlarmEventAttribute doc, WarnResultLogTmpVO warnResultLogTmpVO) {
        Map<String, String[]> logMap = warnResultLogTmpVO.getIdRoom();
        List<LogIdVO> logs = new LinkedList<>();
        for (Map.Entry<String, String[]> entry : logMap.entrySet()){
            LogIdVO logIdVO = new LogIdVO();
            String key = entry.getKey();
            String[] ids = entry.getValue();
            logIdVO.setIndexName(key);
            logIdVO.setEventTableName(key);
            logIdVO.setIds(Arrays.asList(ids));
            logs.add(logIdVO);
        }
        doc.setLogs(logs);
    }

    /**
     * 通过日志信息统计
     *
     * @param doc
     */
    private void countForLog(AlarmEventAttribute doc) {
        doc.setDeviceCount(doc.getDeviceInfos() == null ? 0 : doc.getDeviceInfos().size());
        doc.setStaffNum(doc.getStaffInfos() == null ? 0 : doc.getStaffInfos().size());
        doc.setDeviceAppCount(doc.getApplicationInfos() == null ? 0 : doc.getApplicationInfos().size());
        doc.setFileCount(doc.getFileInfos() == null ? 0 : doc.getFileInfos().size());
    }

    /**
     * 保存日志基础数据
     *
     * @param doc
     */
    public void saveBaseData(EventLogDstBean eventLogDstBean, AlarmEventAttribute doc) {
        queryBaseDataByIp(eventLogDstBean, doc.getSrcIps(), true);
        if (StringUtils.isNotBlank(doc.getDstIps())) {
            queryBaseDataByIp(eventLogDstBean, doc.getDstIps(), false);
        }
    }

    public void queryBaseDataByIp(EventLogDstBean eventLogDstBean, String ip, boolean isSrc) {
        if (isSrc) {
            // 源设备
            // 设备信息
            handleSrcAssetData(eventLogDstBean, ip);
        } else {
            // 目标设备
            // 设备信息
            handleDstAssetData(eventLogDstBean, ip);
        }
    }

    /**
     * 补全目的设备信息
     *
     * @param eventLogDstBean
     * @param ip
     */
    private void handleDstAssetData(EventLogDstBean eventLogDstBean, String ip) {
        AssetCacheVo asset = alarmBaseDataService.queryAssetByIp(ip);
        if (asset != null) {
            handleDstAsset(eventLogDstBean, asset);
            // 设备类型信息
            String assetType = alarmBaseDataService.queryAssetByAssetId(asset.getAssetType());
            eventLogDstBean.setDstStdDevType(assetType);
            // 设备扩展信息
            handleDstAssetExtend(eventLogDstBean, asset);
            // 用户信息
            handleDstUser(eventLogDstBean, asset);
            // 安全域信息
            handleDstAssetSerurity(eventLogDstBean, asset);
            // 应用信息
            handleDstAppSys(eventLogDstBean, asset);
        }
    }

    /**
     * 设置原设备信息
     *
     * @param eventLogDstBean
     * @param ip
     */
    private void handleSrcAssetData(EventLogDstBean eventLogDstBean, String ip) {
        AssetCacheVo asset = alarmBaseDataService.queryAssetByIp(ip);
        if (asset != null) {
            handleSrcAsset(eventLogDstBean, asset);
            // 设备类型信息
            String assetType = alarmBaseDataService.queryAssetByAssetId(asset.getAssetType());

            eventLogDstBean.setStdDevType(assetType);
            // 设备扩展信息
            handleSrcAssetExtend(eventLogDstBean, asset);
            // 用户信息
            handleSrcUser(eventLogDstBean, asset);
            // 安全域信息
            handleSrcAssetSerurity(eventLogDstBean, asset);
            // 应用信息
            handleSrcAssetAppSys(eventLogDstBean, asset);
        }
    }

    /**
     * 设置 目的设备 应用信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleDstAppSys(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        AppSysManagerCacheVo appSysManager = alarmBaseDataService.qyeryAppSysManagerByAppId(asset.getGuid());
        if (appSysManager != null) {
            eventLogDstBean.setDstStdSysName(appSysManager.getAppName());
            eventLogDstBean.setDstStdSysId(appSysManager.getAppNo());
        }
    }

    /**
     * 设置目的设备安全域信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleDstAssetSerurity(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        BaseSecurityDomain baseSecurityDomain = alarmBaseDataService.queryDomianById(asset.getSecurityGuid());
        if (baseSecurityDomain != null) {
            eventLogDstBean.setDstStdDevSafetyMarignName(baseSecurityDomain.getDomainName());
        }
    }

    /**
     * 设置目的设备人员信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleDstUser(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        BasePersonZjg basePersonZjg = alarmBaseDataService.queryPersonByUserNo(asset.getResponsibleCode());
        if (basePersonZjg != null) {
            eventLogDstBean.setDstStdUserStation(basePersonZjg.getPersonRank());
            eventLogDstBean.setDstStdUserRole(basePersonZjg.getPersonType());
            eventLogDstBean.setDstStdUserNo(basePersonZjg.getUserNo());
            eventLogDstBean.setDstStdUserName(basePersonZjg.getUserName());
            eventLogDstBean.setDstStdUserLevel(String.valueOf(basePersonZjg.getSecretLevel()));
            eventLogDstBean.setDstStdUserDepartment(basePersonZjg.getOrgName());
            eventLogDstBean.setDstPersonType(basePersonZjg.getPersonType());
        }
    }

    /**
     * 设置目的设备扩展信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleDstAssetExtend(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        AssetExtendBean assetExtend = alarmBaseDataService.queryAssetExtendData(asset.getGuid());
        if (assetExtend != null) {
            eventLogDstBean.setDstStdDevBrandModel("-1");
            eventLogDstBean.setDstStdDevHardwareIdentification(assetExtend.getExtendDiskNumber());
            eventLogDstBean.setDstStdDevSoftwareVersion(assetExtend.getSysSno());
            eventLogDstBean.setDstStdDevOsType(assetExtend.getExtendSystem());
        }
    }

    /**
     * 设置目的设备基础信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleDstAsset(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        eventLogDstBean.setDstStdDevId(asset.getGuid());
        eventLogDstBean.setDstStdDevIp(asset.getIp());
        eventLogDstBean.setDstStdDevHardwareModel("-1");
        eventLogDstBean.setDstStdDevLevel(String.valueOf(asset.getEquipmentIntensive()));
        eventLogDstBean.setDstStdDevMac(asset.getMac());
        eventLogDstBean.setDstStdDevName(asset.getName());
        eventLogDstBean.setDstStdOrgName(asset.getOrgName());
        eventLogDstBean.setDstStdOrgCode(asset.getOrgCode());
        eventLogDstBean.setDstStdDevNetTime("-1");
    }

    /**
     * 设置源设备应用信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleSrcAssetAppSys(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        AppSysManagerCacheVo appSysManager = alarmBaseDataService.qyeryAppSysManagerByAppId(asset.getGuid());
        if (appSysManager != null) {
            eventLogDstBean.setStdSysId(appSysManager.getAppNo());
            eventLogDstBean.setStdSysName(appSysManager.getAppName());
        }
    }

    /**
     * 设置源设备安全域信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleSrcAssetSerurity(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        BaseSecurityDomain baseSecurityDomain = alarmBaseDataService.queryDomianById(asset.getSecurityGuid());
        if (baseSecurityDomain != null) {
            eventLogDstBean.setSrcStdDevSafetyMarignName(baseSecurityDomain.getDomainName());
            eventLogDstBean.setStdDevSafetyMarign(baseSecurityDomain.getCode());
        }
    }

    /**
     * 设置源设备人员信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleSrcUser(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        BasePersonZjg basePersonZjg = alarmBaseDataService.queryPersonByUserNo(asset.getResponsibleCode());
        if (basePersonZjg != null) {
            eventLogDstBean.setStdUserDepartment(basePersonZjg.getOrgName());
            eventLogDstBean.setStdUserLevel(String.valueOf(basePersonZjg.getSecretLevel()));
            eventLogDstBean.setStdUserName(basePersonZjg.getUserName());
            eventLogDstBean.setStdUserNo(basePersonZjg.getUserNo());
            eventLogDstBean.setStdUserRole(basePersonZjg.getPersonType());
            eventLogDstBean.setStdUserstation(basePersonZjg.getPersonRank());
            eventLogDstBean.setStdUserType(basePersonZjg.getPersonType());
        }
    }

    /**
     * 设置源设备扩展信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleSrcAssetExtend(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        AssetExtendBean assetExtend = alarmBaseDataService.queryAssetExtendData(asset.getGuid());
        if (assetExtend != null) {
            eventLogDstBean.setStdDevBrandModel(assetExtend.getExtendVersionInfo());
            eventLogDstBean.setStdDevHardwareIdentification(assetExtend.getExtendDiskNumber());
            eventLogDstBean.setStdDevOsType(assetExtend.getExtendSystem());
            eventLogDstBean.setStdDevSoftwareVersion(assetExtend.getSysSno());
        }
    }

    /**
     * 补充源设备信息
     *
     * @param eventLogDstBean
     * @param asset
     */
    private void handleSrcAsset(EventLogDstBean eventLogDstBean, AssetCacheVo asset) {
        eventLogDstBean.setStdDevId(asset.getGuid());
        eventLogDstBean.setStdDevIp(asset.getIp());
        eventLogDstBean.setStdDevMac(asset.getMac());
        eventLogDstBean.setStdDevLevel(String.valueOf(asset.getEquipmentIntensive()));
        eventLogDstBean.setStdDevName(asset.getName());
        eventLogDstBean.setStdDevNetTime("-1");
        eventLogDstBean.setStdOrgName(asset.getOrgName());
        eventLogDstBean.setStdOrgCode(asset.getOrgCode());
        eventLogDstBean.setStdDevHardwareModel("-1");
    }

    /**
     * 处理基础数据
     *
     * @param warnResultLogTmpVO
     * @param doc
     */
    public void handleBaseData(WarnResultLogTmpVO warnResultLogTmpVO, AlarmEventAttribute doc) {
        // 1、处理基础数据
        alarmDataHandleService.handleBaseData(warnResultLogTmpVO, doc);

        String ruleCode = warnResultLogTmpVO.getRuleCode().split("-")[0];
        List<RiskEventRule> riskEventRules = riskEventRuleMap.get(ruleCode);
        RiskEventRule riskEventRule = riskEventRules.get(0);
        List<EventCategory> eventCategorys = eventCategoryMap.get(riskEventRule.getRiskEventId());
        EventCategory eventCategory = eventCategorys.get(0);
        logger.info("AlarmDataSaveJob haveLogData riskEventRuleMap eventCategory success");
        // 2、补全分类信息
        alarmDataHandleService.formEventCategory(riskEventRule, eventCategory, doc);
        // 3、补全认证信息
        alarmDataHandleService.formAuthData(eventAlarmSettingMap, eventCategoryMap, doc);
        // 4、补全告警状态
        alarmDataHandleService.formAlarmStatus(warnResultLogTmpVO.getStatusEnum(), doc);
        logger.info("AlarmDataSaveJob haveLogData success");
    }
}

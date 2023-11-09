package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataForLogService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.UnitInfo;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl.EventSearchCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月28日 10:12
 */
@Service
public class AlarmDataForLogServiceImpl implements AlarmDataForLogService {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(AlarmDataForLogServiceImpl.class);

    /**
     * 处理日志数据中文件信息
     *
     * @param logDstBean
     * @param doc
     */
    @Override
    public void handleFileData(EventLogDstBean logDstBean, AlarmEventAttribute doc) {
        //文件
        List<FileInfo> fileInfos = doc.getFileInfos();
        if (fileInfos == null) {
            doc.setFileInfos(new ArrayList<>());
            fileInfos = doc.getFileInfos();
        }
        List<FileInfo> filesTemp = this.getFileInfos(logDstBean);
        if (filesTemp != null && !filesTemp.isEmpty()) {
            fileInfos.addAll(filesTemp);
        }
    }

    /**
     * 处理日志数据中部门信息
     *
     * @param logDstBean
     * @param doc
     */
    @Override
    public void handleUnitInfoData(EventLogDstBean logDstBean, AlarmEventAttribute doc) {

        //通过日志  获取设备信息
        UnitInfo unitInfo = this.getUnitInfo(doc.getPrincipalIp(), logDstBean);
        List<UnitInfo> unitInfos = new ArrayList<>();
        if (unitInfo != null) {
            if (doc.getUnitList() == null || doc.getUnitList().size() < 1) {
                EventSearchCache.updateUnitInfo(unitInfo);
                unitInfos.add(unitInfo);
            }
        }
        doc.setUnitList(unitInfos);
        logger.debug("AlarmDataSaveJob handleAlarmData handleUnitInfoData success");
    }

    /**
     * 处理日志数据中人员信息
     *
     * @param logDstBean
     * @param doc
     */
    @Override
    public void handleStaffInfosData(EventLogDstBean logDstBean, AlarmEventAttribute doc) {
        //人员赋值
        List<StaffInfo> staffInfos = doc.getStaffInfos();
        if (staffInfos == null) {
            doc.setStaffInfos(new ArrayList<>());
            staffInfos = doc.getStaffInfos();
        }

        List<StaffInfo> staffsTemp = getStaffInfos(logDstBean);
        if (staffsTemp != null && !staffsTemp.isEmpty()) {
            staffInfos.addAll(staffsTemp);
        }

        String staffInfoNo = "";
        // 获取关联设备责任人code
        List<DeviceInfo> deviceInfos = getDeviceInfos(logDstBean);
        for (DeviceInfo deviceInfo : deviceInfos) {
            if (deviceInfo.getDeviceIp().equals(doc.getPrincipalIp())) {
                PersonLiable personLiable = deviceInfo.getPersonLiable();
                if (personLiable != null && null != personLiable.getPersonLiableCode()) {
                    staffInfoNo = personLiable.getPersonLiableCode();
                }
            }
        }

        List<StaffInfo> staffInfoList = new ArrayList<>();
        // 匹配到的人员，设置是否关联，用于显示
        for (StaffInfo staffInfo : staffInfos) {
            if (staffInfoNo.equals(staffInfo.getStaffNo()) && null != staffInfo.getStaffRole()) {
                staffInfo.setIsRelation(1);
                staffInfoList.add(staffInfo);
            }
        }
        staffInfoList.stream().distinct();
        doc.setRelatedStaffInfos(staffInfoList);
        logger.info("AlarmDataSaveJob handleAlarmData handleStaffInfosData success");
    }

    /**
     * 处理日志数据中设备信息
     *
     * @param logDstBean
     * @param doc
     */
    @Override
    public void handleDeviceInfosData(EventLogDstBean logDstBean, AlarmEventAttribute doc) {
        //设备赋值
        List<DeviceInfo> deviceInfos = doc.getDeviceInfos();
        if (doc.getDeviceInfos() == null) {
            doc.setDeviceInfos(new ArrayList<>());
            deviceInfos = doc.getDeviceInfos();
        }

        List<DeviceInfo> devicesTemp = this.getDeviceInfos(logDstBean);
        if (devicesTemp != null && !devicesTemp.isEmpty()) {
            for (DeviceInfo device : devicesTemp) {
                if (device == null || StringUtils.isEmpty(device.getDeviceIp())) {
                    continue;
                }
                deviceInfos.add(device);
            }
        }
        logger.info("AlarmDataSaveJob handleAlarmData handleDeviceInfosData success");
    }

    /**
     * 处理日志数据中应用信息
     *
     * @param logDstBean
     * @param doc
     */
    @Override
    public void handleApplicationInfosData(EventLogDstBean logDstBean, AlarmEventAttribute doc) {
        //应用系统
        List<ApplicationInfo> applicationInfos = doc.getApplicationInfos();
        if (applicationInfos == null) {
            doc.setApplicationInfos(new ArrayList<>());
            applicationInfos = doc.getApplicationInfos();
        }
        List<ApplicationInfo> appsTemp = this.getApplicationInfos(logDstBean);
        if (appsTemp != null && !appsTemp.isEmpty()) {
            applicationInfos.addAll(appsTemp);
        }
        logger.info("AlarmDataSaveJob handleAlarmData handleApplicationInfosData success");
    }

    public void handleExtention(EventLogDstBean logDstBean, AlarmEventAttribute doc) {
        List<ThreeinOneInfo> threeinOneInfos=new ArrayList<>();
        ThreeinOneInfo threeinOneInfo=new ThreeinOneInfo();
        threeinOneInfo.setThreeinOneNum("未知，待补全");
        threeinOneInfo.setThreeinOneVersion("未知，待补全");
        threeinOneInfos.add(threeinOneInfo);
        doc.setExtention(threeinOneInfos);
    }

    public List<ApplicationInfo> getApplicationInfos(EventLogDstBean logDstBean) {
        List<ApplicationInfo> list = new ArrayList<>();
        ApplicationInfo stdApplicationInfo = handleStdApplicationInfo(logDstBean);
        ApplicationInfo dstApplicationInfo = handleDtdApplicationInfo(logDstBean);
        if (stdApplicationInfo != null) {
            list.add(stdApplicationInfo);
        }
        if (dstApplicationInfo != null) {
            list.add(dstApplicationInfo);
        }
        return list;
    }

    /**
     * 处理源应用信息
     *
     * @return
     */
    public ApplicationInfo handleStdApplicationInfo(EventLogDstBean logDstBean) {
        if (StringUtils.isEmpty(logDstBean.getStdSysId())) {
            return null;
        }

        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApplicationIp(logDstBean.getStdSysIp());
        applicationInfo.setApplicationId(logDstBean.getStdSysId());
        applicationInfo.setApplicationLabel(logDstBean.getStdSysName());
        applicationInfo.setApplicationPort(logDstBean.getStdSysPort() != null ? logDstBean.getStdSysPort() : "");
        applicationInfo.setApplicationArg(logDstBean.getStdSysParameter() != null ? logDstBean.getStdSysParameter() : "");
        applicationInfo.setApplicationProtocal(logDstBean.getStdSysProtocal() != null ? logDstBean.getStdSysProtocal() : "");
        return applicationInfo;
    }

    /**
     * 处理目的应用信息
     *
     * @return
     */
    public ApplicationInfo handleDtdApplicationInfo(EventLogDstBean logDstBean) {
        if (StringUtils.isEmpty(logDstBean.getDstStdSysId())) {
            return null;
        }

        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApplicationIp(logDstBean.getDstStdSysIp());
        applicationInfo.setApplicationId(logDstBean.getDstStdSysId());
        applicationInfo.setApplicationLabel(logDstBean.getDstStdSysName());
        // 提供源数据中不存在ApplicationPort、ApplicationArg、ApplicationProtocal
        applicationInfo.setApplicationPort("");
        applicationInfo.setApplicationArg("");
        applicationInfo.setApplicationProtocal("");
        return applicationInfo;
    }

    public List<DeviceInfo> getDeviceInfos(EventLogDstBean logDstBean) {
        List<DeviceInfo> list = new ArrayList<>();
        DeviceInfo stdDeviceInfo = handlerStdDeviceDate(logDstBean);
        DeviceInfo dstDeviceInfo = handlerDstDeviceDate(logDstBean);
        if (stdDeviceInfo != null) {
            list.add(stdDeviceInfo);
        }
        if (dstDeviceInfo != null) {
            list.add(dstDeviceInfo);
        }
        return list;
    }

    /**
     * 处理源设备信息
     *
     * @return DeviceInfo
     */
    public DeviceInfo handlerStdDeviceDate(EventLogDstBean logDstBean) {
        if (StringUtils.isEmpty(logDstBean.getStdDevIp())) {
            return null;
        }
        DeviceInfo device = new DeviceInfo();
        device.setDeviceMac(logDstBean.getStdDevMac());
        device.setDeviceDiskSeq(logDstBean.getStdDevHardwareIdentification());
        device.setDeviceBrand(logDstBean.getStdDevBrandModel());
        device.setDeviceIp(logDstBean.getStdDevIp());
        device.setDeviceModel(logDstBean.getStdDevHardwareModel());
        device.setDeviceName(logDstBean.getStdDevName());
        device.setDeviceNetTime(logDstBean.getStdDevNetTime());
        device.setDeviceOs(logDstBean.getStdDevOsType());
        device.setDeviceSecurityDomain(logDstBean.getSrcStdDevSafetyMarignName());
        device.setDeviceVersion(logDstBean.getStdDevSoftwareVersion());
        device.setDeviceLevel(logDstBean.getStdDevLevel());
        device.setDeviceType(logDstBean.getStdDevType());
        device.setDeviceId(logDstBean.getStdDevId());
        device.setOrgCode(logDstBean.getStdOrgCode());
        device.setOrgName(logDstBean.getStdOrgName());

        PersonLiable personLiable = new PersonLiable();
        personLiable.setPersonLiableCode(logDstBean.getStdUserNo());
        personLiable.setPersonLiableName(logDstBean.getStdUserName());
        personLiable.setPersonLiableOrg(logDstBean.getStdUserDepartment());
        personLiable.setPersonLiableRole(logDstBean.getStdUserRole());

        device.setPersonLiable(personLiable);
        return device;
    }

    /**
     * 处理目的设置信息
     *
     * @return DeviceInfo
     */
    public DeviceInfo handlerDstDeviceDate(EventLogDstBean logDstBean) {
        if (StringUtils.isEmpty(logDstBean.getDstStdDevIp())) {
            return null;
        }
        DeviceInfo device = new DeviceInfo();
        device.setDeviceMac(logDstBean.getDstStdDevMac());
        device.setDeviceDiskSeq(logDstBean.getDstStdDevHardwareIdentification());
        device.setDeviceBrand(logDstBean.getDstStdDevBrandModel());
        device.setDeviceIp(logDstBean.getDstStdDevIp());
        device.setDeviceModel(logDstBean.getDstStdDevHardwareModel());
        device.setDeviceName(logDstBean.getDstStdDevName());
        device.setDeviceNetTime(logDstBean.getDstStdDevNetTime());
        device.setDeviceOs(logDstBean.getDstStdDevOsType());
        device.setDeviceSecurityDomain(logDstBean.getDstStdDevSafetyMarignName());
        device.setDeviceVersion(logDstBean.getDstStdDevSoftwareVersion());
        device.setDeviceLevel(logDstBean.getDstStdDevLevel());
        device.setDeviceType(logDstBean.getDstStdDevType());
        device.setDeviceId(logDstBean.getDstStdDevId());
        device.setOrgCode(logDstBean.getDstStdOrgCode());
        device.setOrgName(logDstBean.getDstStdOrgName());

        PersonLiable personLiable = new PersonLiable();
        personLiable.setPersonLiableCode(logDstBean.getDstStdUserNo());
        personLiable.setPersonLiableName(logDstBean.getDstStdUserName());
        personLiable.setPersonLiableOrg(logDstBean.getDstStdUserDepartment());
        personLiable.setPersonLiableRole(logDstBean.getDstStdUserRole());

        device.setPersonLiable(personLiable);
        return device;
    }

    public List<StaffInfo> getStaffInfos(EventLogDstBean logDstBean) {
        List<StaffInfo> list = new ArrayList<>();
        StaffInfo stdStaffInfo = handleStdStaffInfo(logDstBean);
        StaffInfo dstStaffInfo = handleDtdStaffInfo(logDstBean);

        if (stdStaffInfo != null) {
            list.add(stdStaffInfo);
        }
        if (dstStaffInfo != null) {
            list.add(dstStaffInfo);
        }
        if (list.size() != 0) {
            // 多个人员
            List<StaffInfo> results =
                    list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(distinctByKeyFunction()))), ArrayList::new));
            return results;
        }
        return list;
    }

    public static Function<StaffInfo, String> distinctByKeyFunction() {
        return (StaffInfo staffInfo) -> staffInfo.getStaffNo() + "-" + staffInfo.getStaffName();
    }

    /**
     * 处理源人员信息
     *
     * @return StaffInfo
     */
    public StaffInfo handleStdStaffInfo(EventLogDstBean logDstBean) {
        if (StringUtils.isEmpty(logDstBean.getStdUserNo())) {
            return null;
        }

        StaffInfo staff = new StaffInfo();
        staff.setStaffAccount(logDstBean.getStdUserNo());
        staff.setStaffDepartment(logDstBean.getStdUserDepartment());
        staff.setStaffLevel(logDstBean.getStdUserLevel());
        staff.setStaffName(logDstBean.getStdUserName());
        staff.setStaffNo(logDstBean.getStdUserNo());
        staff.setStaffRole(logDstBean.getStdUserRole());
        staff.setStaffPost(logDstBean.getStdUserstation());
        staff.setStaffCompany(logDstBean.getStdOrgName());
        //补全人员类型
        staff.setStaffType(logDstBean.getStdUserType());
        return staff;
    }

    /**
     * 处理目的人员信息
     *
     * @return StaffInfo
     */
    public StaffInfo handleDtdStaffInfo(EventLogDstBean logDstBean) {
        if (StringUtils.isEmpty(logDstBean.getDstStdUserNo())) {
            return null;
        }

        StaffInfo staff = new StaffInfo();
        staff.setStaffAccount(logDstBean.getDstStdUserNo());
        staff.setStaffDepartment(logDstBean.getDstStdUserDepartment());
        staff.setStaffLevel(logDstBean.getDstStdUserLevel());
        staff.setStaffName(logDstBean.getDstStdUserName());
        staff.setStaffNo(logDstBean.getDstStdUserNo());
        staff.setStaffRole(logDstBean.getDstStdUserRole());
        staff.setStaffPost(logDstBean.getDstStdUserStation());
        staff.setStaffCompany(logDstBean.getStdOrgName());
        //补齐人员类型
        staff.setStaffType(logDstBean.getDstPersonType());
        return staff;
    }

    public List<FileInfo> getFileInfos(EventLogDstBean logDstBean) {
        List<FileInfo> list = new ArrayList<>();
        FileInfo fileInfo = handleStdFileInfo(logDstBean);
        if (fileInfo != null) {
            list.add(fileInfo);
        }
        return list;
    }

    /**
     * 处理文件数据
     *
     * @return FileInfo
     */
    public FileInfo handleStdFileInfo(EventLogDstBean logDstBean) {
        if (StringUtils.isEmpty(logDstBean.getFileMd5())) {
            return null;
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileBusiness(logDstBean.getBusinessList());
        fileInfo.setFileDataDownloadAddress(logDstBean.getSrcFileDir());
        fileInfo.setFileDataToAddress(logDstBean.getSrcFileDir());
        fileInfo.setFileMd5(logDstBean.getFileMd5());
        if (!StringUtils.isEmpty(logDstBean.getFileName())) {

            fileInfo.setFileName(logDstBean.getFileName());
        }
        fileInfo.setFileSecurityLevel(logDstBean.getFileLevel());
        fileInfo.setFileStoragePath(logDstBean.getFileName());
        fileInfo.setFileType(logDstBean.getFileType());
        return fileInfo;
    }

    public UnitInfo getUnitInfo(String principalIp, EventLogDstBean logDstBean) {
        if (!StringUtils.isEmpty(principalIp) && principalIp.equals(logDstBean.getDstStdDevIp())) {
            UnitInfo unitInfo = new UnitInfo();
            unitInfo.setUnitGeoIdent(logDstBean.getDstStdOrgCode());
            unitInfo.setUnitDepartName(logDstBean.getDstStdOrgName());
            if (StringUtils.isBlank(logDstBean.getDstStdOrgCode())) {
                return null;
            }
            return unitInfo;
        } else {
            UnitInfo unitInfo = new UnitInfo();
            unitInfo.setUnitGeoIdent(logDstBean.getStdOrgCode());
            unitInfo.setUnitDepartName(logDstBean.getStdOrgName());
            if (StringUtils.isBlank(logDstBean.getStdOrgCode())) {
                return null;
            }
            return unitInfo;
        }
    }
}

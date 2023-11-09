package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.CollectorDataAccess;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.CollectorDataAccessService;
import com.vrv.vap.admin.service.SafeKitEventLogService;
import com.vrv.vap.admin.service.SyncBaseDataService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.util.LogSendUtil;
import com.vrv.vap.common.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author lilang
 * @date 2023/4/17
 * @description 安全套件事件日志
 */
@Service
public class SaveKitEventLogServiceImpl extends BaseDataProducerServiceImpl implements SafeKitEventLogService  {

    private static final Logger log = LoggerFactory.getLogger(SaveKitEventLogServiceImpl.class);

    private static final String KIT_CID = "99afdb174bb19a9e6b56df7420463bb5";

    @Autowired
    CollectorDataAccessService collectorDataAccessService;

    @Resource
    private SyncBaseDataService syncBaseDataService;

    @Resource
    SystemConfigService systemConfigService;

    @Override
    public void produce(SyncBaseData syncBaseData) {
        List<String> deptIds = this.getDept(syncBaseData);
        this.getKitTerminal(syncBaseData,deptIds);
    }

    public List<String> getDept(SyncBaseData syncBaseData) {
        List<String> deptIds = new ArrayList<>();
        String prefix = this.getUrlPrefix(syncBaseData);
        String url = prefix + "/api/getorglist?type=80" + this.getKitUrlSuffix(syncBaseData);
        try {
            log.info("请求地址：" + url);
            String result = HTTPUtil.GET(url,null);
            log.info("套件获取组织机构返回结果：" + result);
            if (StringUtils.isEmpty(result)) {
                log.info("获取组织机构失败");
                return deptIds;
            }
            Document document = DocumentHelper.parseText(result);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element depList = root.element("DeptList");
            if (depList != null) {
                List<Element> elements = depList.elements();
                for (Element item : elements) {
                    // 根节点能查到所有资产
                    if ("0".equals(item.elementText("ParentID"))) {
                        deptIds.add(item.elementText("DeptID"));
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }
        return deptIds;
    }

    public void getKitTerminal(SyncBaseData syncBaseData,List<String> deptIds) {
        Integer total = 0;
        String description = "";
        Integer status = 0;
        Date endTime = new Date();
        String key = this.getKitKey();
        if (org.apache.commons.lang.StringUtils.isEmpty(key)) {
            this.saveLog(syncBaseData,0,1,"套件秘钥未配置");
            return;
        }
        if (CollectionUtils.isNotEmpty(deptIds)) {
            for (String deptId : deptIds) {
                String prefix = this.getUrlPrefix(syncBaseData);
                String terminalUrl = prefix + "/api/gethostlist?type=81&DeptID=" + deptId + this.getKitUrlSuffix(syncBaseData);
                try {
                    log.info("请求地址：" + terminalUrl);
                    String terminalResult = HTTPUtil.GET(terminalUrl,null);
                    log.info("套件获取终端返回结果：" + terminalResult);
                    if (StringUtils.isEmpty(terminalResult)) {
                        log.info("获取终端失败");
                        return ;
                    }
                    Document document = DocumentHelper.parseText(terminalResult);
                    document.setXMLEncoding("utf-8");
                    Element root = document.getRootElement();
                    Element hostLists = root.element("HostList");
                    if (hostLists != null) {
                        List<Element> elements = hostLists.elements();
                        if (CollectionUtils.isNotEmpty(elements)) {
                            for (Element host : elements) {
                                total += this.sendEventLog(syncBaseData,host,endTime);
                            }
                        } else {
                            log.info("安全套件终端信息为空");
                        }
                    }
                } catch (Exception e) {
                    log.error("",e);
                }
            }
        } else {
            log.info("安全套件组织机构为空");
            description = "获取组织机构失败或组织机构为空";
            status = 1;
        }
        this.saveLog(syncBaseData,total,status,description);
        syncBaseData.setStartTime(endTime);
        syncBaseDataService.updateSelective(syncBaseData);
        return ;
    }

    public Integer sendEventLog(SyncBaseData syncBaseData,Element host,Date endDate) {
        Integer total = 0;
        // 主机唯一标识
        String hostCode = host.elementText("HostCode");
        // 部门编号
        String orgCode = host.elementText("DeptID");
        String startTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.format(syncBaseData.getStartTime(),DateUtil.DEFAULT_DATE_PATTERN),DateUtil.DEFAULT_DATE_PATTERN));
        String endTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.format(endDate,DateUtil.DEFAULT_DATE_PATTERN),DateUtil.DEFAULT_DATE_PATTERN));
        startTime = startTime.substring(0,startTime.length() - 3);
        endTime = endTime.substring(0,endTime.length() - 3);

        String dataName = "TERMINAL.STAINFO.DEVMGBSINFO,TERMINAL.STAINF.DEVHWBSINFO.NWBSINFO,TERMINAL.STAINF.DEVHWBSINFO.HDINFO" +
                ",TERMINAL.STAINF.DEVHWBSINFO.HWINFO,DYNAINFO.DYDEVHDINFO.DYHWUPINFO,DYNAINFO.DYSWINFO,DYNAINFO.DYSYS.DYMONIINFO" +
                ",DYNAINFO.DYSYS.NETININFO,DYNAINFO.DYPERIPH.PERIPHGEN,DYNAINFO.DYPERIPH.PRINTINFO,DYNAINFO.DYPERIPH.BURNINFO" +
                ",DYNAINFO.DYSEC.VIOOUTREACH,DYNAINFO.DYACCINFO.ACCLOGININFO,DYNAINFO.DYFILEPROINFO.MEDFILEOPINFO,DYNAINFO.DYFILEPROINFO.SHAREINFO" +
                ",HOSTSECPROTECT.INSTALL,DYNAINFO.DYSYS.DYPROCINFO,HOSTSECPROTECT.PROTECTIONSWCLIENTINFO.PROSWPROKILL,HOSTSECPROTECT.AUDITCONTRSTRA.AUDITSTRATEGY" +
                ",HOSTSECPROTECT.AUDITCONTRSTRA.CONTROLSTRATEGY,APPLICATION.COMAPP.FRONTDESKINFO";
        String prefix = this.getUrlPrefix(syncBaseData);
        String detailUrl = prefix + "/api/gethostselectedinfo?type=82&SubType=1&DeptID=" + orgCode +
                "&Hostcode=" + hostCode + "&Starttime=" + startTime + "&Endtime=" + endTime + "&DataName=" + dataName + this.getKitUrlSuffix(syncBaseData);
        try {
            log.info("请求地址：" + detailUrl);
            String operationResult = HTTPUtil.GET(detailUrl,null);
            log.info("套件获取事件日志返回结果：" + operationResult);
            if (StringUtils.isEmpty(operationResult)) {
                log.info("获取套件事件日志信息失败");
                return total;
            }
            Document document = DocumentHelper.parseText(operationResult);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element selectInfo = root.element("HostSelectedinfo");
            if (selectInfo != null) {
                List<Element> elements = selectInfo.elements();
                if (CollectionUtils.isNotEmpty(elements)) {
                    String address = "";
                    List<CollectorDataAccess> accessList = collectorDataAccessService.findByProperty(CollectorDataAccess.class,"cid",KIT_CID);
                    if (CollectionUtils.isNotEmpty(accessList)) {
                        CollectorDataAccess access = accessList.get(0);
                        address = access.getSrcIp() + ":" + access.getPort();
                    }
                    // 硬件变更审计日志
                    total += this.sendHardWareAuditLog(elements,host,address);
                    // 软件日志
                    total += this.sendSoftwareLog(elements,host,address);
                    // 主机异常告警日志
                    total += this.sendSaLog(elements,host,address);
                    // 违规介质操作日志
                    total += this.sendSemLog(elements,host,address);
                    // 打印刻录日志
                    total += this.sendPrintAuditLog(elements,host,address);
                    // 违规外联日志
                    total += this.sendViolationLog(elements,host,address);
                    // 终端登录日志
                    total += this.sendTerminalLoginLog(elements,host,address);
                    // 开关机日志
                    total += this.sendStartOrShutLog(elements,host,address);
                    // 文件监控日志
                    total += this.sendFileAuditLog(elements,host,address);
                    // 文件密级操作日志
                    total += this.sendMbLog(elements,host,address);
                    // 共享审计日志
                    total += this.sendShareLog(elements,host,address);
                    // 进程日志
                    total += this.sendProcessLog(elements,host,address);
                    // 策略变更日志
                    total += this.sendChangeStrategeLog(elements,host,address);
                    // 应用系统访问日志
                    total += this.sendAppLog(elements,host,address);
                }
            }
        } catch (Exception e) {
            log.info("获取套件事件日志信息异常");
            log.error("",e);
        }
        return total;
    }

    /**
     * 硬件变更审计日志
     * @param elements
     */
    public Integer sendHardWareAuditLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 硬件变更信息
        List<Element> dyhwups = this.getElementsByName(elements,"DYNAINFO.DYDEVHDINFO.DYHWUPINFOList");
        if (CollectionUtils.isNotEmpty(dyhwups)) {
            for (Element dyhwup : dyhwups) {
                List<Map> deviceInfoList = new ArrayList<>();
                String removeDevices = dyhwup.elementText("RemoveDevice");
                String linkDevices = dyhwup.elementText("LinkDevice");
                this.getHardwareAuditInfo(removeDevices,deviceInfoList);
                this.getHardwareAuditInfo(linkDevices,deviceInfoList);
                if (CollectionUtils.isNotEmpty(deviceInfoList)) {
                    for (Map deviceInfo : deviceInfoList) {
                        Map<String,Object> hardWareAuditMap = new HashMap<>();
                        hardWareAuditMap.put("report_log_type","DT029");
                        hardWareAuditMap.put("username",host.elementText("EmpName"));
                        hardWareAuditMap.put("dev_id",host.elementText("HostCode"));
                        hardWareAuditMap.put("dev_ip",host.elementText("DevIP"));
                        hardWareAuditMap.put("hardware_name",deviceInfo.get("name") != null ? deviceInfo.get("name") : "");
                        hardWareAuditMap.put("hardware_vendor",deviceInfo.get("manufacturer") != null ? deviceInfo.get("manufacturer") : "");
                        hardWareAuditMap.put("event_time",DateUtil.timeStamp2Date(dyhwup.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                        //字典转换
                        hardWareAuditMap.put("op_type",deviceInfo.get("opType"));
                        LogSendUtil.sendLogByUdp(JSON.toString(hardWareAuditMap),address);
                        total++;
                    }
                }
            }
        }
        return total;
    }

    /**
     * 获取新增、删除设备信息
     * @param changeDeviceInfo
     * @param deviceInfoList
     */
    private void getHardwareAuditInfo(String changeDeviceInfo,List<Map> deviceInfoList) {
        if (StringUtils.isNotEmpty(changeDeviceInfo) && changeDeviceInfo.indexOf("#") >= 0) {
            String[] changeDeviceArr = changeDeviceInfo.split("#");
            for (String changeDevice : changeDeviceArr) {
                Map map = new HashMap();
                if ("RemoveDevice".equals(changeDeviceInfo)) {
                    map.put("opType",1);
                } else {
                    map.put("opType",0);
                }
                if (StringUtils.isNotEmpty(changeDevice) && changeDevice.indexOf(";") >= 0) {
                    String[] changeFields = changeDevice.split(";");
                    for (String changeField : changeFields) {
                        if (StringUtils.isNotEmpty(changeField) && changeField.indexOf(":") >= 0) {
                            String[] changeFieldArr = changeField.split(":");
                            if ("设备类型".equals(changeFieldArr[0].trim())) {
                                map.put("name",changeFieldArr[1]);
                            }
                            if ("供应商".equals(changeFieldArr[0].trim())) {
                                map.put("manufacturer",changeFieldArr[1]);
                            }
                        }
                    }
                }
                deviceInfoList.add(map);
            }
        }
    }

    /**
     * 软件日志
     * @param elements
     */
    public Integer sendSoftwareLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 设备软件信息
        List<Element> dysws = this.getElementsByName(elements,"DYNAINFO.DYSWINFOList");
        // 安全保密产品安装情况
        List<Element> installs = this.getElementsByName(elements,"HOSTSECPROTECT.INSTALLList");
        if (CollectionUtils.isNotEmpty(dysws)) {
            for (Element dysw : dysws) {
                Map<String,Object> softwareMap = new HashMap<>();
                softwareMap.put("report_log_type","DT004");
                softwareMap.put("username",host.elementText("EmpName"));
                softwareMap.put("dev_id",host.elementText("HostCode"));
                softwareMap.put("dev_ip",host.elementText("DevIP"));

                softwareMap.put("software_name",dysw.elementText("DisplayName"));
                // 字典转换
                softwareMap.put("op_type",getSoftwareOptype(dysw.elementText("SWUPAction")));
                softwareMap.put("event_time",DateUtil.timeStamp2Date(dysw.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                LogSendUtil.sendLogByUdp(JSON.toString(softwareMap),address);
                total++;
            }
        }
        if (CollectionUtils.isNotEmpty(installs)) {
            for (Element install : installs) {
                Map<String,Object> softwareMap = new HashMap<>();
                softwareMap.put("report_log_type","DT004");
                softwareMap.put("username",host.elementText("EmpName"));
                softwareMap.put("dev_id",host.elementText("HostCode"));
                softwareMap.put("dev_ip",host.elementText("DevIP"));
                softwareMap.put("software_name",install.elementText("ProductName"));
                if (StringUtils.isNotEmpty(install.elementText("ClientsInsTime")) && !"--".equals(install.elementText("ClientsInsTime"))) {
                    softwareMap.put("event_time",install.elementText("ClientsInsTime"));
                    softwareMap.put("op_type","0");
                } else if (StringUtils.isNotEmpty(install.elementText("ClientsUninsTime")) && !"--".equals(install.elementText("ClientsUninsTime"))) {
                    softwareMap.put("event_time",install.elementText("ClientsUninsTime"));
                    softwareMap.put("op_type","1");
                } else {
                    softwareMap.put("event_time",DateUtil.timeStamp2Date(install.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                    softwareMap.put("op_type","");
                }
                LogSendUtil.sendLogByUdp(JSON.toString(softwareMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 软件操作类型
     * @param swuAction
     * @return
     */
    private String getSoftwareOptype(String swuAction) {
        String opType = "";
        switch (swuAction) {
            case "安装":
                opType = "0";
                break;
            case "卸载":
                opType = "1";
                break;
            default:
                break;
        }
        return opType;
    }

    /**
     * 主机异常告警日志
     * @param elements
     */
    public Integer sendSaLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 多操作识别和监控信息
        List<Element> dymonis = this.getElementsByName(elements,"DYNAINFO.DYSYS.DYMONIINFOList");
        // TODO 类型不存在
        // 网络接入信息
        List<Element> netins = this.getElementsByName(elements,"DYNAINFO.DYSYS.NETININFOList");
        if (CollectionUtils.isNotEmpty(dymonis)) {
            for (Element dymon : dymonis) {
                Map<String,Object> saMap = new HashMap<>();
                saMap.put("report_log_type","DT033");
                    saMap.put("dev_ip",host.elementText("DevIP"));
                // 若多操作识别和监控信息有上报数据，则默认op_type=8
                // 若网络接入信息中接入未授权设备告警信息有上报数据，则默认op_type=5
                if (dymon.elementText("MoniAction") != null) {
                    saMap.put("op_type",8);
                }
                saMap.put("event_time",DateUtil.timeStamp2Date(dymon.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                saMap.put("op_description","安装多操作系统");
                LogSendUtil.sendLogByUdp(JSON.toString(saMap),address);
                total++;
            }
        }
        if (CollectionUtils.isNotEmpty(netins)) {
            for (Element netin : netins) {
                Map<String,Object> saMap = new HashMap<>();
                saMap.put("report_log_type","DT033");
                saMap.put("dev_ip",host.elementText("DevIP"));
                if (netin.elementText("InUnauthDevAcAlert") != null) {
                    saMap.put("op_type",5);
                }
                saMap.put("event_time",DateUtil.timeStamp2Date(netin.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                saMap.put("op_description","未登记设备接入");
                LogSendUtil.sendLogByUdp(JSON.toString(saMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 违规介质操作日志
     * @param elements
     */
    public Integer sendSemLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 设备基本信息
        List<Element> devmgbs = this.getElementsByName(elements,"TERMINAL.STAINFO.DEVMGBSINFOList");
        // 网络基本信息
        List<Element> nwbs = this.getElementsByName(elements,"TERMINAL.STAINF.DEVHWBSINFO.NWBSINFOList");
        // 通用信息
        List<Element> periphgens = this.getElementsByName(elements,"DYNAINFO.DYPERIPH.PERIPHGENList");
        if (CollectionUtils.isNotEmpty(periphgens)) {
            for (Element periphgen : periphgens) {
                Map<String,Object> semLogMap = new HashMap<>();
                semLogMap.put("report_log_type","DT010");
                semLogMap.put("username",host.elementText("EmpName"));
                semLogMap.put("dev_id",host.elementText("HostCode"));
                semLogMap.put("dev_mac",host.elementText("MAC"));
                semLogMap.put("dev_ip",host.elementText("DevIP"));
                semLogMap.put("dev_class",periphgen.elementText("PeriphType"));
                semLogMap.put("event_time",DateUtil.timeStamp2Date(periphgen.elementText("PeriphAcTime"),DateUtil.DEFAULT_DATE_PATTERN));
                semLogMap.put("op_description",periphgen.elementText("UnauthDevAcAlert"));
                semLogMap.put("op_code","违规介质操作");
                LogSendUtil.sendLogByUdp(JSON.toString(semLogMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 打印刻录日志
     * @param elements
     */
    public Integer sendPrintAuditLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 打印行为信息
        List<Element> prints = this.getElementsByName(elements,"DYNAINFO.DYPERIPH.PRINTINFOList");
        // 光盘刻录信息
        List<Element> burns = this.getElementsByName(elements,"DYNAINFO.DYPERIPH.BURNINFOList");
        if (CollectionUtils.isNotEmpty(prints)) {
            for (Element print : prints) {
                Map<String,Object> printMap = new HashMap<>();
                printMap.put("report_log_type","DT005");
                printMap.put("username",host.elementText("EmpName"));
                printMap.put("dev_id",host.elementText("HostCode"));
                printMap.put("dev_ip",host.elementText("DevIP"));
                printMap.put("dev_name",print.elementText("PrinterName"));
                printMap.put("op_type","0");
                printMap.put("file_name",print.elementText("PrintFileName"));
                printMap.put("file_type",print.elementText("PrintFileType"));
                printMap.put("file_num",print.elementText("PrintCopy"));
                if (StringUtils.isNotEmpty(print.elementText("PrintResult")) && "1".equals(print.elementText("PrintResult"))) {
                    printMap.put("op_result",0);
                } else {
                    printMap.put("op_result",1);
                }
                printMap.put("event_time",print.elementText("PrintTime"));
                printMap.put("file_level",print.elementText("FileSecLev"));
                printMap.put("file_size",print.elementText("PrintFileSize"));
                printMap.put("business_list","");
                // 规则解析，根据event_time取小时
                printMap.put("op_hour","");
                // TODO 未对应字段填充
                printMap.put("data_source","");
                LogSendUtil.sendLogByUdp(JSON.toString(printMap),address);
                total++;
            }
        }
        if (CollectionUtils.isNotEmpty(burns)) {
            for (Element burn : burns) {
                Map<String,Object> printMap = new HashMap<>();
                printMap.put("report_log_type","DT005");
                printMap.put("username",host.elementText("EmpName"));
                printMap.put("dev_id",host.elementText("HostCode"));
                printMap.put("dev_ip",host.elementText("DevIP"));
                printMap.put("dev_name",burn.elementText("BurnFile"));
                printMap.put("op_type","1");
                printMap.put("file_name",burn.elementText("BurnFileName"));
                printMap.put("file_type",burn.elementText("BurnFileType"));
                if (StringUtils.isNotEmpty(burn.elementText("BurnResult")) && "1".equals(burn.elementText("BurnResult"))) {
                    printMap.put("op_result",0);
                } else {
                    printMap.put("op_result",1);
                }
                printMap.put("event_time",burn.elementText("BurnTime"));
                printMap.put("file_level",burn.elementText("BurnSecLev"));
                printMap.put("file_size",burn.elementText("BurnFileSize"));
                printMap.put("business_list","");
                // 规则解析，根据event_time取小时
                printMap.put("op_hour","");
                // TODO 未对应字段填充
                printMap.put("data_source","");
                LogSendUtil.sendLogByUdp(JSON.toString(printMap),address);
                total++;
            }
        }
        return total;

    }

    /**
     * 违规外联日志
     * @param elements
     */
    public Integer sendViolationLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 违规外联
        List<Element> viooutreachs = this.getElementsByName(elements,"DYNAINFO.DYSEC.VIOOUTREACHList");
        if (CollectionUtils.isNotEmpty(viooutreachs)) {
            for (Element viooutreach : viooutreachs) {
                Map<String,Object> violationMap = new HashMap<>();
                violationMap.put("report_log_type","DT032");
                violationMap.put("username",host.elementText("EmpName"));
                violationMap.put("dev_id",host.elementText("HostCode"));
                violationMap.put("dev_mac",host.elementText("MAC"));
                violationMap.put("dev_ip",host.elementText("DevIP"));
                violationMap.put("op_description",viooutreach.elementText("VioContent"));
                violationMap.put("event_time",DateUtil.timeStamp2Date(viooutreach.elementText("VioTime"),DateUtil.DEFAULT_DATE_PATTERN));
                // TODO 未对应字段填充
                violationMap.put("data_source","");
                LogSendUtil.sendLogByUdp(JSON.toString(violationMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 终端登录日志
     * @param elements
     */
    public Integer sendTerminalLoginLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 账号动态信息
        List<Element> acclogins = this.getElementsByName(elements,"DYNAINFO.DYACCINFO.ACCLOGININFOList");
        if (CollectionUtils.isNotEmpty(acclogins)) {
            for (Element acclogin : acclogins) {
                String loginAction = acclogin.elementText("LoginAction");
                if ("注销".equals(loginAction) || "登录".equals(loginAction)) {
                    Map<String,Object> terminalMap = new HashMap<>();
                    terminalMap.put("report_log_type","DT008");
                    terminalMap.put("username",host.elementText("EmpName"));
                    terminalMap.put("dev_id",host.elementText("HostCode"));
                    terminalMap.put("dev_ip",host.elementText("DevIP"));
                    if (StringUtils.isNotEmpty(acclogin.elementText("OSSucLoginTime")) && !"--".equals(acclogin.elementText("OSSucLoginTime"))) {
                        terminalMap.put("login_time",acclogin.elementText("OSSucLoginTime"));
                        terminalMap.put("op_type",1);
                    }
                    if (StringUtils.isNotEmpty(acclogin.elementText("LogoutTime")) && !"--".equals(acclogin.elementText("LogoutTime"))) {
                        terminalMap.put("login_time",acclogin.elementText("LogoutTime"));
                        terminalMap.put("op_type",2);
                    }
                    if (StringUtils.isNotEmpty(acclogin.elementText("Result")) && "成功".equals(acclogin.elementText("Result"))) {
                        terminalMap.put("op_result",0);
                    } else {
                        terminalMap.put("op_result",1);
                    }
                    terminalMap.put("event_time",terminalMap.get("login_time"));
                    terminalMap.put("op_hour","");
                    // TODO 未对应字段填充
                    terminalMap.put("data_source","");
                    LogSendUtil.sendLogByUdp(JSON.toString(terminalMap),address);
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * 开关机日志
     * @param elements
     * @param address
     * @return
     */
    public Integer sendStartOrShutLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 账号动态信息
        List<Element> acclogins = this.getElementsByName(elements,"DYNAINFO.DYACCINFO.ACCLOGININFOList");
        if (CollectionUtils.isNotEmpty(acclogins)) {
            for (Element acclogin : acclogins) {
                String loginAction = acclogin.elementText("LoginAction");
                if ("开机".equals(loginAction) || "关机".equals(loginAction)) {
                    Map<String,Object> terminalMap = new HashMap<>();
                    terminalMap.put("report_log_type","DT031");
                    terminalMap.put("username",host.elementText("EmpName"));
                    terminalMap.put("dev_id",host.elementText("HostCode"));
                    terminalMap.put("dev_ip",host.elementText("DevIP"));
                    String DevStartTime = acclogin.elementText("DevStartTime");
                    String DevShutTime = acclogin.elementText("DevShutTime");
                    if ((StringUtils.isNotEmpty(DevStartTime) && !"--".equals(DevStartTime)) && (StringUtils.isEmpty(DevShutTime) || "--".equals(DevShutTime))) {
                        terminalMap.put("op_type",0);
                        terminalMap.put("op_description","设备在" + TimeTools.utc2Local(DevStartTime) + "开机");
                        terminalMap.put("event_time",TimeTools.utc2Local(DevStartTime));
                    }
                    if ((StringUtils.isNotEmpty(DevShutTime) && !"--".equals(DevShutTime))) {
                        terminalMap.put("op_type",1);
                        terminalMap.put("op_description","设备在" + TimeTools.utc2Local(DevShutTime) + "关机");
                        terminalMap.put("event_time",TimeTools.utc2Local(DevShutTime));
                    }
                    LogSendUtil.sendLogByUdp(JSON.toString(terminalMap),address);
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * 文件监控日志
     * @param elements
     */
    public Integer sendFileAuditLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 设备基本信息
        List<Element> devmgbs = this.getElementsByName(elements,"TERMINAL.STAINFO.DEVMGBSINFOList");
        // 网络基本信息
        List<Element> nwbs = this.getElementsByName(elements,"TERMINAL.STAINF.DEVHWBSINFO.NWBSINFOList");
        // 介质及本地文件操作情况
        List<Element> medfileops = this.getElementsByName(elements,"DYNAINFO.DYFILEPROINFO.MEDFILEOPINFOList");
        if (CollectionUtils.isNotEmpty(medfileops)) {
            for (Element medfileop : medfileops) {
                Map<String,Object> fileAuditMap = new HashMap<>();
                fileAuditMap.put("report_log_type","DT002");
                fileAuditMap.put("username",host.elementText("EmpName"));
                fileAuditMap.put("dev_id",host.elementText("HostCode"));
                fileAuditMap.put("dev_ip",host.elementText("DevIP"));
                // 字典转换
                fileAuditMap.put("op_type",getFileAuditType(medfileop.elementText("FileAction")));
                fileAuditMap.put("business_list",medfileop.elementText("DistriScope"));
                fileAuditMap.put("dst_file_dir",medfileop.elementText("FilePath"));
                fileAuditMap.put("dst_file_name",getFileName(medfileop.elementText("FileName"),medfileop.elementText("FilePath")));
                fileAuditMap.put("file_type",getFileType(medfileop.elementText("FileSuffix"),medfileop.elementText("FilePath")));
                fileAuditMap.put("file_level",medfileop.elementText("FileSecurityLevel"));
                fileAuditMap.put("event_time",DateUtil.timeStamp2Date(medfileop.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                // TODO 未对应字段填充
                fileAuditMap.put("data_source","");
                fileAuditMap.put("src_file_dir","");
                fileAuditMap.put("src_file_name","");
                fileAuditMap.put("file_size","");
                fileAuditMap.put("md5","");
                LogSendUtil.sendLogByUdp(JSON.toString(fileAuditMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 文件操作类型字典转换
     * @param typeName
     * @return
     */
    private String getFileAuditType(String typeName) {
        String opType = "";
        switch (typeName) {
            case "新建":
                opType = "0";
                break;
            case "修改":
                opType = "1";
                break;
            case "删除":
                opType = "2";
                break;
            case "打开":
                opType = "3";
                break;
            case "重命名":
                opType = "4";
                break;
            case "复制":
                opType = "5";
                break;
            case "粘贴":
                opType = "5";
                break;
            case "移动":
                opType = "6";
                break;
            default:
                break;
        }
        return opType;
    }

    /**
     * 文件名称转换
     * @param fileName
     * @return
     */
    private static String getFileName(String fileName,String fileDir) {
        if (StringUtils.isEmpty(fileName) && StringUtils.isEmpty(fileDir)) {
            return "";
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = fileDir;
        }
        if (fileName.indexOf("/") >= 0) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        return fileName;
    }

    /**
     * 文件类型转换
     * @param fileSuffix
     * @param fileDir
     * @return
     */
    private static String getFileType(String fileSuffix,String fileDir) {
        if (StringUtils.isEmpty(fileSuffix) && StringUtils.isEmpty(fileDir)) {
            return "";
        }
        if (StringUtils.isNotEmpty(fileSuffix)) {
            return fileSuffix;
        }
        if (fileDir.indexOf(".") >= 0) {
            fileSuffix = fileDir.substring(fileDir.lastIndexOf(".") + 1);
        }
        return fileSuffix;
    }

    /**
     * 文件密级操作日志
     * @param elements
     */
    public Integer sendMbLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 介质及本地文件操作情况
        List<Element> medfileops = this.getElementsByName(elements,"DYNAINFO.DYFILEPROINFO.MEDFILEOPINFOList");
        if (CollectionUtils.isNotEmpty(medfileops)) {
            for (Element medfileop : medfileops) {
                if (StringUtils.isNotEmpty(medfileop.elementText("NolabInfo")) && !"--".equals(medfileop.elementText("NolabInfo"))) {
                    Map<String,Object> mbMap = new HashMap<>();
                    mbMap.put("report_log_type","DT007");
                    mbMap.put("username",host.elementText("EmpName"));
                    mbMap.put("dev_id",host.elementText("HostCode"));
                    mbMap.put("dev_ip",host.elementText("DevIP"));
                    mbMap.put("business_list",medfileop.elementText("DistriScope"));
                    mbMap.put("op_code",8);

                    mbMap.put("file_name",getFileName(medfileop.elementText("FileName"),medfileop.elementText("FilePath")));
                    mbMap.put("file_type",getFileType(medfileop.elementText("FileSuffix"),medfileop.elementText("FilePath")));
                    mbMap.put("file_level",medfileop.elementText("FileSecurityLevel"));
                    mbMap.put("event_time",DateUtil.timeStamp2Date(medfileop.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                    // TODO 未对应字段填充
                    mbMap.put("file_size","");
                    mbMap.put("md5","");
                    LogSendUtil.sendLogByUdp(JSON.toString(mbMap),address);
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * 共享审计
     * @param elements
     */
    public Integer sendShareLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 共享监控信息
        List<Element> shares = this.getElementsByName(elements,"DYNAINFO.DYFILEPROINFO.SHAREINFOList");
        if (CollectionUtils.isNotEmpty(shares)) {
            for (Element share : shares) {
                Map shareMap = new HashMap();
                shareMap.put("report_log_type","DT028");
                shareMap.put("dev_id",host.elementText("HostCode"));
                shareMap.put("dev_ip",host.elementText("DevIP"));
                shareMap.put("file_info",share.elementText("SharePath"));
                shareMap.put("event_time",DateUtil.timeStamp2Date(share.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                // TODO 未对应字段填充
                shareMap.put("dev_port","");
                LogSendUtil.sendLogByUdp(JSON.toString(shareMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 进程日志
     * @param elements
     */
    public Integer sendProcessLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 进程信息
        List<Element> dyprocs = this.getElementsByName(elements,"DYNAINFO.DYSYS.DYPROCINFOList");
        // 防护软件客户端进程杀死记录情况
//        List<Element> proswprokills = this.getElementsByName(elements,"HOSTSECPROTECT.PROTECTIONSWCLIENTINFO.PROSWPROKILLList");
        if (CollectionUtils.isNotEmpty(dyprocs)) {
            for (Element dyproc : dyprocs) {
                Map<String,Object> processMap = new HashMap<>();
                processMap.put("report_log_type","DT003");
                processMap.put("dev_id",host.elementText("HostCode"));
                processMap.put("dev_ip",host.elementText("DevIP"));
                processMap.put("process_name",dyproc.elementText("ProcessName"));
                // 字典转换
                processMap.put("op_type",getProcessopType(dyproc.elementText("ProcessAction")));
                processMap.put("event_time",DateUtil.timeStamp2Date(dyproc.elementText("logTime"),DateUtil.DEFAULT_DATE_PATTERN));
                LogSendUtil.sendLogByUdp(JSON.toString(processMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 获取进程操作类型
     * @param processAction
     * @return
     */
    private String getProcessopType(String processAction) {
        String opType = "";
        switch (processAction) {
            case "退出":
                opType = "1";
                break;
            case "创建":
                opType = "0";
                break;
        }
        return opType;
    }

    /**
     * 策略变更日志
     * @param elements
     */
    public Integer sendChangeStrategeLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // 审计策略
        List<Element> auditstrategys = this.getElementsByName(elements,"HOSTSECPROTECT.AUDITCONTRSTRA.AUDITSTRATEGYList");
        // 监控策略
        List<Element> controllstrategys = this.getElementsByName(elements,"HOSTSECPROTECT.AUDITCONTRSTRA.CONTROLSTRATEGYList");
        if (CollectionUtils.isNotEmpty(controllstrategys)) {
            for (Element controllstrategy : controllstrategys) {
                Map<String,Object> strategyMap = new HashMap<>();
                strategyMap.put("report_log_type","DT026");
                strategyMap.put("username",host.elementText("EmpName"));
                strategyMap.put("dev_ip",host.elementText("DevIP"));
                strategyMap.put("prev_status",controllstrategy.elementText("PrevStatus"));
                strategyMap.put("cur_status",controllstrategy.elementText("CurStatus"));
                strategyMap.put("op_description",controllstrategy.elementText("InOutOpenPoliList"));
                strategyMap.put("event_time",controllstrategy.elementText("DateTime"));
                String description = (String) strategyMap.get("op_description");
                if (StringUtils.isNotEmpty(description)) {
                    if (description.indexOf("禁用") >= 0) {
                        description = description.substring(description.indexOf("禁用"));
                    } else if (description.indexOf("启用") >= 0) {
                        description = description.substring(description.indexOf("启用"));
                    } else if (description.indexOf("启动") >= 0) {
                        description = description.substring(description.indexOf("启动"));
                    } else if (description.indexOf("由") >= 0) {
                        description = description.substring(0,description.indexOf("由"));
                    } else {
                        description = "";
                    }
                }
                strategyMap.put("op_code",description);
                // TODO 未对应字段
                strategyMap.put("data_source","");

                LogSendUtil.sendLogByUdp(JSON.toString(strategyMap),address);
                total++;
            }
        }
        if (CollectionUtils.isNotEmpty(auditstrategys)) {
            for (Element auditstrategy : auditstrategys) {
                Map<String,Object> strategyMap = new HashMap<>();
                strategyMap.put("report_log_type","DT026");
                strategyMap.put("username",host.elementText("EmpName"));
                strategyMap.put("dev_ip",host.elementText("DevIP"));
                // TODO 审计策略状态对应关系
                strategyMap.put("cur_status",auditstrategy.elementText("AuditPoliStatus"));
                if ("2".equals(auditstrategy.elementText("AuditPoliStatus"))) {
                    strategyMap.put("prev_status","1");
                } else if ("1".equals(auditstrategy.elementText("AuditPoliStatus"))) {
                    strategyMap.put("prev_status","2");
                } else {
                    strategyMap.put("prev_status","");
                }
                strategyMap.put("event_time",DateUtil.timeStamp2Date(auditstrategy.elementText("DateTime"),DateUtil.DEFAULT_DATE_PATTERN));
                strategyMap.put("op_description",auditstrategy.elementText("AuditPoliContent"));
                String description = (String) strategyMap.get("op_description");
                if (StringUtils.isNotEmpty(description)) {
                    if (description.indexOf("禁用") >= 0) {
                        description = description.substring(description.indexOf("禁用"));
                    } else if (description.indexOf("启用") >= 0) {
                        description = description.substring(description.indexOf("启用"));
                    } else if (description.indexOf("启动") >= 0) {
                        description = description.substring(description.indexOf("启动"));
                    } else if (description.indexOf("由") >= 0) {
                        description = description.substring(0,description.indexOf("由"));
                    } else {
                        description = "";
                    }
                }
                strategyMap.put("op_code",description);
                // TODO 未对应字段
                strategyMap.put("data_source","");
                LogSendUtil.sendLogByUdp(JSON.toString(strategyMap),address);
                total++;
            }
        }
        return total;
    }

    /**
     * 应用系统访问日志
     * @param elements
     */
    public Integer sendAppLog(List<Element> elements,Element host,String address) {
        Integer total = 0;
        // TODO 类型不存在
        // 前台访问信息
        List<Element> prontdesks = this.getElementsByName(elements,"APPLICATION.COMAPP.FRONTDESKINFOList");
        if (CollectionUtils.isNotEmpty(prontdesks)) {
            for (Element prontdesk : prontdesks) {
                Map<String,Object> appMap = new HashMap<>();
                appMap.put("report_log_type","DT036");
                appMap.put("dev_ip",prontdesk.elementText("FrontdeskIP"));
                appMap.put("app_au_info",prontdesk.elementText("AppAUInfo"));
                appMap.put("middle_au_info",prontdesk.elementText("MiddleAUInfo"));
                appMap.put("acclogmiddle_port",prontdesk.elementText("AccLogMiddlePort"));
                appMap.put("acclogmiddle_pro",prontdesk.elementText("AccLogMiddlePro"));
                // TODO acc_port、page_type、acc_pro关系对应

                // TODO 地址填充
                LogSendUtil.sendLogByUdp(JSON.toString(appMap),address);
                total++;
            }
        }
        return total;
    }

    public List<Element> getElementsByName(List<Element> elements,String name) {
        if (StringUtils.isEmpty(name) || CollectionUtils.isEmpty(elements)) {
            return null;
        }
        for (Element element : elements) {
            if (name.equals(element.getName())) {
                return element.elements();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Map<String,Object> strategyMap = new HashMap<>();
        strategyMap.put("op_description","注册客户端，默认禁用RAID控制器");
        String description = (String) strategyMap.get("op_description");
        if (StringUtils.isNotEmpty(description)) {
            if (description.indexOf("禁用") >= 0) {
                description = description.substring(description.indexOf("禁用"));
            } else if (description.indexOf("启用") >= 0) {
                description = description.substring(description.indexOf("启用"));
            } else if (description.indexOf("启动") >= 0) {
                description = description.substring(description.indexOf("启动"));
            } else {
                description = "";
            }
        }
        System.out.println(description);
    }
}

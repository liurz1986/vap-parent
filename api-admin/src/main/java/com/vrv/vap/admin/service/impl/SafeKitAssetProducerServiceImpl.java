package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.SafeKitAssetProducerService;
import com.vrv.vap.admin.vo.SyncAssetVO;
import com.vrv.vap.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lilang
 * @date 2023/2/27
 * @description 安全套件终端数据同步
 */
@Service
public class SafeKitAssetProducerServiceImpl extends BaseDataProducerServiceImpl implements SafeKitAssetProducerService {

    private static final Logger log = LoggerFactory.getLogger(SafeKitAssetProducerServiceImpl.class);

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_TJ.equals(source)) {
            this.produceBxy(syncBaseData);
        }
    }

    public void produceBxy(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-安全套件终端信息同步");
        String key = this.getKitKey();
        if (org.apache.commons.lang.StringUtils.isEmpty(key)) {
            this.saveLog(syncBaseData,0,1,"套件秘钥未配置");
            return;
        }
        String prefix = this.getUrlPrefix(syncBaseData);
        String url = prefix + "/api/getorglist?type=80" + getKitUrlSuffix(syncBaseData);
        try {
            log.info("请求地址：" + url);
            String result = HTTPUtil.GET(url,null);
            log.info("套件返回组织机构信息：" + result);
            if (StringUtils.isEmpty(result)) {
                description = "获取安全套件组织机构数据失败";
                log.info(description);
                status = 1;
                this.saveLog(syncBaseData,total,status,description);
                return;
            }
            Document document = DocumentHelper.parseText(result);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element depList = root.element("DeptList");
            List<String> deptIds = new ArrayList<>();
            if (depList != null) {
                List<Element> elements = depList.elements();
                for (Element item : elements) {
                    // 根节点能查到所有资产
                    if ("0".equals(item.elementText("ParentID"))) {
                        deptIds.add(item.elementText("DeptID"));
                    }
                }
            }
            Map map = this.syncKitTerminal(syncBaseData,deptIds,total,description);
            total += (Integer) map.get("total");
            description = (String) map.get("description");
        } catch (Exception e) {
            description = "套件终端数据同步异常";
            log.error("",e);
            status = 1;
        }
        this.saveLog(syncBaseData,total,status,description);
    }

    public Map syncKitTerminal(SyncBaseData syncBaseData,List<String> deptIds,Integer total,String description) {
        Map result = new HashMap();
        if (CollectionUtils.isNotEmpty(deptIds)) {
            for (String deptId : deptIds) {
                String prefix = this.getUrlPrefix(syncBaseData);
                String terminalUrl = prefix + "/api/gethostlist?type=81&DeptID=" + deptId + getKitUrlSuffix(syncBaseData);
                try {
                    log.info("请求地址：" + terminalUrl);
                    String terminalResult = HTTPUtil.GET(terminalUrl,null);
                    log.info("套件返回资产信息：" + terminalResult);
                    if (StringUtils.isEmpty(terminalResult)) {
                        description = "获取安全套件终端数据失败";
                        log.info(description);
                        result.put("total",total);
                        result.put("description",description);
                        return result;
                    }
                    Document document = DocumentHelper.parseText(terminalResult);
                    document.setXMLEncoding("utf-8");
                    Element root = document.getRootElement();
                    Element hostLists = root.element("HostList");
                    if (hostLists != null) {
                        List<Element> elements = hostLists.elements();
                        total += elements.size();
                        if (CollectionUtils.isNotEmpty(elements)) {
                            String batchNo = System.currentTimeMillis() + "";
                            for (Element host : elements) {
                                SyncAssetVO syncAssetVO = new SyncAssetVO();
                                // 主机唯一标识
                                syncAssetVO.setGuid(CommonTools.generateId());
                                // TODO 需要根据类型字典调整
                                syncAssetVO.setTypeGuid(this.getTypeGuid(host.elementText("Machine")));
                                syncAssetVO.setTypeUnicode(this.getTypeUnicode(host.elementText("Machine")));
                                syncAssetVO.setBatchNo(batchNo);
                                syncAssetVO.setSerialNumber(host.elementText("HostCode"));
                                syncAssetVO.setName(host.elementText("ComputerName"));
                                syncAssetVO.setIp(host.elementText("DevIP"));
                                this.completeDomain(syncAssetVO);
                                syncAssetVO.setMac(host.elementText("MAC"));
                                syncAssetVO.setResponsibleCode(host.elementText("EmpID"));
                                syncAssetVO.setResponsibleName(host.elementText("EmpName"));
                                syncAssetVO.setIsAdmin(host.elementText("IsAdmin"));
                                syncAssetVO.setVendor(host.elementText("Vendor"));
                                // 设备类型
                                String isAdmin = host.elementText("IsAdmin");
                                if ("0".equals(isAdmin)) {
                                    syncAssetVO.setTerminalType("1");
                                } else {
                                    syncAssetVO.setTerminalType("2");
                                }
                                syncAssetVO.setGateway(host.elementText("Gateway"));
                                // 部门编号
                                String orgCode = host.elementText("DeptID");
                                syncAssetVO.setOrgCode(orgCode);
                                this.completeOrgInfo(syncAssetVO);
                                syncAssetVO.setOsList(host.elementText("SysVer"));
                                // 拓展字段
                                Map<String,Object> extendsInfo = new HashMap<>();
                                // 硬盘序列号
                                extendsInfo.put("extendDiskNumber",host.elementText("HDCode"));
                                // 操作系统版本
                                extendsInfo.put("sysSno",host.elementText("SysVer"));
                                syncAssetVO.setExtendInfos(extendsInfo);
                                syncAssetVO.setSyncUid(syncAssetVO.getIp() + "-" + syncAssetVO.getMac());
                                syncAssetVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_TJ);
                                syncAssetVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                                // 套件非运维信息数据
                                this.syncTerminalNotOperation(syncBaseData,syncAssetVO);
                                // 套件运维信息数据
                                this.syncTerminalOperationDetail(syncBaseData,syncAssetVO);
                                // 入kafka
                                this.sendData(syncAssetVO, "asset", SyncBaseDataConstants.TOPIC_NAME_ASSET);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("",e);
                }
            }
        }
        result.put("total",total);
        result.put("description",description);
        return result;
    }

    /**
     * 终端运维数据
     * @param syncBaseData
     * @param syncAssetVO
     * @return
     */
    public SyncAssetVO syncTerminalOperationDetail(SyncBaseData syncBaseData,SyncAssetVO syncAssetVO) {
        String deptId = syncAssetVO.getOrgCode();
        String hostCode = syncAssetVO.getSerialNumber();
        String startTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.getBeforeMouthByNum(1),DateUtil.DEFAULT_DATE_PATTERN));
        String endTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN),DateUtil.DEFAULT_DATE_PATTERN));
        startTime = startTime.substring(0,startTime.length() - 3);
        endTime = endTime.substring(0,endTime.length() - 3);
        // 数据类型：套件运维数据格式二级字段名
        String dataName = "STAINFO";
        String prefix = this.getUrlPrefix(syncBaseData);
        String detailUrl = prefix + "/api/gethostselectedinfo?type=82&SubType=2&DeptID=" + deptId +
                "&Hostcode=" + hostCode + "&Starttime=" + Long.valueOf(startTime) + "&Endtime=" + Long.valueOf(endTime) + "&DataName=" + dataName + getKitUrlSuffix(syncBaseData);
        try {
            log.info("请求地址：" + detailUrl);
            String operationResult = HTTPUtil.GET(detailUrl,null);
            if (StringUtils.isEmpty(operationResult)) {
                log.info("获取套件终端运维信息失败");
                return syncAssetVO;
            }
            Document document = DocumentHelper.parseText(operationResult);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element selectInfo = root.element("HostSelectedinfo");
            if (selectInfo != null) {
                List<Element> elements = selectInfo.elements();
                if (CollectionUtils.isNotEmpty(elements)) {
                    for (Element detail : elements) {
                        // 拓展字段
                        Map<String,Object> extendsInfo = syncAssetVO.getExtendInfos();
                        extendsInfo.put("extendVersionInfo",detail.elementText("DevModel"));
                        extendsInfo.put("extendSystem",detail.elementText("SysName"));
                        syncAssetVO.setExtendInfos(extendsInfo);

                        syncAssetVO.setCoreVer(detail.elementText("CoreVer"));
                        syncAssetVO.setBiosVer(detail.elementText("BIOSVer"));
                    }
                }
            }
        } catch (Exception e) {
            log.info("获取套件终端运维信息异常");
            log.error("",e);
        }
        return syncAssetVO;
    }

    /**
     * 终端非运维数据
     * @param syncBaseData
     * @param syncAssetVO
     * @return
     */
    public SyncAssetVO syncTerminalNotOperation(SyncBaseData syncBaseData,SyncAssetVO syncAssetVO) {
        String deptId = syncAssetVO.getOrgCode();
        String hostCode = syncAssetVO.getSerialNumber();
        String startTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.getBeforeMouthByNum(1),DateUtil.DEFAULT_DATE_PATTERN));
        String endTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN),DateUtil.DEFAULT_DATE_PATTERN));
        startTime = startTime.substring(0,startTime.length() - 3);
        endTime = endTime.substring(0,endTime.length() - 3);
        // TODO 数据类型
        String dataName = "TERMINAL.STAINFO.DEVMGBSINFO,TERMINAL.STAINFO.DEVHWBSINFO.NWBSINFO,TERMINAL.STAINFO.DEVHWBSINFO.HDINFO," +
                "TERMINAL.STAINFO.DEVHWBSINFO.MAININFO,TERMINAL.STAINFO.DEVHWBSINFO.HWVERINFO,TERMINAL.STAINFO.DEVSWBSINFO.OSBSINFO," +
                "TERMINAL.STAINFO.DEVSWBSINFO.SWVERINFO,TERMINAL.STAINFO.DEVSWBSINFO,NETSECEQUIP.OPERATDATA.NETSTAINFO";
        String prefix = this.getUrlPrefix(syncBaseData);
        String detailUrl = prefix + "/api/gethostselectedinfo?type=82&SubType=1&DeptID=" + deptId +
                "&Hostcode=" + hostCode + "&Starttime=" + Long.valueOf(startTime) + "&Endtime=" + Long.valueOf(endTime) + "&DataName=" + dataName + getKitUrlSuffix(syncBaseData);
        try {
            log.info("请求地址：" + detailUrl);
            String operationResult = HTTPUtil.GET(detailUrl,null);
            if (StringUtils.isEmpty(operationResult)) {
                log.info("获取套件终端非运维信息失败");
                return syncAssetVO;
            }
            Document document = DocumentHelper.parseText(operationResult);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element selectInfo = root.element("HostSelectedinfo");
            if (selectInfo != null) {
                List<Element> elements = selectInfo.elements();
                if (CollectionUtils.isNotEmpty(elements)) {
                    for (Element detail : elements) {
                        if ("TERMINAL.STAINFO.DEVMGBSINFOList".equals(detail.getName())) {
                            // 单位名称
                            syncAssetVO.setUnitName(detail.elementText("UnitName"));
                            // 单位编码
                            syncAssetVO.setUnitId(detail.elementText("UnitID"));
                            // 机构代码
//                            syncAssetVO.setOrgCode(detail.elementText("OrgID"));
                            // 省份
                            syncAssetVO.setProvince(detail.elementText("Province"));
                            // 安全域
                            syncAssetVO.setDomainName(detail.elementText("SecDomain"));
                            // 城市
                            syncAssetVO.setCity(detail.elementText("City"));
                            // 部门名称
                            syncAssetVO.setDeptName(detail.elementText("DeptName"));
                            // 密级
                            syncAssetVO.setSecretLevel(detail.elementText("SecLvl"));
                        }
                        if ("TERMINAL.STAINFO.DEVHWBSINFO.NWBSINFOList".equals(detail.getName())) {
                            // 网卡编号
                            syncAssetVO.setNetcardModel(detail.elementText("NetcardModel"));
                        }
                        if ("TERMINAL.STAINFO.DEVHWBSINFO.HDINFOList".equals(detail.getName())) {
                            // 硬盘序列号变更时间
                            syncAssetVO.setHdiChgTime(detail.elementText("HdIDChgTime"));
                        }
                        if ("TERMINAL.STAINFO.DEVHWBSINFO.MAININFOList".equals(detail.getName())) {
                            // 主板序列号
                            syncAssetVO.setBoardSerialNum(detail.elementText("SerialNum"));
                        }
                        if ("TERMINAL.STAINFO.DEVHWBSINFO.HWVERINFOList".equals(detail.getName())) {
                            // 安全卡编号
                            syncAssetVO.setSecCardId(detail.elementText("SecCardID"));
                        }
                        if ("TERMINAL.STAINFO.DEVSWBSINFO.OSBSINFOList".equals(detail.getName())) {
                            // 软件安装列表
                            syncAssetVO.setSoftInsList(detail.elementText("SoftInsList"));
                        }
                        if ("NETSECEQUIP.OPERATDATA.NETSTAINFOList".equals(detail.getName())) {
                            // 网络设备-名称
                            if (StringUtils.isNotEmpty(detail.elementText("NetDevName"))) {
                                syncAssetVO.setName(detail.elementText("NetDevName"));
                            }
                            if (StringUtils.isNotEmpty(detail.elementText("NetModel"))) {
                                // 拓展字段
                                Map<String,Object> extendsInfo = syncAssetVO.getExtendInfos();
                                // 型号
                                extendsInfo.put("extendVersionInfo",detail.elementText("NetModel"));
                                syncAssetVO.setExtendInfos(extendsInfo);
                            }
                            // 制造商
                            if (StringUtils.isNotEmpty(detail.elementText("NetManufacture"))) {
                                syncAssetVO.setVendor(detail.elementText("NetManufacture"));
                            }
                            if (StringUtils.isNotEmpty(detail.elementText("NetDevType"))) {
                                // 设备类型
                                syncAssetVO.setTerminalType(detail.elementText("NetDevType"));
                            }
                            // 责任人
                            if (StringUtils.isNotEmpty(detail.elementText("EMP"))) {
                                syncAssetVO.setResponsibleName(detail.elementText("EMP"));
                            }
                            // 安装位置
                            if (StringUtils.isNotEmpty(detail.elementText("InstSize"))) {
                                syncAssetVO.setInstSize(detail.elementText("InstSize"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("获取套件终端非运维信息异常");
            log.error("",e);
        }
        return syncAssetVO;
    }

    private String getTypeGuid(String machine) {
        switch (machine) {
            // 其它终端
            case "05":
                return "2e064ec4885f464f8043d37ac7b489d9";
            // 其它服务器
            case "06":
                return "67dce0923b3d455ab6a33dbabaed6556";
            case "10":
                return "67dce0923b3d455ab6a33dbabaed6556";
            case "11":
                return "2e064ec4885f464f8043d37ac7b489d9";
            case "03":
                return "67dce0923b3d455ab6a33dbabaed6556";
            default:
                return "";
        }
    }

    private String getTypeUnicode(String machine) {
        switch (machine) {
            case "05":
                return "其它终端";
            // 其它服务器
            case "06":
                return "其它服务器";
            case "10":
                return "其它服务器";
            case "11":
                return "其它终端";
            case "03":
                return "其它服务器";
            default:
                return "";
        }
    }
}

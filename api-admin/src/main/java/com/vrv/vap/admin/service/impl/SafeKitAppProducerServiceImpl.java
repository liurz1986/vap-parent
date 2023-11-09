package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.SafeKitAppProducerService;
import com.vrv.vap.admin.vo.SyncAppVO;
import com.vrv.vap.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author lilang
 * @date 2023/3/8
 * @description 安全套件应用数据同步实现类
 */
@Service
public class SafeKitAppProducerServiceImpl extends BaseDataProducerServiceImpl implements SafeKitAppProducerService {

    private static final Logger log = LoggerFactory.getLogger(SafeKitAppProducerServiceImpl.class);

    @Resource
    BaseKoalOrgService baseKoalOrgService;

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
        String url = prefix + "/api/getorglist?type=80" + this.getKitUrlSuffix(syncBaseData);
        try {
            String result = HTTPUtil.GET(url,null);
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
                total += elements.size();
                for (Element item : elements) {
                    deptIds.add(item.elementText("DeptID"));
                }
            }
            Map map = this.syncKitApp(syncBaseData,deptIds,total,description);
            total = (Integer) map.get("total");
            description = (String) map.get("description");
        } catch (Exception e) {
            description = "套件应用数据同步异常";
            log.error("",e);
            status = 1;
        }
        this.saveLog(syncBaseData,total,status,description);
    }

    public Map syncKitApp(SyncBaseData syncBaseData,List<String> deptIds,Integer total,String description) {
        Map result = new HashMap();
        if (CollectionUtils.isNotEmpty(deptIds)) {
            for (String deptId : deptIds) {
                String prefix = this.getUrlPrefix(syncBaseData);
                String terminalUrl = prefix + "/api/gethostlist?type=81&DeptID=" + deptId + this.getKitUrlSuffix(syncBaseData);
                try {
                    String terminalResult = HTTPUtil.GET(terminalUrl,null);
                    if (StringUtils.isEmpty(terminalResult)) {
                        description = "获取安全套件应用数据失败";
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
                            for (Element host : elements) {
                                SyncAppVO syncAppVO = new SyncAppVO();
                                // 主机唯一标识
                                String hostCode = host.elementText("HostCode");
                                // 部门编号
                                String orgCode = host.elementText("DeptID");
                                syncAppVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_TJ);
                                syncAppVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                                // 套件非运维信息数据
                                this.syncAppNotOperation(syncBaseData,syncAppVO,hostCode,orgCode);
                                syncAppVO.setSyncUid(syncAppVO.getServerIp() + "-" + syncAppVO.getAppName());
                                syncAppVO.setDepartmentGuid(deptId);
                                BaseKoalOrg baseKoalOrg = baseKoalOrgService.findByCode(deptId);
                                if (baseKoalOrg != null) {
                                    syncAppVO.setDepartmentName(baseKoalOrg.getName());
                                }
                                // 入kafka
                                this.sendData(syncAppVO, "app", SyncBaseDataConstants.TOPIC_NAME_APP);
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

    public SyncAppVO syncAppNotOperation(SyncBaseData syncBaseData,SyncAppVO syncAppVO,String hostCode,String orgCode) {
        String startTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.getBeforeMouthByNum(1),DateUtil.DEFAULT_DATE_PATTERN));
        String endTime = String.valueOf(DateUtil.getTimeTostamp(DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN),DateUtil.DEFAULT_DATE_PATTERN));
        startTime = startTime.substring(0,startTime.length() - 3);
        endTime = endTime.substring(0,endTime.length() - 3);
        // TODO 数据类型
        String dataName = "APPLICATION.COMAPP.APPGENINFO,APPLICATION.COMAPP.APPACCINFO";
        String prefix = this.getUrlPrefix(syncBaseData);
        String detailUrl = prefix + "/api/gethostselectedinfo?type=82&SubType=1&DeptID=" + orgCode +
                "&Hostcode=" + hostCode + "&Starttime=" + startTime + "&Endtime=" + endTime + "&DataName=" + dataName + this.getKitUrlSuffix(syncBaseData);
        try {
            String operationResult = HTTPUtil.GET(detailUrl,null);
            if (StringUtils.isEmpty(operationResult)) {
                log.info("获取套件应用非运维信息失败");
                return syncAppVO;
            }
            Document document = DocumentHelper.parseText(operationResult);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element selectInfo = root.element("HostSelectedinfo");
            if (selectInfo != null) {
                List<Element> elements = selectInfo.elements();
                if (CollectionUtils.isNotEmpty(elements)) {
                    for (Element detail : elements) {
                        if ("APPLICATION.COMAPP.APPGENINFO".equals(detail.getName())) {
                            syncAppVO.setAppName(detail.elementText("AppName"));
                            syncAppVO.setSecretLevel(detail.elementText("AppSecLev"));
                            syncAppVO.setMiddlewareName(detail.elementText("MiddlewareName"));
                            syncAppVO.setMiddlewareVer(detail.elementText("MiddlewareVe"));
                            syncAppVO.setServerIp(detail.elementText("ServerIP"));
                            syncAppVO.setDatabaseIp(detail.elementText("DatabaseIP"));
                            syncAppVO.setDatabaseType(detail.elementText("DatabaseType"));
                            syncAppVO.setBusiPageAddrList(detail.elementText("BusiPageAddrList"));
                            syncAppVO.setMgtPageAddrList(detail.elementText("MgtPageAddrList"));
                            syncAppVO.setDirectoryAcAuth(detail.elementText("DirectoryAcAuth"));
                        }
                        if ("APPLICATION.COMAPP.APPACCINFO".equals(detail.getName())) {
                            syncAppVO.setAdminAcList(detail.elementText("AdminAcList"));
                            syncAppVO.setSysAdminAcList(detail.elementText("SysAdminAcList"));
                            syncAppVO.setScAdminAcList(detail.elementText("SCAdminAcList"));
                            syncAppVO.setSaAdminAcList(detail.elementText("SAAdminAcList"));
                            syncAppVO.setUserList(detail.elementText("UserList"));
                            syncAppVO.setUserAcList(detail.elementText("UserAcList"));
                            syncAppVO.setRoleName(detail.elementText("RoleName"));
                            syncAppVO.setRoleCreateTime(detail.elementText("RoleCreateTime"));
                            syncAppVO.setRoleLogoutTime(detail.elementText("RoleLogoutTime"));
                            syncAppVO.setRoleAccessList(detail.elementText("RoleAccessList"));
                            syncAppVO.setAccList(detail.elementText("AccList"));
                            syncAppVO.setAccSecLev(detail.elementText("AccSecLev"));
                            syncAppVO.setAccRoleDes(detail.elementText("AccRoleDes"));
                            syncAppVO.setAccUnit(detail.elementText("AccUnit"));
                            syncAppVO.setAccCreateTime(detail.elementText("AccCreateTime"));
                            syncAppVO.setAccLogoutTime(detail.elementText("AccLogoutTime"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("获取套件应用非运维信息异常");
            log.error("",e);
        }
        return syncAppVO;
    }

}

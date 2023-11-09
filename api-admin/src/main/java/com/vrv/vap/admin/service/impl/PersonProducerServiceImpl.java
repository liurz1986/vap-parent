package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.util.Base64Util;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.PersonProducerService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.vo.SyncOrgVO;
import com.vrv.vap.admin.vo.SyncPersonVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/5/16
 * @description
 */
@Service
public class PersonProducerServiceImpl extends BaseDataProducerServiceImpl implements PersonProducerService {

    private static final Logger log = LoggerFactory.getLogger(PersonProducerServiceImpl.class);

    @Autowired
    private KafkaSenderService kafkaSenderService;

    private final String[] resourcesName = {"departments","employees"};

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_GMP.equals(source)) {
            this.produceBxyGMP(syncBaseData);
        }
        if (SyncSourceConstants.SOURCE_BXY_TJ.equals(source)) {
            this.produceBxyKit(syncBaseData);
        }
    }

    public void produceBxyGMP(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-基础表信息同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        String version = this.getLatestVersion(syncBaseData);
        if (StringUtils.isEmpty(version)) {
            log.info("获取最新版本信息失败！");
            return;
        }
        for(String resourceName : resourcesName){
            Map<String,String> param = new HashMap<>();
            int page = 0;
            param.put("$size","20");
            List<Map> dataList = new ArrayList<>();
            while (true){
                page = page++;
                param.put("$page",(page++)+"");
                String url = protocolType + "://"  + ip + ":" + port + "/snapshots/" + version + "/" + resourceName + "?$page=" + page + "&$size=20";
                String clientId = syncBaseData.getAccount();
                String clientSecret = syncBaseData.getPassword();
                String token = this.generateToken(clientId,clientSecret);
                Map headers = this.generateHeaders(token);
                try {
                    String response = HTTPUtil.GET(url,headers);
                    if (StringUtils.isNotEmpty(response)) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map result = objectMapper.readValue(response,Map.class);
                        if (result.containsKey("data")) {
                            List<Map> datas = (List<Map>) result.get("data");
                            total += datas.size();
                            dataList.addAll(datas);
                            if(datas == null || datas.size() < 20){
                                break;
                            }
                        }
                    } else {
                        log.info("获取最新版本分页数据返回结果为空！");
                        description = "获取最新最新版本分页数据请求失败！";
                        status = 1;
                        break;
                    }
                } catch (Exception e) {
                    description = "人员数据同步异常！";
                    status = 1;
                    log.error("",e);
                    break;
                }
            }
            syncGMPData(resourceName,dataList,syncBaseData.getSource());
        }
        this.saveLog(syncBaseData,total,status,description);
    }

    public void produceBxyKit(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-安全套件信息同步");
        String key = this.getKitKey();
        if (StringUtils.isEmpty(key)) {
            this.saveLog(syncBaseData,0,1,"套件秘钥未配置");
            return;
        }
        String prefix = this.getUrlPrefix(syncBaseData);
        String url = prefix + "/api/getorglist?type=80" + this.getKitUrlSuffix(syncBaseData);
        try {
            log.info("请求地址：" + url);
            String result = HTTPUtil.GET(url,null);
            if (com.vrv.vap.common.utils.StringUtils.isEmpty(result)) {
                description = "套件人员组织机构获取失败";
                status = 1;
                log.info(description);
                this.saveLog(syncBaseData,total,status,description);
                return;
            }
            Document document = DocumentHelper.parseText(result);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
//            Element unitInfo = root.element("UnitInfo");
            Element depList = root.element("DeptList");
            Element EmpList = root.element("EmpList");
//            if (unitInfo != null) {
//                 String unitId = unitInfo.elementText("UnitID");
//                 String unitName = unitInfo.elementText("UnitName");
//            }
            List<Map> orgList = new ArrayList<>();
            if (depList != null) {
                List<Element> elements = depList.elements();
                if (CollectionUtils.isNotEmpty(elements)) {
                    total += elements.size();
                    for (Element item : elements) {
                        Map data = new HashMap();
                        data.put("id",item.elementText("DeptID"));
                        data.put("name",item.elementText("DeptName"));
                        data.put("pid",item.elementText("ParentID"));
                        data.put("secretLevel",item.elementText("SecretLevel"));
                        data.put("secretQualifications",item.elementText("SecretQualifications"));
                        data.put("protectionLevel",item.elementText("ProtectionLevel"));
                        orgList.add(data);
                    }
                    this.syncOrgan(orgList,syncBaseData.getSource());
                }
            }
            List<Map> personList = new ArrayList<>();
            if (EmpList != null) {
                List<Element> elements = EmpList.elements();
                if (CollectionUtils.isNotEmpty(elements)) {
                    total += elements.size();
                    for (Element item : elements) {
                        Map data = new HashMap();
                        data.put("id",item.elementText("EmpID"));
                        data.put("name",item.elementText("EmpName"));
                        data.put("departmentId",item.elementText("DeptID"));
                        data.put("personRank",item.elementText("RMSPostNo"));
                        data.put("secretLevel",item.elementText("RMSSecurityLevel"));
                        data.put("originAccount",item.elementText("RMSAccount"));
                        data.put("userNo",item.elementText("EmpID"));
                        data.put("userIdnEx",item.elementText("UserIdnEx"));
                        data.put("personType",item.elementText("PersonType"));
                        personList.add(data);
                    }
                    this.syncPerson(personList,syncBaseData.getSource(),orgList);
                }
            }
        } catch (Exception e) {
            description = "套件人员组织机构同步异常";
            log.error("",e);
            status = 1;
        }
        this.saveLog(syncBaseData,total,status,description);
    }

    private void syncGMPData(String resourceName,List<Map> dataList,String source){
        switch (resourceName) {
            case "departments":
                syncOrgan(dataList,source);
                break;
            case "employees":
                syncPerson(dataList,source,null);
                break;
        }
    }

    private void syncOrgan(List<Map> dataList,String source) {
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Map map : dataList) {
                SyncOrgVO syncOrgVO = new SyncOrgVO();
                syncOrgVO.setSyncUid(String.valueOf(map.get("id")));
                syncOrgVO.setSyncSource(source);
                syncOrgVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                syncOrgVO.setCode(String.valueOf(map.get("id")));
                syncOrgVO.setParentCode(String.valueOf(map.get("pid")));
                syncOrgVO.setName((String) map.get("name"));
                if (SyncSourceConstants.SOURCE_BXY_GMP.equals(source)) {
                    syncOrgVO.setSort((Integer) map.get("order"));
                }
                if (SyncSourceConstants.SOURCE_BXY_TJ.equals(source)) {
                    syncOrgVO.setSecretLevel((String) map.get("secretLevel"));
                    syncOrgVO.setSecretQualifications((String) map.get("secretQualifications"));
                    syncOrgVO.setProtectionLevel((String) map.get("protectionLevel"));
                }
                // 入kafka
                this.sendData(syncOrgVO,"org",SyncBaseDataConstants.TOPIC_NAME_ORG);
            }
            this.sendCompleteMessage("org");
        }
    }

    private void syncPerson(List<Map> dataList,String source,List<Map> orgList) {
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Map map : dataList) {
                SyncPersonVO syncPersonVO = new SyncPersonVO();
                syncPersonVO.setSyncUid(String.valueOf(map.get("id")));
                syncPersonVO.setSyncSource(source);
                syncPersonVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                syncPersonVO.setUserName((String) map.get("name"));
                String departmentId = String.valueOf(map.get("departmentId"));
                syncPersonVO.setOrgCode(departmentId);
                if (SyncSourceConstants.SOURCE_BXY_GMP.equals(source)) {
                    syncPersonVO.setUserNo(String.valueOf(map.get("id")));
                    syncPersonVO.setSecretLevel((String) map.get("securityLevelId"));
                    syncPersonVO.setOriginAccount((String) map.get("account"));
                }
                if (SyncSourceConstants.SOURCE_BXY_TJ.equals(source)) {
                    syncPersonVO.setUserNo((String) map.get("userNo"));
                    syncPersonVO.setUserIdnEx((String) map.get("userIdnEx"));
                    syncPersonVO.setPersonType((String) map.get("personType"));
                    syncPersonVO.setPersonRank((String) map.get("personRank"));
                    syncPersonVO.setSecretLevel((String) map.get("secretLevel"));
                    if (CollectionUtils.isNotEmpty(orgList)) {
                        for (Map org : orgList) {
                            if (departmentId.equals(org.get("id").toString())) {
                                syncPersonVO.setOrgName((String) org.get("name"));
                                break;
                            }
                        }
                    }
                    syncPersonVO.setOriginAccount((String) map.get("originAccount"));
                }
                // 入kafka
                this.sendData(syncPersonVO,"person",SyncBaseDataConstants.TOPIC_NAME_PERSON);
            }
            this.sendCompleteMessage("person");
        }
    }

    private String getLatestVersion(SyncBaseData syncBaseData) {
        String version = "";
        String description = "";
        if (StringUtils.isEmpty(version)) {
            String ip = syncBaseData.getIp();
            String port = syncBaseData.getPort();
            String protocolType = syncBaseData.getProtocolType();
            String address = protocolType + "://"  + ip + ":" + port + "/snapshot-versions?$latest-version";
            log.info("基础表信息同步获取最新版本号请求地址：" + address);
            String clientId = syncBaseData.getAccount();
            String clientSecret = syncBaseData.getPassword();
            String token = this.generateToken(clientId,clientSecret);
            Map headers = this.generateHeaders(token);
            try {
                String result = HTTPUtil.GET(address,headers);
                if (StringUtils.isEmpty(result)) {
                    log.info("基础表信息同步获取最新版本号失败！");
                    description = "基础表信息最新版本地址请求失败";
                    this.saveLog(syncBaseData,0,1,description);
                    return version;
                }
                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.readValue(result,Map.class);
                if (map.containsKey("data")) {
                    Map data = (Map) map.get("data");
                    version = (String) data.get("id");
                    log.info("注册成功，最新版本号为：" + version);
                } else {
                    log.info("基础表信息同步获取最新版本号返回结果中没有[data]！");
                    description = "基础表信息同步获取最新版本号返回结果中没有[data]";
                    this.saveLog(syncBaseData,0,1,description);
                    return version;
                }
            } catch (Exception e) {
                log.error("",e);
            }
        }
        return version;
    }

    private String generateToken(String clientId,String clientSecret) {
        String decriptToken = clientId + ":" + clientSecret;
        String token = Base64Util.encodeBase64(decriptToken);
        return token;
    }

    /**
     * 构造请求头
     * @param token
     * @return
     */
    private Map<String,String> generateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization","Basic " + token);
        headers.put("Accept", "application/json;charset=UTF-8");
        return headers;
    }

    public void sendCompleteMessage(String type) {
        Map<String,Object> result = new HashMap<>();
        result.put("item",type);
        result.put("time", System.currentTimeMillis());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(result);
            kafkaSenderService.send("vap_base_data_complete_message",null,content);
        } catch (Exception e) {
            log.error("",e);
        }
    }
}

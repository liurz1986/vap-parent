package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.AssetTypeRel;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.AssetTypeRelService;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.service.MachineAuditAssetProducerService;
import com.vrv.vap.admin.vo.SyncAssetVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/4/26
 * @description
 */
@Service
public class MachineAuditAssetProducerServiceImpl extends BaseDataProducerServiceImpl implements MachineAuditAssetProducerService {

    private static final Logger log = LoggerFactory.getLogger(MachineAuditAssetProducerServiceImpl.class);

    private static final Integer DATA_SIZE = 20;

    @Resource
    AssetTypeRelService assetTypeRelService;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_ZS.equals(source)) {
            this.produceBxy(syncBaseData);
        }
    }

    public void produceBxy(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-主审资产同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        String address = protocolType + "://" + ip + ":" + port + "/NTDS/WebServer/AssetManagement.asmx/GetDeviceAll";
        log.info("主审资产同步地址：" + address);
        Map<String, Object> param = new HashMap<>();
        param.put("SearchCondition", new HashMap<>());
        param.put("PageIndex", "1");
        param.put("PageSize", "1");
        try {
            String totalResponse = sendPostRequest(address, "where=" + JSON.toJSONString(param));
            log.info("主审返回结果：" + totalResponse);
            if (StringUtils.isEmpty(totalResponse)) {
                log.info("获取主审资产数据失败！");
                description = "主审资产同步地址请求失败！";
                this.saveLog(syncBaseData,total,1,description);
                return;
            }
            baseDictAllService.generateDicMap();
            Map<String, Map<String,String>> dicMap = baseDictAllService.getDicValueToCodeMap();
            // 主审设备类型关联关系
            List<AssetTypeRel> typeRelList = assetTypeRelService.findAll().stream().filter(item -> item.getType() == 1).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(typeRelList)) {
                log.info("主审资产类型未配置！");
                description = "主审资产类型未配置！";
                this.saveLog(syncBaseData,total,1,description);
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            //根据总量获取分页的组织机构
            Map<String, Object> result = objectMapper.readValue(totalResponse, Map.class);
            if (result.containsKey("TotalCount")) {
                total = (Integer) result.get("TotalCount");
                if (total > 0) {
                    Integer pageCount = total / DATA_SIZE;
                    if (total % DATA_SIZE != 0) {
                        pageCount++;
                    }
                    String batchNo = System.currentTimeMillis() + "";
                    for (int i = 0; i < pageCount; i++) {
                        param.put("PageIndex", i + 1);
                        param.put("PageSize", DATA_SIZE);
                        String response = sendPostRequest(address, "where=" + JSON.toJSONString(param));
                        if (StringUtils.isNotEmpty(response)) {
                            Map<String, Object> pageResult = objectMapper.readValue(response, Map.class);
                            if (pageResult.containsKey("DataSource")) {
                                List<Map<String, Object>> dataList = (List<Map<String, Object>>) pageResult.get("DataSource");
                                if (CollectionUtils.isNotEmpty(dataList)) {
                                    for (Map<String, Object> map : dataList) {
                                        String deviceType = (String) map.get("DeviceType");
                                        if (StringUtils.isNotEmpty(deviceType)) {
                                            log.info("deviceType:" + deviceType);
                                            Optional<AssetTypeRel> typeRelOptional = typeRelList.stream().filter(item -> deviceType.equals(item.getAuditType())).findFirst();
                                            if (typeRelOptional.isPresent()) {
                                                AssetTypeRel typeRel = typeRelOptional.get();
                                                String typeGuid = typeRel.getAssetTypeGuid();
                                                String typeName = typeRel.getAssetTypeName();
                                                if (StringUtils.isNotEmpty(typeGuid)) {
                                                    SyncAssetVO syncAssetVO = new SyncAssetVO();
                                                    syncAssetVO.setGuid(CommonTools.generateId());
                                                    syncAssetVO.setTypeGuid(typeGuid);
                                                    syncAssetVO.setTypeUnicode(typeName);
                                                    syncAssetVO.setBatchNo(batchNo);
                                                    syncAssetVO.setName((String) map.get("DeviceName"));
                                                    syncAssetVO.setIp((String) map.get("IPAddres"));
                                                    this.completeDomain(syncAssetVO);
                                                    String mac = (String) map.get("MacAddress");
                                                    if (StringUtils.isNotEmpty(mac)) {
                                                        syncAssetVO.setMac(parseMac(mac).toUpperCase());
                                                    }
                                                    String userName = (String) map.get("UserName");
                                                    this.completePersonInfo(syncAssetVO,userName);
                                                    if (StringUtils.isEmpty(syncAssetVO.getOrgName())) {
                                                        syncAssetVO.setOrgName((String) map.get("DeptName"));
                                                    }
                                                    syncAssetVO.setOsSetuptime((String) map.get("SetupTmos"));
                                                    syncAssetVO.setSerialNumber(map.get("ExternalDeviceModel") != null ? ((String) map.get("ExternalDeviceModel")).toUpperCase() : "");
                                                    syncAssetVO.setSyncUid(String.valueOf(map.get("DeviceID")));
                                                    syncAssetVO.setClientUpLastTime((String) map.get("LastTime"));
                                                    syncAssetVO.setDeviceStatus((Integer) map.get("RunStatus"));
                                                    syncAssetVO.setClientStatus(syncAssetVO.getClientUpLastTime().compareTo(DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN)) > 0 ? 1 : 0);
                                                    syncAssetVO.setRegisterTime((String) map.get("RegisterTime"));
                                                    syncAssetVO.setExtendDiskNumber(map.get("DiskSerial") != null ? ((String) map.get("DiskSerial")).toUpperCase() : "");
                                                    // 资产编号
                                                    syncAssetVO.setAssetNum(map.get("DeviceCode") != null ? ((String) map.get("DeviceCode")).toUpperCase() : "");
                                                    // 位置
                                                    syncAssetVO.setLocation((String) map.get("RoomNumber"));
                                                    // 密级
                                                    String policemenKind = (String) map.get("PolicemenKind");
                                                    if (StringUtils.isNotEmpty(policemenKind)) {
                                                        String equipmentIntensive = dicMap.get("设备信息-设备密级").get(policemenKind);
                                                        syncAssetVO.setEquipmentIntensive(equipmentIntensive);
                                                    }
                                                    // 备注
                                                    syncAssetVO.setRemarkInfo((String) map.get("FloorNumber"));
                                                    // 操作系统类型，取第二个空格之前
                                                    String osType = (String) map.get("OSType");
                                                    if (StringUtils.isNotEmpty(osType)) {
                                                        String[] osList = osType.split("\\s+");
                                                        if (osList.length >= 2) {
                                                            if (osType.toLowerCase().startsWith("windows server")) {
                                                                if (osList.length > 2) {
                                                                    syncAssetVO.setOsList((osList[0] + " " + osList[1] + " " + osList[2]).toUpperCase());
                                                                } else {
                                                                    syncAssetVO.setOsList((osList[0] + " " + osList[1]).toUpperCase());
                                                                }
                                                            } else {
                                                                syncAssetVO.setOsList((osList[0] + " " + osList[1]).toUpperCase());
                                                            }
                                                        } else {
                                                            syncAssetVO.setOsList(osType.toUpperCase());
                                                        }
                                                        // 系统架构
                                                        String deviceArch = getDeviceArch(osType);
                                                        syncAssetVO.setDeviceArch(deviceArch);
                                                    }
                                                    syncAssetVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_ZS);
                                                    syncAssetVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                                                    // 拓展字段
                                                    Map<String,Object> extendsInfo = new HashMap<>();
                                                    extendsInfo.put("extendSystem", map.get("OSType") != null ? ((String) map.get("OSType")).toUpperCase() : "");
                                                    extendsInfo.put("extendDiskNumber",map.get("DiskSerial") != null ? ((String) map.get("DiskSerial")).toUpperCase() : "");
                                                    syncAssetVO.setExtendInfos(extendsInfo);
                                                    // 入kafka
                                                    this.sendData(syncAssetVO, "asset", SyncBaseDataConstants.TOPIC_NAME_ASSET);
                                                } else {
                                                    log.info("无对应的资产类型：" + JSON.toJSONString(map));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                description = "主审资产数据同步失败！";
                status = 1;
            }
        } catch (Exception e) {
            description = "主审资产数据同步异常！";
            status = 1;
            log.error("", e);
        }
        this.saveLog(syncBaseData,total,status,description);
    }

    private static String parseMac(String mac) {
        if (StringUtils.isNotEmpty(mac)) {
            if (mac.length() == 12) {
                String regex = "(.{2})";
                mac = mac.replaceAll(regex, "$1-");
                mac = mac.substring(0, mac.length() - 1);
            }
        }
        return mac;
    }

    private static String getDeviceArch(String osType) {
        String pattern = "\\d+-BIT|\\d+-bit";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(osType);
        if (m.find()) {
            String deviceArch = m.group(0);
            String[] deviceArchs = deviceArch.split("-");
            return deviceArchs[0];
        }
        return "";
    }

    private static String sendPostRequest(String urlString, String param) {
        Document document;
        String result = "";
        try {
            Map<String,String> headers = new HashMap<>();
            headers.put("Content-Type","application/x-www-form-urlencoded");
            String response = HTTPUtil.POST(urlString,headers,param);
            document = DocumentHelper.parseText(response);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            result = root.getText();
        } catch (Exception e) {
            log.error("", e);
        }
        return result;
    }

    public static void main(String[] args) {
        String urlString = "https://192.168.119.7/NTDS/WebServer/AssetManagement.asmx/GetDeviceAll";
        String param = "where={\"SearchCondition\":{},\"PageIndex\":1,\"PageSize\":1}";
        String result = sendPostRequest(urlString, param);
        System.out.println(result);
    }
}

package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.ServerAuditProducerService;
import com.vrv.vap.admin.vo.SyncAssetVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/10/12
 * @description
 */
@Service
public class ServerAuditProducerServiceImpl extends BaseDataProducerServiceImpl implements ServerAuditProducerService {

    private static final Logger log = LoggerFactory.getLogger(ServerAuditProducerServiceImpl.class);

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_FS.equals(source)) {
            this.produceBxy(syncBaseData);
        }
    }

    public void produceBxy(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-服审资产同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        String address = protocolType + "://" + ip + ":" + port + "/api/vrv/foreign/device/v1/page?$page=0&$size=1000000";
        log.info("服审资产同步地址：" + address);
        Map<String,String> headers = new HashedMap();
        headers.put("Content-Type","application/json");
        headers.put("Authorization","Basic dnNlYzp2c2Vj");
        try {
            String response = HTTPUtil.GET(address,headers);
            if (StringUtils.isNotEmpty(response)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String,Object> result = objectMapper.readValue(response,Map.class);
                if (result.containsKey("data")) {
                    List<Map> dataList = (List<Map>) result.get("data");
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        total = dataList.size();
                        for (Map data : dataList) {
                            SyncAssetVO syncAssetVO = new SyncAssetVO();
                            syncAssetVO.setGuid(CommonTools.generateId());
                            Integer deviceTypeId = (Integer) data.get("deviceTypeId");
                            syncAssetVO.setTypeGuid(this.getTypeGuid(deviceTypeId));
                            syncAssetVO.setTypeUnicode(this.getTypeName(deviceTypeId));
                            syncAssetVO.setName((String) data.get("name"));
                            syncAssetVO.setIp((String) data.get("ip"));
                            this.completeDomain(syncAssetVO);
                            String mac = (String) data.get("mac");
                            if (StringUtils.isNotEmpty(mac)) {
                                mac = mac.replaceAll(":","-");
                            }
                            syncAssetVO.setMac(mac);
                            Map<String,Object> deviceOs = (Map<String,Object>) data.get("deviceOs");
                            String installTime = (String) deviceOs.get("installTime");
                            syncAssetVO.setOsSetuptime(installTime);
                            Map<String,Object> deviceAsset = (Map<String, Object>) data.get("deviceAsset");
                            String diskSerial = (String) deviceAsset.get("diskSerial");
                            syncAssetVO.setSerialNumber(diskSerial);
                            syncAssetVO.setOsList((String) deviceOs.get("name"));
                            syncAssetVO.setSyncUid((String) data.get("uniqueId"));
                            String holder = (String) data.get("holder");
                            this.completePersonInfo(syncAssetVO,holder);
                            List<Map<String,Object>> deviceHardwareList = (List<Map<String, Object>>) data.get("deviceHardwareList");
                            if (CollectionUtils.isNotEmpty(deviceHardwareList)) {
                                Map<String,Object> deviceHardware = deviceHardwareList.get(0);
                                String reportTime = (String) deviceHardware.get("reportTime");
                                syncAssetVO.setClientUpLastTime(reportTime);
                            }
                            boolean onlineStatus = (boolean) data.get("onlineStatus");
                            syncAssetVO.setDeviceStatus(onlineStatus ? 1 : 0);
                            syncAssetVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_FS);
                            syncAssetVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                            // 拓展字段
                            Map<String,Object> extendsInfo = new HashMap<>();
                            extendsInfo.put("extendSystem", deviceOs.get("name"));
                            extendsInfo.put("extendDiskNumber",diskSerial);
                            syncAssetVO.setExtendInfos(extendsInfo);
                            // 入kafka
                            this.sendData(syncAssetVO, "asset", SyncBaseDataConstants.TOPIC_NAME_ASSET);
                        }
                    }
                } else {
                    log.info("获取服审资产列表失败！");
                    status = 1;
                }
            } else {
                log.info("获取服审资产列表失败！");
                status = 1;
            }
        } catch (Exception e) {
            log.error("",e);
        }
        this.saveLog(syncBaseData,total,status,description);
    }

    private String getTypeGuid(Integer deviceTypeId) {
        switch (deviceTypeId) {
            case 0:
                return "2e064ec4885f464f8043d37ac7b489d9";
            case 1:
                return "500b468c848745cd9b78baf9949cfa63";
            case 5:
                return "2e064ec4885f464f8043d37ac7b489d9";
            case 6:
                return "ee34a1893d57460f8bc91b2eca1eaee7";
            case 9:
                return "67dce0923b3d455ab6a33dbabaed6556";
            case 13:
                return "67dce0923b3d455ab6a33dbabaed6556";
            default:
                return "";
        }
    }

    private String getTypeName(Integer deviceTypeId) {
        switch (deviceTypeId) {
            case 0:
                return "其他终端";
            case 1:
                return "笔记本";
            case 5:
                return "其他终端";
            case 6:
                return "台式机";
            case 9:
                return "其他服务器";
            case 13:
                return "其他服务器";
            default:
                return "";
        }
    }
}

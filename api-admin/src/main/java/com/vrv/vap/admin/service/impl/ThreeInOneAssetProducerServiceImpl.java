package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.util.Base64Util;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.ThreeInOneAssetProducerService;
import com.vrv.vap.admin.vo.SyncAssetVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/4/25
 * @description
 */
@Service
public class ThreeInOneAssetProducerServiceImpl extends BaseDataProducerServiceImpl implements ThreeInOneAssetProducerService {

    private static final Logger log = LoggerFactory.getLogger(ThreeInOneAssetProducerServiceImpl.class);

    private static final String GUID = "4024d801545944de8d4b7985ff1ad0f4";

    private static final String UNICODE = "通用存储介质";

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_RY.equals(source)) {
            this.produceBxy(syncBaseData);
        }
    }

    public void produceBxy(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-融合一红盘资产同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        String address = protocolType + "://" + ip + ":" + port + "/rhglthirdpart/udisks?page=0&size=100000";
        log.info("融合一红盘资产同步地址：" + address);
        String clientId = syncBaseData.getAccount();
        String clientSecret = syncBaseData.getPassword();
        String token = this.generateToken(clientId,clientSecret);
        Map<String, String> headers = generateHeaders(token);
        try {
            String response = HTTPUtil.GET(address, headers);
            log.info("融合一红盘资产返回结果:" + response);
            if (StringUtils.isEmpty(response)) {
                log.info("获取融合一红盘资产数据失败！");
                description = "融合一红盘资产同步地址请求失败！";
                this.saveLog(syncBaseData,total,1,description);
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            if (result.containsKey("data")) {
                List<Map<String,Object>> usbList = (List<Map<String,Object>>) result.get("data");
                if (CollectionUtils.isNotEmpty(usbList)) {
                    total = usbList.size();
                    String batchNo = System.currentTimeMillis() + "";
                    for (Map<String,Object> map : usbList) {
                        SyncAssetVO syncAssetVO = new SyncAssetVO();
                        syncAssetVO.setName((String) map.get("usbDiskSerial"));
                        syncAssetVO.setSyncUid(map.get("id").toString());
                        syncAssetVO.setGuid(CommonTools.generateId());
                        syncAssetVO.setTypeGuid(GUID);
                        syncAssetVO.setTypeUnicode(UNICODE);
                        syncAssetVO.setBatchNo(batchNo);
                        syncAssetVO.setSerialNumber((String) map.get("usbDiskSerial"));
                        syncAssetVO.setEquipmentIntensive(map.get("usbDiskSecurityLevel").toString());
                        String responsibleName = (String) map.get("responsibleName");
                        this.completePersonInfo(syncAssetVO,responsibleName);
                        // 国产
                        syncAssetVO.setTermType("1");
                        syncAssetVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_RY);
                        syncAssetVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                        // 拓展字段
                        Map<String,Object> extendsInfo = new HashMap<>();
                        syncAssetVO.setExtendInfos(extendsInfo);
                        // 入kafka
                        this.sendData(syncAssetVO,"asset",SyncBaseDataConstants.TOPIC_NAME_ASSET);
                    }
                }
            } else {
                if (result.containsKey("title")) {
                    String title = (String) result.get("title");
                    log.info("获取融合一红盘资产数据失败，失败原因：" + title);
                    description = title;
                    status = 1;
                }
            }
        } catch (Exception e) {
            description = "融合一红盘资产数据同步异常！";
            status = 1;
            log.error("",e);
        }
        this.saveLog(syncBaseData,total,status,description);
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
//        headers.put("Content-Type", "application/json");
        return headers;
    }
}

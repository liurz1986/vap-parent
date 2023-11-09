package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.FileProducerService;
import com.vrv.vap.admin.util.SM4Util;
import com.vrv.vap.admin.vo.SyncFileVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/5/13
 * @description
 */
@Service
public class FileProducerServiceImpl extends BaseDataProducerServiceImpl implements FileProducerService {

    private static final Logger log = LoggerFactory.getLogger(FileProducerServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTpl;

    private static final String SYSNAME = "北信源涉密网综合审计监管系统";

    private static final String REGREASON = "文件信息同步";

    private static final String SYNC_FILE_DEVICE_ID = "_SYNC_FILE_DEVICE_ID";

    private static final String SYNC_FILE_SECKEY = "_SYNC_FILE_SECKEY";

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_MB.equals(source)) {
            this.produceBxy(syncBaseData);
        }
    }

    public void produceBxy(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-文件信息同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        String fileUrl = protocolType + "://" + ip + ":" + port + "/deck/API/getFileInfo";
        log.info("文件信息列表地址：" + fileUrl);
        String deviceId = this.getDeviceId(syncBaseData);
        if (StringUtils.isEmpty(deviceId)) {
            log.info("同步文件信息获取deviceId失败！");
            return;
        }
        Map<String,Object> param = new HashMap<>();
        param.put("deviceId",deviceId);
        param.put("getAll",1);
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        try {
            String response = HTTPUtil.POST(fileUrl,headers, JSON.toJSONString(param));
            if (StringUtils.isNotEmpty(response)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> result = objectMapper.readValue(response, Map.class);
                Integer code = (Integer) result.get("Code");
                if (code == 0) {
                    String enData = (String) result.get("Data");
                    String seckey = redisTpl.opsForValue().get(SYNC_FILE_SECKEY);
                    SM4Util sm4Util = new SM4Util(seckey);
                    String deData = sm4Util.decrypt(enData);
                    SyncFileVO syncFileVO = new SyncFileVO();
                    Map<String,Object> map = objectMapper.readValue(deData,Map.class);
                    List<Map> rows = (List<Map>) map.get("rows");
                    if (CollectionUtils.isNotEmpty(rows)) {
                        total = rows.size();
                        for (Map data : rows) {
                            syncFileVO.setDataFlag((String) data.get("fileGuid"));
                            syncFileVO.setBusinessType("未知");
                            Integer fileSecId = (Integer) data.get("fileSecId");
                            Integer secretLevel = getSecretLevel(fileSecId);
                            syncFileVO.setSecretLevel(secretLevel);
                            syncFileVO.setSyncUid((String) data.get("fileGuid"));
                            syncFileVO.setFileName((String) data.get("fileName"));
                            syncFileVO.setFileType((String) data.get("fileType"));
                            syncFileVO.setFileSize(Float.parseFloat(data.get("fileSize").toString()));
                            syncFileVO.setFileStatus((Integer) data.get("fileStatus"));
                            syncFileVO.setDraftUser(JSON.toJSONString(data.get("draftUser")));
                            syncFileVO.setDetermineUser(JSON.toJSONString(data.get("determineUser")));
                            syncFileVO.setSaleUser(JSON.toJSONString(data.get("saleUser")));
                            syncFileVO.setAwareScope(JSON.toJSONString(data.get("awareScope")));
                            syncFileVO.setSecretPeriod(JSON.toJSONString(data.get("secretPeriod")));
                            syncFileVO.setDetermineReason(JSON.toJSONString(data.get("determineReason")));
                            syncFileVO.setFileAuth(JSON.toJSONString(data.get("fileAuth")));
                            syncFileVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_MB);
                            syncFileVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                            // 入kafka
                            this.sendData(syncFileVO,"file", SyncBaseDataConstants.TOPIC_NAME_FILE);
                        }
                    }
                } else {
                    String message = (String) result.get("Message");
                    log.info("获取文件信息列表失败，原因：" + message);
                    description = message;
                    status = 1;
                }
            } else {
                log.info("获取文件信息列表失败！");
                description = "文件信息列表地址请求失败！";
                status = 1;
            }

        } catch (Exception e) {
            log.error("",e);
            description = "文件信息资产数据同步异常！";
            status = 1;
        }
        this.saveLog(syncBaseData,total,status,description);
    }

    private Integer getSecretLevel(Integer secId) {
        Integer secretLevel;
        switch (secId) {
            case 1:
                secretLevel = 4;
                break;
            case 2:
                secretLevel = 3;
                break;
            case 4:
                secretLevel = 2;
                break;
            case 8:
                secretLevel = 1;
                break;
            case 16:
                secretLevel = 0;
                break;
            default:
                secretLevel = 4;
                break;
        }
        return secretLevel;
    }

    private String getDeviceId(SyncBaseData syncBaseData) {
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        Integer status = 0;
        String description = "";
        String deviceId = redisTpl.opsForValue().get(SYNC_FILE_DEVICE_ID);
        if (StringUtils.isEmpty(deviceId)) {
            String localIp = System.getenv("LOCAL_SERVER_IP");
            String authUrl = protocolType + "://" + ip +":" + port + "/deck/API/authRegistration?ip=" + localIp + "&sysName=" + SYSNAME + "&regReason=" + REGREASON;
            log.info("文件信息认证地址：" + authUrl);
            Map<String,String> headers = new HashMap<>();
            try {
                String response = HTTPUtil.GET(authUrl,headers);
                if (StringUtils.isNotEmpty(response)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> result = objectMapper.readValue(response, Map.class);
                    Integer code = (Integer) result.get("Code");
                    String message = (String) result.get("Message");
                    if (code != 0) {
                        log.info("文件信息认证注册失败,原因：" + message);
                        description = message;
                        this.saveLog(syncBaseData,0,1,description);
                        return "";
                    }
                    Map data = (Map) result.get("Data");
                    if (data != null) {
                        deviceId = (String) data.get("deviceID");
                        String seckey = (String) data.get("seckey");
                        redisTpl.opsForValue().set(SYNC_FILE_DEVICE_ID,deviceId);
                        redisTpl.opsForValue().set(SYNC_FILE_SECKEY,seckey);
                    } else {
                        log.info("文件信息认证注册data为空");
                        description = "文件信息认证注册返回数据为空";
                        status = 1;
                    }
                } else {
                    log.info("文件信息认证注册失败！");
                    description = "文件信息认证地址请求失败";
                    status = 1;
                }
            } catch (Exception e) {
                log.error("",e);
                description = "文件信息认证地址请求失败";
                status = 1;
            }
        }
        if (status == 1) {
            this.saveLog(syncBaseData,0,status,description);
        }
        return deviceId;
    }
}

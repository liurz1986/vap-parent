package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.AssetTypeRel;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.AssetTypeRelService;
import com.vrv.vap.admin.service.PermitAssetProducerService;
import com.vrv.vap.admin.vo.SyncAssetVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/4/26
 * @description
 */
@Service
public class PermitAssetProducerServiceImpl extends BaseDataProducerServiceImpl implements PermitAssetProducerService {

    private static final Logger log = LoggerFactory.getLogger(PermitAssetProducerServiceImpl.class);

    @Autowired
    AssetTypeRelService assetTypeRelService;

    private static final String UNICODE_TERMINAL = "终端";

    private static final String UNICODE_SERVER = "服务器";

    private static final String UNICODE_NETWORKDEVICE = "网络设备";

    private static final String CLASS_ID_TERMINAL = "2";

    private static final String CLASS_ID_NETWORKDEVICE = "4";

    private static final String CLASS_ID_SERVER = "5";

    private static final Integer DATA_SIZE = 50;

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_ZR.equals(source)) {
            this.produceBxy(syncBaseData);
        }
    }

    public void produceBxy(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-准入资产同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        String address = protocolType + "://" + ip + ":" + port + "/cfg.php?controller=sock&action=getOnlineDev";
        log.info("准入资产同步地址：" + address);
        try {
            String totalResponse = HTTPUtil.GET(address + "&limit=1", new HashMap<>());
            if (StringUtils.isEmpty(totalResponse)) {
                log.info("准入资产同步地址请求失败！");
                description = "准入资产同步地址请求失败！";
                this.saveLog(syncBaseData,total,1,description);
                return;
            }
            List<AssetTypeRel> typeRelList = assetTypeRelService.findAll().stream().filter(item -> item.getType() == 2).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(typeRelList)) {
                log.info("准入资产类型未配置！");
                description = "准入资产类型未配置！";
                this.saveLog(syncBaseData,total,1,description);
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            //根据总量获取分页的组织机构
            Map<String, Object> result = objectMapper.readValue(totalResponse, Map.class);
            if (result.containsKey("total")) {
                total = Integer.valueOf((String)result.get("total"));
                if (total > 0) {
                    Integer pageCount = total / DATA_SIZE;
                    if (total % DATA_SIZE != 0) {
                        pageCount ++;
                    }
                    String batchNo = System.currentTimeMillis() + "";
                    for (int i = 0; i < pageCount; i++) {
                        String response = HTTPUtil.GET(address + "&start=" + i*DATA_SIZE + "&limit=" + DATA_SIZE , new HashMap<>());
                        if (StringUtils.isNotEmpty(response)) {
                            Map<String,Object> pageResult = objectMapper.readValue(response,Map.class);
                            if (pageResult.containsKey("data")) {
                                List<Map<String,Object>> dataList = (List<Map<String, Object>>) pageResult.get("data");
                                if (CollectionUtils.isNotEmpty(dataList)) {
                                    for (Map<String,Object> map : dataList) {
                                        SyncAssetVO syncAssetVO = new SyncAssetVO();
                                        syncAssetVO.setGuid(CommonTools.generateId());
                                        syncAssetVO.setBatchNo(batchNo);
                                        String devClassId = (String) map.get("devclass_id");
                                        if (CLASS_ID_TERMINAL.equals(devClassId) || CLASS_ID_NETWORKDEVICE.equals(devClassId) || CLASS_ID_SERVER.equals(devClassId)) {
                                            Optional<AssetTypeRel> typeRelOptional;
                                            if (CLASS_ID_TERMINAL.equals(devClassId)) {
                                                typeRelOptional = typeRelList.stream().filter(item -> UNICODE_TERMINAL.equals(item.getAuditType())).findFirst();
                                            } else if (CLASS_ID_SERVER.equals(devClassId)) {
                                                typeRelOptional = typeRelList.stream().filter(item -> UNICODE_SERVER.equals(item.getAuditType())).findFirst();
                                            } else {
                                                typeRelOptional = typeRelList.stream().filter(item -> UNICODE_NETWORKDEVICE.equals(item.getAuditType())).findFirst();
                                            }
                                            if (typeRelOptional.isPresent()) {
                                                AssetTypeRel auditTypeRel = typeRelOptional.get();
                                                syncAssetVO.setTypeGuid(auditTypeRel.getAssetTypeGuid());
                                                syncAssetVO.setTypeUnicode(auditTypeRel.getAssetTypeName());
                                            } else {
                                                log.info("设备类型不匹配！" + JSON.toJSONString(map));
                                                continue;
                                            }

                                            syncAssetVO.setName((String) map.get("dev_name"));
                                            syncAssetVO.setIp((String) map.get("ip"));
                                            this.completeDomain(syncAssetVO);
                                            String mac = (String) map.get("mac");
                                            if (StringUtils.isNotEmpty(mac)) {
                                                mac = mac.replaceAll(":","-");
                                            }
                                            syncAssetVO.setMac(mac);
                                            syncAssetVO.setTerminalType((String) map.get("devclass_id"));
                                            syncAssetVO.setOsList((String) map.get("systype"));
                                            String responsible = (String) map.get("responsible");
                                            this.completePersonInfo(syncAssetVO,responsible);
                                            syncAssetVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_ZR);
                                            syncAssetVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                                            syncAssetVO.setSyncUid(syncAssetVO.getIp() + "-" + syncAssetVO.getMac());
                                            // 拓展字段
                                            Map<String,Object> extendsInfo = new HashMap<>();
                                            extendsInfo.put("extendSystem", map.get("systype"));
                                            extendsInfo.put("extendTypeSno",map.get("vendor"));
                                            extendsInfo.put("extendVersionInfo",map.get("model"));
                                            syncAssetVO.setExtendInfos(extendsInfo);
                                            // 入kafka
                                            this.sendData(syncAssetVO,"asset",SyncBaseDataConstants.TOPIC_NAME_ASSET);
                                        } else {
                                            log.info("设备类型不匹配！" + JSON.toJSONString(map));
                                        }
                                    }
                                }
                            } else {
                                description = "主融合一红盘资产数据同步失败！";
                                status = 1;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            description = "主融合一红盘资产数据同步异常！";
            status = 1;
            log.error("",e);
        }
        this.saveLog(syncBaseData,total,status,description);
    }

}

package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.AssetTypeRel;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.AssetTypeRelService;
import com.vrv.vap.admin.service.OperationManageAssetProducerService;
import com.vrv.vap.admin.vo.SyncAssetVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/6/20
 * @description
 */
@Service
public class OperationManageAssetProducerServiceImpl extends BaseDataProducerServiceImpl implements OperationManageAssetProducerService {

    private static final Logger log = LoggerFactory.getLogger(OperationManageAssetProducerServiceImpl.class);

    private static final Integer DATA_SIZE = 20;

    @Resource
    AssetTypeRelService assetTypeRelService;

    @Override
    public void produce(SyncBaseData syncBaseData) {
        String source = syncBaseData.getSource();
        if (SyncSourceConstants.SOURCE_BXY_YG.equals(source)) {
            this.produceBxy(syncBaseData);
        }
    }

    public void produceBxy(SyncBaseData syncBaseData) {
        Integer total = 0;
        Integer status = 0;
        String description = "";
        log.info("北信源-运管资产同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        Integer rows = 1;
        Integer page = 1;
        String param = "?rows=" + rows + "&page=" + page;
        String address = protocolType + "://" + ip + ":" + port + "/yw/api/outside/getAssetDetail";
        log.info("运管资产同步地址：" + address);
        Map<String,String> headers = new HashMap<>();
        try {
            String totalResponse = HTTPUtil.GET(address + param,headers);
            if (StringUtils.isEmpty(totalResponse)) {
                log.info("获取运管资产数据失败！");
                description = "运管资产同步地址请求失败！";
                this.saveLog(syncBaseData,total,1,description);
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map totalResult = objectMapper.readValue(totalResponse,Map.class);
            if (totalResult.containsKey("total")) {
                total = (Integer) totalResult.get("total");
                if (total > 0) {
                    Integer pageCount = total / DATA_SIZE;
                    if (total % DATA_SIZE != 0) {
                        pageCount++;
                    }
                    List<AssetTypeRel> typeRelList = assetTypeRelService.findAll().stream().filter(item -> item.getType() == 3).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(typeRelList)) {
                        log.info("运管资产类型未配置！");
                        description = "运管资产类型未配置！";
                        this.saveLog(syncBaseData,total,1,description);
                        return;
                    }
                    String batchNo = System.currentTimeMillis() + "";
                    for (int i = 0; i < pageCount; i++) {
                        rows = DATA_SIZE;
                        page = i + 1;
                        param = "?rows=" + rows + "&page=" + page;
                        String response = HTTPUtil.GET(address + param,headers);
                        if (StringUtils.isNotEmpty(response)) {
                            Map<String, Object> pageResult = objectMapper.readValue(response, Map.class);
                            if (pageResult.containsKey("rows")) {
                                List<Map<String, Object>> dataList = (List<Map<String, Object>>) pageResult.get("rows");
                                if (CollectionUtils.isNotEmpty(dataList)) {
                                    for (Map<String, Object> map : dataList) {
                                        String assetType = (String) map.get("assetType");
                                        log.info("assetType:" + assetType);
                                        Optional<AssetTypeRel> typeRelOptional = typeRelList.stream().filter(item -> assetType.equals(item.getAuditType())).findFirst();
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
                                                syncAssetVO.setName((String) map.get("key"));
                                                syncAssetVO.setIp((String) map.get("ip"));
                                                this.completeDomain(syncAssetVO);
                                                String mac = (String) map.get("mac");
                                                if (StringUtils.isNotEmpty(mac)) {
                                                    mac = mac.replaceAll(":","-");
                                                }
                                                syncAssetVO.setMac(mac);
                                                String dulyName = (String) map.get("dulyName");
                                                this.completePersonInfo(syncAssetVO,dulyName);
                                                syncAssetVO.setOsList((String) map.get("sysName"));
                                                syncAssetVO.setSerialNumber((String) map.get("uniqueIdent"));
                                                syncAssetVO.setSyncUid(syncAssetVO.getIp() + "-" + syncAssetVO.getMac());
                                                syncAssetVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_YG);
                                                syncAssetVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                                                // 拓展字段
                                                Map<String,Object> extendsInfo = new HashMap<>();
                                                extendsInfo.put("extendSystem", map.get("sysName"));
                                                extendsInfo.put("extendDiskNumber",map.get("uniqueIdent"));
                                                syncAssetVO.setExtendInfos(extendsInfo);
                                                // 入kafka
                                                this.sendData(syncAssetVO, "asset", SyncBaseDataConstants.TOPIC_NAME_ASSET);
                                            }
                                        }
                                    }
                                }
                            } else {
                                description = "运管资产数据同步失败！";
                                status = 1;
                            }
                        }
                    }
                }
            } else {
                description = "获取运管资产数据总数失败！";
                status = 1;
                log.info("获取运管资产数据总数失败！");
            }
        } catch (Exception e) {
            description = "运管资产数据同步异常！";
            status = 1;
            log.error("",e);
        }
        this.saveLog(syncBaseData,total,status,description);
    }

}

package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.ImportExcelUtil;
import com.vrv.vap.admin.model.AssetTypeRel;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.AssetTypeRelService;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.service.BookAssetProducerService;
import com.vrv.vap.admin.vo.SyncAssetVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2023/3/29
 * @description
 */
@Service
public class BookAssetProducerServiceImpl extends BaseDataProducerServiceImpl implements BookAssetProducerService {

    private static final Logger log = LoggerFactory.getLogger(BookAssetProducerServiceImpl.class);

    @Resource
    AssetTypeRelService assetTypeRelService;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Override
    public ErrorCode importBookData(MultipartFile file) {
        SyncBaseData syncBaseData = new SyncBaseData();
        syncBaseData.setName("IT运维资产台账导入");
        syncBaseData.setType("asset");
        Integer total = 0;
        syncBaseData.setSource(SyncSourceConstants.SOURCE_BXY_YWTZ);
        String fileName = file.getOriginalFilename();
        InputStream in = null;
        List<List<Object>> listob;
        if (!file.isEmpty()) {
            try {
                in = file.getInputStream();
                listob = ImportExcelUtil.getListByExcel(in, fileName);
                if (!CollectionUtils.isEmpty(listob)) {
                    List<Object> fieldNames = listob.get(0);
                    Boolean result = this.validateField(fieldNames,"IT运维资产台账导入模板.xls");
                    if (!result) {
                        this.saveLog(syncBaseData,total,1,ErrorCode.OFFLINE_CONTENT_NOT_MATCH.getResult().getMessage());
                        return ErrorCode.OFFLINE_CONTENT_NOT_MATCH;
                    }
                    baseDictAllService.generateDicMap();
                    Map<String, Map<String,String>> dicMap = baseDictAllService.getDicValueToCodeMap();
                    // 运维资产台账设备类型关联关系
                    List<AssetTypeRel> typeRelList = assetTypeRelService.findAll().stream().filter(item -> item.getType() == 4).collect(Collectors.toList());
                    String batchNo = System.currentTimeMillis() + "";
                    for (int i = 1; i < listob.size(); i++) {
                        List<Object> lo = listob.get(i);
                        String deviceType = (String) lo.get(2);
                        log.info("deviceType:" + deviceType);
                        if (StringUtils.isEmpty(deviceType)) {
                            continue;
                        }
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
                                syncAssetVO.setDeviceDesc((String) lo.get(2));
                                // 资产编号
                                syncAssetVO.setAssetNum(lo.get(3) != null ? ((String) lo.get(3)).toUpperCase() : "");
                                syncAssetVO.setSyncUid((String) lo.get(3));
                                // 名称
                                syncAssetVO.setName((String) lo.get(5));
                                // 品牌型号
                                syncAssetVO.setTypeSnoGuid((String) lo.get(6));
                                // 密级
                                String secretLevel = (String) lo.get(7);
                                if (StringUtils.isNotEmpty(secretLevel)) {
                                    String equipmentIntensive = dicMap.get("设备信息-设备密级").get(secretLevel);
                                    syncAssetVO.setEquipmentIntensive(equipmentIntensive);
                                }
                                String responsibleName = (String) lo.get(10);
                                this.completePersonInfo(syncAssetVO,responsibleName);
                                if (StringUtils.isEmpty(syncAssetVO.getOrgName())) {
                                    syncAssetVO.setOrgName((String) lo.get(9));
                                }
                                // 放置地点
                                syncAssetVO.setLocation((String) lo.get(11));
                                syncAssetVO.setOsSetuptime((String) lo.get(12));
                                syncAssetVO.setRegisterTime((String) lo.get(13));
                                syncAssetVO.setExtendDiskNumber(lo.get(14) != null ? ((String) lo.get(14)).toUpperCase() : "");
                                syncAssetVO.setIp((String) lo.get(15));
                                this.completeDomain(syncAssetVO);
                                //TODO 确认格式
                                String mac = (String) lo.get(16);
                                if (StringUtils.isNotEmpty(mac)) {
                                    syncAssetVO.setMac(this.transformMac(mac).toUpperCase());
                                }
                                // 操作系统类型，取第二个空格之前
                                String osType = (String) lo.get(19);
                                if (StringUtils.isNotEmpty(osType)) {
                                    String[] osList = osType.split("\\s+");
                                    if (osList.length > 2) {
                                        syncAssetVO.setOsList((osList[0] + " " + osList[1]).toUpperCase());
                                        // 系统架构，win7取最后一个空格字符串，xp取倒数第二个空格字符串
                                        String deviceArch = "";
                                        if ("XP".equals(osList[1].toUpperCase())) {
                                            deviceArch = osList[osList.length - 2];
                                        } else {
                                            deviceArch = osList[osList.length - 1];
                                        }
                                        if (StringUtils.isNotEmpty(deviceArch) && deviceArch.toLowerCase().startsWith("x")) {
                                            deviceArch = deviceArch.substring(1);
                                        }
                                        syncAssetVO.setDeviceArch(deviceArch);
                                    } else {
                                        syncAssetVO.setOsList(osType.toUpperCase());
                                    }
                                }
                                // 拓展字段
                                Map<String,Object> extendsInfo = new HashMap<>();
                                extendsInfo.put("extendDiskNumber", lo.get(14) != null ? ((String) lo.get(14)).toUpperCase() : "");
                                syncAssetVO.setExtendInfos(extendsInfo);
                                syncAssetVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_YWTZ);
                                syncAssetVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                                // 入kafka
                                this.sendData(syncAssetVO, "asset", SyncBaseDataConstants.TOPIC_NAME_ASSET);
                            } else {
                                log.info("无对应的资产类型：" + deviceType);
                            }
                            total++;
                        }
                    }
                    this.saveLog(syncBaseData,total,0,"同步成功");
                } else {
                    this.saveLog(syncBaseData,total,1,ErrorCode.OFFLINE_CONTENT_NOT_MATCH.getResult().getMessage());
                    return ErrorCode.OFFLINE_CONTENT_NOT_MATCH;
                }
            } catch (Exception e) {
                log.error("excel文件读取异常！",e);
                this.saveLog(syncBaseData,total,1,ErrorCode.OFFLINE_CONTENT_PARSE_ERROR.getResult().getMessage());
                return ErrorCode.OFFLINE_CONTENT_PARSE_ERROR;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("",e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 校验导入数据字段
     * @param fieldNames
     * @return
     */
    public boolean validateField(List<Object> fieldNames,String templateName) {
        if (CollectionUtils.isEmpty(fieldNames)) {
            return false;
        }
        InputStream st = null;
        try {
            st = this.getClass().getResourceAsStream("/templates/" + templateName);
            List<Object> fieldList = new ArrayList<>();
            List<List<Object>> listob = ImportExcelUtil.getListByExcel(st, templateName);
            if (!CollectionUtils.isEmpty(listob)) {
                fieldList = listob.get(0);
            }
            if (CollectionUtils.isNotEmpty(fieldList)) {
                fieldList.removeAll(fieldNames);
                if (CollectionUtils.isEmpty(fieldList)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("",e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 转换Mac格式
     * @return
     */
    public String transformMac(String mac) {
        if (StringUtils.isNotEmpty(mac)) {
            mac = mac.replaceAll("-","");
            return mac.replaceAll("(.{2})", "-$1").substring(1);
        }
        return mac;
    }

}

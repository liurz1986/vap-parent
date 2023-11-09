package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.SyncBaseDataConstants;
import com.vrv.vap.admin.common.constant.SyncSourceConstants;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.OperationManageOrgProducerService;
import com.vrv.vap.admin.vo.SyncOrgVO;
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
 * @date 2022/6/20
 * @description
 */
@Service
public class OperationManageOrgProducerServiceImpl extends BaseDataProducerServiceImpl implements OperationManageOrgProducerService {

    private static final Logger log = LoggerFactory.getLogger(OperationManageOrgProducerServiceImpl.class);

    private static final Integer DATA_SIZE = 20;

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
        log.info("北信源-运管机构同步");
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        Integer rows = 1;
        Integer page = 1;
        String param = "?rows=" + rows + "&page=" + page;
        String address = protocolType + "://" + ip + ":" + port + "/yw/api/outside/getDepartMents";
        log.info("运管机构同步地址：" + address);
        Map<String,String> headers = new HashMap<>();
        try {
            String totalResponse = HTTPUtil.GET(address + param,headers);
            if (StringUtils.isEmpty(totalResponse)) {
                log.info("运管资产同步地址请求失败！");
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
                                        SyncOrgVO syncOrgVO = new SyncOrgVO();
                                        syncOrgVO.setSyncUid((String) map.get("guid"));
                                        syncOrgVO.setSyncSource(SyncSourceConstants.SOURCE_BXY_YG);
                                        syncOrgVO.setDataSourceType(SyncBaseDataConstants.SOURCE_TYPE_SYNC);
                                        syncOrgVO.setCode((String) map.get("guid"));
                                        syncOrgVO.setName((String) map.get("name"));
                                        syncOrgVO.setParentCode((String) map.get("parentId"));
                                        // 入kafka
                                        this.sendData(syncOrgVO,"org", SyncBaseDataConstants.TOPIC_NAME_ORG);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                log.info("获取运管机构数据总数失败！");
                description = "获取运管机构数据总数失败！";
                status = 1;
            }
        } catch (Exception e) {
            description = "运管资产数据同步异常！";
            status = 1;
            log.error("",e);
        }
        this.saveLog(syncBaseData,total,status,description);
    }
}

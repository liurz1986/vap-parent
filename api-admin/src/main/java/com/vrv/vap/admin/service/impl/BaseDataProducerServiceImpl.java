package com.vrv.vap.admin.service.impl;

import cn.com.vrv.gmp.key.common.util.SecureKeyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.common.util.StringUtil;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/4/22
 * @description
 */
@Service
public class BaseDataProducerServiceImpl implements BaseDataProducerService {

    private static final Logger log = LoggerFactory.getLogger(BaseDataProducerServiceImpl.class);

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Resource
    private SyncBaseDataLogService syncBaseDataLogService;

    @Resource
    BasePersonZjgService basePersonZjgService;

    @Resource
    BaseSecurityDomainService baseSecurityDomainService;

    @Resource
    BaseSecurityDomainIpSegmentService baseSecurityDomainIpSegmentService;

    @Resource
    BaseKoalOrgService baseKoalOrgService;

    @Resource
    SystemConfigService systemConfigService;

    @Override
    public void produce(SyncBaseData syncBaseData) {

    }

    @Override
    public void sendData(Object object, String dataType,String topicName) {
        Map<String,Object> result = new HashMap<>();
        result.put("dataType",dataType);
        result.put("data",object);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(result);
            kafkaSenderService.send(topicName,null,content);
        } catch (Exception e) {
            log.error("",e);
        }
    }

    @Override
    public void saveLog(SyncBaseData syncBaseData, Integer totalCount, Integer status, String description) {
        SyncBaseDataLog syncBaseDataLog = new SyncBaseDataLog();
        syncBaseDataLog.setTaskName(syncBaseData.getName());
        syncBaseDataLog.setType(syncBaseData.getType());
        syncBaseDataLog.setSource(syncBaseData.getSource());
        syncBaseDataLog.setTotalCount(totalCount);
        syncBaseDataLog.setCreateTime(new Date());
        syncBaseDataLog.setStatus(status);
        if (status == 0) {
            if (description != null) {
                syncBaseDataLog.setDescription(description);
            } else {
                syncBaseDataLog.setDescription("同步成功");
            }
        } else {
            syncBaseDataLog.setDescription("同步失败：" + description);
        }
        syncBaseDataLogService.save(syncBaseDataLog);
    }

    @Override
    public void completePersonInfo(SyncAssetVO syncAssetVO,String userName) {
        if (StringUtils.isEmpty(userName)) {
            return;
        }
        syncAssetVO.setResponsibleName(userName);
        List<BasePersonZjg> basePersonZjgs = basePersonZjgService.findByProperty(BasePersonZjg.class,"userName",userName);
        if (CollectionUtils.isNotEmpty(basePersonZjgs)) {
            BasePersonZjg basePersonZjg = basePersonZjgs.get(0);
            syncAssetVO.setResponsibleCode(basePersonZjg.getUserNo());
            syncAssetVO.setOrgName(basePersonZjg.getOrgName());
            syncAssetVO.setOrgCode(basePersonZjg.getOrgCode());
        }
    }

    public void completeDomain(SyncAssetVO syncAssetVO) {
        String ip = syncAssetVO.getIp();
        BaseSecurityDomainIpSegment baseSecurityDomainIpSegment =  baseSecurityDomainIpSegmentService.findByIp(ip);
        if (baseSecurityDomainIpSegment == null) {
            return ;
        }
        List<BaseSecurityDomain> areas = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class, "code", baseSecurityDomainIpSegment.getCode());
        if (areas != null && areas.size() > 0) {
            BaseSecurityDomain baseSecurityDomain = areas.get(0);
            syncAssetVO.setDomainName(baseSecurityDomain.getDomainName());
        }
    }

    public void completeOrgInfo(SyncAssetVO syncAssetVO) {
        String orgCode = syncAssetVO.getOrgCode();
        BaseKoalOrg baseKoalOrg = baseKoalOrgService.findByCode(orgCode);
        if (baseKoalOrg != null) {
            syncAssetVO.setOrgName(baseKoalOrg.getName());
        }
    }

    @Override
    public String getUrlPrefix(SyncBaseData syncBaseData) {
        String ip = syncBaseData.getIp();
        String port = syncBaseData.getPort();
        String protocolType = syncBaseData.getProtocolType();
        String prefix = protocolType + "://"  + ip + ":" + port;
        return prefix;
    }

    public String getKitUrlSuffix(SyncBaseData syncBaseData) {
        String suffix = "&key=" + getKitSecureKey(syncBaseData) + "&ReqID=" + this.getKitReqId();
        return suffix;
    }

    /**
     * 获取套件自监管系统id
     * @return
     */
    public Integer getKitReqId() {
        SystemConfig sysConfig = systemConfigService.findByConfId("sync_base_data_kit_req_id");
        if (sysConfig != null) {
            String reqId = sysConfig.getConfValue();
            return Integer.valueOf(reqId);
        }
        return null;
    }

    /**
     * 获取套件自监管key
     * @return
     */
    public String getKitKey() {
        SystemConfig sysConfig = systemConfigService.findByConfId("sync_base_data_kit_key");
        if (sysConfig != null) {
            return sysConfig.getConfValue();
        }
        return "";
    }

    /**
     * 获取套件加密key
     * @param syncBaseData
     * @return
     */
    public String getKitSecureKey(SyncBaseData syncBaseData) {
        String secretKey = "";
        String prefix = this.getUrlPrefix(syncBaseData);
        String url = prefix + "/api/getalgorithm?type=90&ReqID=" + this.getKitReqId();
        try {
            log.info("请求地址：" + url);
            String result = HTTPUtil.GET(url,null);
            if (com.vrv.vap.common.utils.StringUtils.isEmpty(result)) {
                log.info("调用套件加密接口失败");
                return secretKey;
            }
            Document document = DocumentHelper.parseText(result);
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element errorCode = root.element("ErrorCode");
            if (errorCode != null) {
                log.info("获取套件加密key失败");
                return secretKey;
            }
            String algorithm = root.elementText("Algorithm");
            String params = root.elementText("Params");
            // 暂时只支持SM4
            if (com.vrv.vap.common.utils.StringUtils.isNotEmpty(algorithm) && "SM4".equals(algorithm.toUpperCase())) {
                String[] paramsArr = params.split(",");
                String randomStr = StringUtil.getRandomStr(15);
                secretKey = SecureKeyUtils.encodeSm4Value(paramsArr[0],paramsArr[1],paramsArr[2],randomStr,this.getKitKey());
            } else {
                log.info("算法为空或不是SM4");
            }
        } catch (Exception e) {
            log.error("",e);
        }
        return secretKey;
    }
}

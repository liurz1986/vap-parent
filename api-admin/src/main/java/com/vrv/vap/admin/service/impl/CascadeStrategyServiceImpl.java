package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.util.AESUtil;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.mapper.CascadeStrategyMapper;
import com.vrv.vap.admin.model.CascadePlatform;
import com.vrv.vap.admin.model.CascadeStrategy;
import com.vrv.vap.admin.model.CascadeStrategyDetail;
import com.vrv.vap.admin.service.CascadePlatformService;
import com.vrv.vap.admin.service.CascadeStrategyDetailService;
import com.vrv.vap.admin.service.CascadeStrategyService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author lilang
 * @date 2021/3/25
 * @description
 */
@Service
@Transactional
public class CascadeStrategyServiceImpl extends BaseServiceImpl<CascadeStrategy> implements CascadeStrategyService {

    private static final Logger logger = LoggerFactory.getLogger(CascadeStrategyServiceImpl.class);

    @Resource
    CascadeStrategyMapper cascadeStrategyMapper;


    @Resource
    CascadePlatformService cascadePlatformService;


    @Resource
    CascadeStrategyDetailService cascadeStrategyDetailService;

    String cKey = "1234567887654321";

    @Override
    public Boolean updateStrategy(CascadeStrategy cascadeStrategy) {
        Integer id = cascadeStrategy.getId();
        CascadeStrategy oldCascadeStrategy = cascadeStrategyMapper.selectByPrimaryKey(id);
        String oldPlatformId = oldCascadeStrategy.getPlatformId();

        cascadeStrategy.setUpdateTime(new Date());
        int result = cascadeStrategyMapper.updateByPrimaryKeySelective(cascadeStrategy);
        if (result == 1) {
            Integer status = cascadeStrategy.getStatus();
            String newPlatformId = cascadeStrategy.getPlatformId();
            String uid = cascadeStrategy.getUid();
            // 启用状态
            if (Global.OK.getCode().equals(status + "")) {
                this.deleteStrategyByPid(oldPlatformId,uid);
                if (StringUtils.isNotEmpty(newPlatformId)) {
                    Example example = new Example(CascadeStrategyDetail.class);
                    example.createCriteria().andEqualTo("puid",uid);
                    List<CascadeStrategyDetail> detailList = cascadeStrategyDetailService.findByExample(example);
                    if (CollectionUtils.isEmpty(detailList)) {
                        logger.info("策略详情为空！");
                        return true;
                    }
                    List<Map<String,Object>> strategy = this.convertStrategyDetail(detailList);
                    String kafka = cascadeStrategy.getKafka();
                    Map kafkaMap = this.encodeKafkaInfo(kafka);
                    this.reportStrategy(newPlatformId,uid,strategy,kafkaMap);
                }
            } else {
                this.deleteStrategyByPid(newPlatformId,uid);
            }
        }
        return result == 1;
    }

    @Override
    public Boolean deleteStrategy(String ids) {
        String[] strategyIds = ids.split(",");
        for (String strategyId : strategyIds) {
            CascadeStrategy cascadeStrategy = cascadeStrategyMapper.selectByPrimaryKey(Integer.valueOf(strategyId));
            if (cascadeStrategy != null) {
                String uid = cascadeStrategy.getUid();
                String platformId = cascadeStrategy.getPlatformId();
                this.deleteStrategyByPid(platformId,uid);

                Example example = new Example(CascadeStrategyDetail.class);
                example.createCriteria().andEqualTo("puid",uid);
                List<CascadeStrategyDetail> detailList = cascadeStrategyDetailService.findByExample(example);
                if (CollectionUtils.isNotEmpty(detailList)) {
                    for (CascadeStrategyDetail strategyDetail: detailList) {
                        cascadeStrategyDetailService.deleteById(strategyDetail.getId());
                    }
                }
            }
        }
        Integer result = cascadeStrategyMapper.deleteByIds(ids);
        return result >=1;
    }

    /**
     * 调用下级删除策略
     * @param platformId
     */
    private void deleteStrategyByPid(String platformId,String uid) {
        if (StringUtils.isNotEmpty(platformId)) {
            String[] platIds = platformId.split(",");
            for (String platId : platIds) {
                CascadePlatform cascadePlatform = cascadePlatformService.findById(Integer.valueOf(platId));
                String ip = cascadePlatform.getIp();
                Integer port = cascadePlatform.getPort();
                String url = "https://" + ip + ":" + port + "/cascade/policy/delete";
                logger.info("下发地址：" + url);
                Map<String, Object> param = new HashMap<>();
                param.put("id",cascadePlatform.getPlatformId());
                param.put("sid",uid);
                String requestParam = JSON.toJSONString(param);
                Map<String, String> headers = generateHeaders();
                try {
                    String response = HTTPUtil.POST(url, headers, requestParam);
                    logger.info("策略删除返回结果：" + LogForgingUtil.validLog(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 调用下级下发策略
     * @param platformId
     * @param strategy
     * @param kafka
     */
    private void reportStrategy(String platformId,String uid,List<Map<String,Object>> strategy,Map kafka) {
        if (StringUtils.isNotEmpty(platformId)) {
            String[] platIds = platformId.split(",");
            for (String platId : platIds) {
                CascadePlatform cascadePlatform = cascadePlatformService.findById(Integer.valueOf(platId));
                String ip = cascadePlatform.getIp();
                Integer port = cascadePlatform.getPort();
                String url = "https://" + ip + ":" + port + "/cascade/policy/report";
                logger.info("下发地址：" + url);
                Map<String, Object> param = new HashMap<>();
                param.put("id",cascadePlatform.getPlatformId());
                param.put("sid",uid);
                param.put("strategy",strategy);
                param.put("kafka",kafka);
                String requestParam = JSON.toJSONString(param);
                Map<String, String> headers = generateHeaders();
                try {
                    String response = HTTPUtil.POST(url, headers, requestParam);
                    logger.info("策略下发返回结果：" + LogForgingUtil.validLog(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 转换策略详情格式
     * @param detailList
     * @return
     */
    private List<Map<String,Object>> convertStrategyDetail(List<CascadeStrategyDetail> detailList) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (CascadeStrategyDetail cascadeStrategyDetail : detailList) {
                Map<String,Object> map = new HashMap<>();
                map.put("type",cascadeStrategyDetail.getType());
                map.put("number",cascadeStrategyDetail.getNumber());
                if (cascadeStrategyDetail.getOpCode() != null) {
                    map.put("op_code",cascadeStrategyDetail.getOpCode());
                }
                if (cascadeStrategyDetail.getKind() != null) {
                    map.put("kind",cascadeStrategyDetail.getKind());
                }
                if (cascadeStrategyDetail.getLevel() != null) {
                    map.put("level",cascadeStrategyDetail.getLevel());
                }
                map.put("startTime",cascadeStrategyDetail.getStartTime());
                map.put("endTime",cascadeStrategyDetail.getEndTime());
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 构造请求头
     * @return
     */
    private Map<String,String> generateHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * kafka信息加密
     * @param kafkaInfo
     * @return
     */
    private Map<String,Object> encodeKafkaInfo(String kafkaInfo) {
        if (StringUtils.isNotEmpty(kafkaInfo)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String,Object> kafkaMap = objectMapper.readValue(JsonSanitizer.sanitize(kafkaInfo),Map.class);
                String userName = (String) kafkaMap.get("userName");
                String passWord = (String) kafkaMap.get("passWord");
                kafkaMap.put("userName", AESUtil.Encrypt(userName,cKey));
                kafkaMap.put("passWord", AESUtil.Encrypt(passWord,cKey));
                kafkaMap.put("key",cKey);
                return kafkaMap;
            } catch (Exception e) {
                logger.info("加密kafka信息异常，加密的字符串为：" + LogForgingUtil.validLog(kafkaInfo));
            }
        }
        return null;
    }
}

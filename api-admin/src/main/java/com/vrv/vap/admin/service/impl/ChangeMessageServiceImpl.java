package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.constant.ChangeMessageConstants;
import com.vrv.vap.admin.model.CollectorIndex;
import com.vrv.vap.admin.service.ChangeMessageService;
import com.vrv.vap.admin.service.CollectorIndexService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2023/3/22
 * @description
 */
@Service
public class ChangeMessageServiceImpl implements ChangeMessageService {

    private static final Logger log = LoggerFactory.getLogger(ChangeMessageServiceImpl.class);

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Autowired
    private CollectorIndexService collectorIndexService;

    @Override
    public void initEventMessage() {
        Map<String,Object> result = new HashMap<>();
        result.put("time", System.currentTimeMillis());
        result.put("type", 1);
        List<CollectorIndex> indexList = collectorIndexService.findAll();
        if (CollectionUtils.isNotEmpty(indexList)) {
            List<Integer> indexIdList = indexList.stream().filter(p -> p.getSourceId() != null).map(CollectorIndex::getSourceId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(indexIdList)) {
                Set<Integer> sourceIdSet = new HashSet<>();
                for (Integer indexId : indexIdList) {
                    sourceIdSet.add(indexId);
                }
                List<Map> resultList = new ArrayList<>();
                for (Integer sourceId : sourceIdSet) {
                    Map map = new HashMap<>();
                    map.put("dataSourceId",sourceId);
                    map.put("dataType",1);
                    map.put("open_status",0);
                    map.put("data_status",0);
                    map.put("msg","未开启接收");
                    resultList.add(map);
                }
                result.put("data",resultList);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    kafkaSenderService.send(ChangeMessageConstants.EVENT_CHANGE_MESSAGE_TOPIC,null,objectMapper.writeValueAsString(result));
                    redisTemplate.opsForValue().set(ChangeMessageConstants.VAP_EVENT_MESSAGE,objectMapper.writeValueAsString(resultList));
                } catch (JsonProcessingException e) {
                    log.error("",e);
                }
            }
        }
    }
}

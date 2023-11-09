package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.mapper.CascadeStrategyReceiveMapper;
import com.vrv.vap.admin.model.CascadeStrategyReceive;
import com.vrv.vap.admin.service.CascadeStrategyReceiveService;
import com.vrv.vap.admin.vo.ReportStrategyVO;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2021/3/26
 * @description
 */
@Service
@Transactional
public class CascadeStrategyReceiveServiceImpl extends BaseServiceImpl<CascadeStrategyReceive> implements CascadeStrategyReceiveService {

    private static final Logger logger = LoggerFactory.getLogger(CascadeStrategyReceiveServiceImpl.class);

    @Resource
    CascadeStrategyReceiveMapper cascadeStrategyReceiveMapper;

    @Override
    public Boolean saveStrategy(ReportStrategyVO strategyVO) {
        String sid = strategyVO.getSid();
        List<Map<String,Object>> strategyList = strategyVO.getStrategy();
        Map kafka = strategyVO.getKafka();
        if (CollectionUtils.isNotEmpty(strategyList)) {
            for (Map<String,Object> strategyMap : strategyList) {
                String type = (String) strategyMap.get("type");
                Integer number = strategyMap.containsKey("number") ? (Integer) strategyMap.get("number") : null;
                Integer opCode = strategyMap.containsKey("op_code") ? (Integer) strategyMap.get("op_code") : null;
                Integer kind = strategyMap.containsKey("kind") ? (Integer) strategyMap.get("kind") : null;
                Integer level = strategyMap.containsKey("level") ? (Integer) strategyMap.get("level") : null;
                String startTime = (String) strategyMap.get("startTime");
                String endTime = (String) strategyMap.get("endTime");
                CascadeStrategyReceive cascadeStrategyReceive = new CascadeStrategyReceive();
                cascadeStrategyReceive.setPuid(sid);
                cascadeStrategyReceive.setType(type);
                cascadeStrategyReceive.setNumber(number);
                cascadeStrategyReceive.setOpCode(opCode);
                cascadeStrategyReceive.setKind(kind);
                cascadeStrategyReceive.setLevel(level);
                cascadeStrategyReceive.setStartTime(startTime);
                cascadeStrategyReceive.setEndTime(endTime);
                cascadeStrategyReceive.setKafka(JSON.toJSONString(kafka));
                cascadeStrategyReceiveMapper.insertSelective(cascadeStrategyReceive);
            }
        }
        return true;
    }
}

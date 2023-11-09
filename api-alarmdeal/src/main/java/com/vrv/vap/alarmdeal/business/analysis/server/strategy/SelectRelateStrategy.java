package com.vrv.vap.alarmdeal.business.analysis.server.strategy;

import java.util.HashMap;
import java.util.Map;

import com.vrv.vap.alarmdeal.business.analysis.vo.FieldInfoVO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.StrategyEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.jpa.json.JsonMapper;

@Component
public class SelectRelateStrategy implements ApplicationContextAware {

     private  static Map<Integer, ResponseStrategyService> strategyMap;

     @Autowired
     private RiskEventRuleService riskEventRuleService;

    @Override
    @Autowired
    public  void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
        Map<String, ResponseStrategyService> map=applicationContext.getBeansOfType(ResponseStrategyService.class);
        strategyMap = new HashMap<>();
        map.forEach((key,value) -> strategyMap.put(value.getIndex(),value));
    }

    public ResponseStrategyService getService(StrategyEnum strategyEnum){
        return strategyMap.get(strategyEnum.getIndex());
    }



    /**
     * 动态选择实现类
     * @param fieldInfo
     * @return
     */
    public ResponseStrategyService getResponseStrategyService(FieldInfoVO fieldInfo){
        if(!fieldInfo.getIsStart()){
            return getService(StrategyEnum.NGONESTRATEGY);
        }else{
            String strgtegyType=fieldInfo.getRulePolicy();
            if(!strgtegyType.isEmpty()){
                return getService(StrategyEnum.getStrategyEnumByName(strgtegyType));
            }
        }

        return  null;
    }

    /**
     * 插入异常时，重启响应通道
     * @param warnResultLogVO
     */
    public   void restartResponse(WarnResultLogTmpVO warnResultLogVO){
        String ruleId=warnResultLogVO.getRuleId();
        if(!ruleId.isEmpty()){
            RiskEventRule riskEventRule= riskEventRuleService.getOne(ruleId);
            FieldInfoVO fieldInfoVO = JsonMapper.fromJsonString(riskEventRule.getField_info(), FieldInfoVO.class);
            ResponseStrategyService responseStrategyService=getResponseStrategyService(fieldInfoVO);
            responseStrategyService.clearCache(warnResultLogVO);
        }
    }
    public void restartStrategyBatch(Map<String,WarnResultLogTmpVO> warnResultLogTmpVOMap){
        for(Map.Entry<String,WarnResultLogTmpVO> entry :warnResultLogTmpVOMap.entrySet()){
            restartResponse(entry.getValue());
        }

    }




}

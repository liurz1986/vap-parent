package com.vrv.vap.alarmdeal.business.analysis.server.strategy.impl;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.StrategyEnum;
import com.vrv.vap.alarmdeal.business.analysis.server.strategy.BaseStrategyServcie;
import com.vrv.vap.alarmdeal.business.analysis.server.strategy.ResponseStrategyService;
import org.springframework.stereotype.Service;

@Service
public class AlarmOneToOneStrategyServiceServiceImpl extends BaseStrategyServcie implements ResponseStrategyService {


    @Override
    public int getIndex(){
        return StrategyEnum.NGONESTRATEGY.getIndex();
    }


    @Override
    public Boolean restartStrategy(String id){
        return null;
    }

    @Override
    public Boolean restartStrategy(WarnResultLogTmpVO warnResultLogTmpVO){
        return null;
    }


}

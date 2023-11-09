package com.vrv.vap.alarmdeal.business.analysis.server.strategy.impl;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealTypeEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.StrategyEnum;
import com.vrv.vap.alarmdeal.business.analysis.server.strategy.BaseStrategyServcie;
import com.vrv.vap.alarmdeal.business.analysis.server.strategy.ResponseStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmStatusStrategyServiceServiceImpl extends BaseStrategyServcie implements ResponseStrategyService {


    @Autowired
    private WarnResultForESService warnResultForESService;

    @Override
    public int getIndex(){
        return StrategyEnum.STATUSSTRATEGY.getIndex();
    }

    /**
     * 重启响应通道
     */
    @Override
    public Boolean restartStrategy(String id){

        WarnResultLogTmpVO warnResult = warnResultForESService.getAlarmById(id);
        return restartStrategy(warnResult);
    }

    @Override
    public Boolean restartStrategy(WarnResultLogTmpVO warnResult) {
        int already_deal = AlarmDealTypeEnum.ALREADY_DEAL.getIndex();
        int error_report = AlarmDealTypeEnum.ERROR_REPORT.getIndex();
        if(warnResult.getRuleId().isEmpty()){
            return  false;
        }
        if(warnResult.getStatusEnum().intValue()==already_deal||warnResult.getStatusEnum().intValue()==error_report){
            clearCache(warnResult);
        }
        return  true;
    }


}

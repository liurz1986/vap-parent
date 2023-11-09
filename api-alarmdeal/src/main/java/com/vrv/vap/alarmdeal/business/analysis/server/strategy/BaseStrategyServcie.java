package com.vrv.vap.alarmdeal.business.analysis.server.strategy;


import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.AlarmInfoMergerHandler;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.RuleMergeHandler;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseStrategyServcie {


    @Autowired
    private AlarmInfoMergerHandler alarmInfoMergerHandler;

    /**
     * 重启响应通道
     */
    public void  clearCache(WarnResultLogTmpVO warnResultLogTmpVO){
        RuleMergeHandler ruleMergeHandler=alarmInfoMergerHandler.chooseRuleMergeHandler(warnResultLogTmpVO.getRuleCode());
        if(ruleMergeHandler!=null){
            ruleMergeHandler.clearCacheByWarnResultLogTmpVO(warnResultLogTmpVO);
        }
    }


}



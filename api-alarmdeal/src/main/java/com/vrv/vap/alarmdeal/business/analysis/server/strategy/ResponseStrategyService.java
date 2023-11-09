package com.vrv.vap.alarmdeal.business.analysis.server.strategy;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;

public interface ResponseStrategyService {


    public int getIndex();

    public Boolean restartStrategy(String id);


    public void  clearCache(WarnResultLogTmpVO warnResultLogTmpVO);

    public Boolean restartStrategy(WarnResultLogTmpVO warnResultLogTmpVO);

}

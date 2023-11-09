package com.vrv.vap.data.service;

import com.vrv.vap.data.vo.CommonRequest;

import java.util.Map;

public interface ScreenDataService {
    Map getNetflowInfo(CommonRequest query);

    Map getDataTrend(CommonRequest query);

    Map getDataTop(CommonRequest query);

    Map getDataProtocol(CommonRequest query);

    Map getDataNetflowNew(CommonRequest query);

    Map getDataAppVisitTop(CommonRequest query);

    Map getDataAppVisitSrcTop(CommonRequest query);

    Map getDataTerminalLoginTop(CommonRequest query);

    Map getDataTerminalLoginTrend(CommonRequest query);

    Map getDataUserLogTop(CommonRequest query);

    Map getDataVisitNum(CommonRequest query);

    Map getDataAttackInfo(CommonRequest query);

    Map getDataAttackIpTop(CommonRequest query);

    Map getDataAttackNew(CommonRequest query);

}

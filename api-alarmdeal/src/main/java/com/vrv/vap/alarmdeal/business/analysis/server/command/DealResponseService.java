package com.vrv.vap.alarmdeal.business.analysis.server.command;


import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseTypeVO;

import java.util.Set;

public interface DealResponseService {

    Set<ResponseTypeVO> getAssetResponsibility(String guid, String alarmName, String type);






}

package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;


import com.vrv.vap.alarmdeal.business.analysis.model.AlarmResponseLog;
import com.vrv.vap.alarmdeal.business.analysis.server.command.DealResponseService;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseTypeVO;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class DomainResponse implements DealResponseService {





    /**
     *资产责任人
     */
    @Override
    public Set<ResponseTypeVO> getAssetResponsibility(String dstAreaCode, String alarmName, String type){
        Set<ResponseTypeVO> set=new HashSet<>();
        return  set;
    }

    public AlarmResponseLog contructAlarmResponseLog(String alarmName, String type) {
        AlarmResponseLog alarmResponseLog=new AlarmResponseLog();
        alarmResponseLog.setAlarmName(alarmName);
        alarmResponseLog.setResponseResult("发送失败");
        alarmResponseLog.setResponseType(type);
        return alarmResponseLog;
    }

}

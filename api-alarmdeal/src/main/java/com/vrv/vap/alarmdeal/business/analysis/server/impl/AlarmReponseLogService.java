package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.AlarmResponseLog;
import com.vrv.vap.alarmdeal.business.analysis.repository.AlarmResponseLogRespository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmReponseLogService extends BaseServiceImpl<AlarmResponseLog,String> {

    @Autowired
    private AlarmResponseLogRespository alarmResponseLogRespository;

    @Override
    public AlarmResponseLogRespository getRepository(){
        return  alarmResponseLogRespository;
    }
}

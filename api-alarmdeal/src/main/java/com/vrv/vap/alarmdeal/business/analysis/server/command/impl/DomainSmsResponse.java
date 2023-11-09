package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;


import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DomainSmsResponse extends DomainResponse {

    public List<SmsVO> operation(SmsSendVO smsSendVO, String assetGuids, String alarmName){
        List<SmsVO> smsVOList=new ArrayList<>();
        return smsVOList;
    }
}

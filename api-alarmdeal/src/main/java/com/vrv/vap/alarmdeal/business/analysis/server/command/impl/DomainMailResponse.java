package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class DomainMailResponse extends DomainResponse {

    public List<MailVO> operation(MailSendVO mailSendVO, String assetGuids, String alarmName){
        List<MailVO> mailVOList=new ArrayList<>();
        return mailVOList;
    }
}

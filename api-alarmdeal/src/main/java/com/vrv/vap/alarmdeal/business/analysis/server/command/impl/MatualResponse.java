package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatualResponse {

    public List<SmsVO> operationSms(SmsSendVO smsSendVO,String alarmName){
        List<SmsVO> smsVOList=new ArrayList<>();
        SmsVO smsVO=new SmsVO();
        smsVO.setContent(smsSendVO.getContent());
        smsVO.setRecipient(smsSendVO.getRecipient().get("value").toString());
        smsVO.setAlarmName(alarmName);
        smsVOList.add(smsVO);

        return smsVOList;
    }


    public List<MailVO>  operationMail(MailSendVO mailSendVO,String alarmName){
          List<MailVO> mailVOList=new ArrayList<>();
          MailVO mailVO=new MailVO();
          mailVO.setContent(mailSendVO.getContent());
          mailVO.setSendTo(mailSendVO.getSendTo().get("value").toString());
          mailVO.setTitle(mailSendVO.getTitle());
          mailVO.setAlarmName(alarmName);
          mailVOList.add(mailVO);
          return mailVOList;
    }

}

package com.vrv.vap.alarmdeal.business.analysis.server.command;

import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.AssetMailResponse;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.AssetSmsResponse;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.MatualResponse;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.SelectedResponse;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmNotice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseContext {

    @Autowired
    private AssetMailResponse assetMailResponse;

    @Autowired
    private AssetSmsResponse assetSmsResponse;

    @Autowired
    private MatualResponse matualResponse;

    @Autowired
    private SelectedResponse selectedResponse;



    public List<SmsVO> smsVoTransform(AlarmNotice alarmNotice, List<SmsSendVO> smsSendVOList) {
        List<SmsVO> smsVOList=new ArrayList<>();
        String alarmName=alarmNotice.getName();
        for(SmsSendVO smsSendVo : smsSendVOList){
            List<SmsVO> list=new ArrayList<>();
            String type=smsSendVo.getRecipient().get("type").toString();
            if(SmsSendVO.dASSET.equals(type)){
                list=assetSmsResponse.operation(smsSendVo,alarmNotice.getAssetGuids(),alarmName);
            }else if(SmsSendVO.MATUAL.equals(type)){
                list=matualResponse.operationSms(smsSendVo,alarmName);
            }else if(SmsSendVO.SELECTED.equals(type)){
                list= selectedResponse.operationSms(smsSendVo,alarmName);
            }
            smsVOList.addAll(list);
        }
        return smsVOList;
    }

    public List<MailVO> mailVoTransform(AlarmNotice alarmNotice, List<MailSendVO> mailSendVOList) {
        List<MailVO> mailVOList=new ArrayList<>();
        String alarmName=alarmNotice.getName();
        for(MailSendVO mailSendVO : mailSendVOList){
            List<MailVO> list=new ArrayList<>();
            String type=mailSendVO.getSendTo().get("type").toString();
            if(SmsSendVO.dASSET.equals(type)){
                list=assetMailResponse.operation(mailSendVO,alarmNotice.getAssetGuids(),alarmName);
            }else if(SmsSendVO.MATUAL.equals(type)){
                list=matualResponse.operationMail(mailSendVO,alarmName);
            }else if(SmsSendVO.SELECTED.equals(type)){
                list= selectedResponse.operationMail(mailSendVO,alarmName);;
            }else if(SmsSendVO.dDOMAIN.equals(type)){
                //todo
            }
            mailVOList.addAll(list);
        }
        return mailVOList;
    }


}

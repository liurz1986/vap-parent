package com.vrv.vap.alarmdeal.business.analysis.server.command;

import com.vrv.vap.alarmdeal.business.analysis.vo.SoarVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import com.vrv.vap.alarmdeal.frameworks.contract.syslog.SysLogVO;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.MailCommandServiceImpl;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.SmsCommandServiceImpl;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.SoarCommandServiceImpl;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.SoarRelateCommandServiceImpl;
import com.vrv.vap.alarmdeal.business.analysis.server.command.impl.SysLogCommandServiceImpl;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmNotice;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.BlockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseCommandInvoker {
    @Autowired
    private SmsCommandServiceImpl smsCommandService;

    @Autowired
    private  MailCommandServiceImpl mailCommandService;

    @Autowired
    private SysLogCommandServiceImpl sysLogCommandService;
    @Autowired
    private SoarCommandServiceImpl soarCommandServiceImpl;
    @Autowired
    private SoarRelateCommandServiceImpl soarRelateCommandService;

    @Autowired
    private  ResponseContext responseContext;

   public void executeResponseCommand(AlarmNotice alarmNotice){
     if(AlarmNotice.OPEN.equals(alarmNotice.getCkEmail())){
           List<MailSendVO> mailSendVOList=alarmNotice.getMailSendVO();
           List<MailVO> mailVOList=responseContext.mailVoTransform(alarmNotice, mailSendVOList);
           mailCommandService.executeResponseCommond(mailVOList);
       }
       if(AlarmNotice.OPEN.equals(alarmNotice.getCkSms())){
           List<SmsSendVO> smsSendVOList =alarmNotice.getSmsSendVO();
           List<SmsVO> smsVOList= responseContext.smsVoTransform(alarmNotice, smsSendVOList);
           smsCommandService.executeResponseCommond(smsVOList);
       }
       if(AlarmNotice.OPEN.equals(alarmNotice.getCkSyslog())){
           List<SysLogVO> sysLogVOList=alarmNotice.getSysLogVOList();
           sysLogCommandService.executeResponseCommond(sysLogVOList);
       }
       if(AlarmNotice.OPEN.equals(alarmNotice.getCkBlockInfo())){
           BlockVO blockVO = alarmNotice.getBlockVO();
           List<BlockVO> blockVOs = new ArrayList<>();
           blockVOs.add(blockVO);
           soarCommandServiceImpl.executeResponseCommond(blockVOs);
       }
       
       if(AlarmNotice.OPEN.equals(alarmNotice.getCkSoarInfo())){
            SoarVO soarVO = alarmNotice.getSoarVO();
           List<SoarVO> soarVOs = new ArrayList<>();
           soarVOs.add(soarVO);
           soarRelateCommandService.executeResponseCommond(soarVOs);
       }
       
       
   }




}

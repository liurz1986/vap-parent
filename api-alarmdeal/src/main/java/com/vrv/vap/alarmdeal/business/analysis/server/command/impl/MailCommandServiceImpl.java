package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.model.AlarmResponseLog;
import com.vrv.vap.alarmdeal.business.analysis.server.command.DealCommandService;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.AlarmReponseLogService;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseCommonVo;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.ResultData;
import com.vrv.vap.alarmdeal.frameworks.feign.ServerSystemFegin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MailCommandServiceImpl implements DealCommandService<MailVO> {

    private static Logger logger = LoggerFactory.getLogger(SmsCommandServiceImpl.class);

    @Autowired
    private ServerSystemFegin serverSysFeign;

    @Autowired
    private AlarmReponseLogService alarmReponseLogService;

    @Override
    public void executeResponseCommond(List<MailVO> mailVOList){
        sendMail(mailVOList);
    }

    /**
     * 邮件通知
     */
    private void sendMail(List<MailVO> mailVOList){
        for(MailVO mailVO :mailVOList){
            AlarmResponseLog alarmResponseLog=new AlarmResponseLog();
            alarmResponseLog.setResponseType(ResponseCommonVo.mailType);
            alarmResponseLog.setAlarmName(mailVO.getAlarmName());
            alarmResponseLog.setGuid(UUID.randomUUID().toString());
            try {
                ResultData<Boolean> resultData= serverSysFeign.sendSimpleEmail(mailVO.toMessage());
                alarmResponseLog.setResponseResult(JSON.toJSONString(resultData));
                logger.info("邮件发送结果: "+ JSON.toJSONString(resultData)+","+mailVO.getContent());
            } catch (Exception e) {
                alarmResponseLog.setResponseReason("feign接口调用异常");
                alarmResponseLog.setResponseResult(e.getMessage());
                logger.error("邮件发送失败");
            }finally {
                alarmReponseLogService.save(alarmResponseLog);
            }
        }
    }

}

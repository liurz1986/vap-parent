package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.model.AlarmResponseLog;
import com.vrv.vap.alarmdeal.business.analysis.server.command.DealCommandService;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.AlarmReponseLogService;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseCommonVo;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SmsCommandServiceImpl  implements DealCommandService<SmsVO> {

    private static Logger logger = LoggerFactory.getLogger(SmsCommandServiceImpl.class);

    @Autowired
    private AdminFeign adminFeign;
    @Autowired
    private AlarmReponseLogService alarmReponseLogService;


    @Override
    public void executeResponseCommond(List<SmsVO> smsVOList){
        sendMessage(smsVOList);
    }

    /**
     * 短信通知
     */
    private void sendMessage(List<SmsVO> smsVOList){
        for(SmsVO smsVO :smsVOList){
            AlarmResponseLog alarmResponseLog=new AlarmResponseLog();
            alarmResponseLog.setResponseType(ResponseCommonVo.smsType);
            alarmResponseLog.setGuid(UUID.randomUUID().toString());
            alarmResponseLog.setAlarmName(smsVO.getAlarmName());
            try {
                logger.info("短信内容："+JSON.toJSONString(smsVO.toMessage()));
                Map<String,Object> result= adminFeign.shortMessage(smsVO.toMessage());
                alarmResponseLog.setResponseResult(JSON.toJSONString(result));
                logger.info("短信发送结果: "+ JSON.toJSONString(result)+","+smsVO.getContent());
            } catch (Exception e) {
                alarmResponseLog.setResponseReason("feign接口调用异常");
                alarmResponseLog.setResponseResult(e.getMessage());
                logger.info("短信发送失败",e);
            }finally {
                alarmReponseLogService.save(alarmResponseLog);
            }
        }
    }

}

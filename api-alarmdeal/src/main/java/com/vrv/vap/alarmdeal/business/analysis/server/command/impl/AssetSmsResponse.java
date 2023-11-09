package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.AlarmResponseLog;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseCommonVo;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseTypeVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.AlarmReponseLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AssetSmsResponse  extends AssetResponse {


    @Autowired
    private AlarmReponseLogService alarmReponseLogService;

    public List<SmsVO> operation(SmsSendVO smsSendVO, String assetGuids, String alarmName){
        List<SmsVO> smsVOList=new ArrayList<>();
        Set<ResponseTypeVO> userSet=getAssetResponsibility(assetGuids,alarmName, ResponseCommonVo.smsType);
        for(ResponseTypeVO responseTypeVO :userSet){
            if (StringUtils.isEmpty(responseTypeVO.getPhone())) {
                AlarmResponseLog alarmResponseLog = contructAlarmResponseLog(alarmName, ResponseCommonVo.smsType);
                alarmResponseLog.setGuid(UUID.randomUUID().toString());
                alarmResponseLog.setResponseReason("未匹配的资产责任人手机号");
                alarmReponseLogService.save(alarmResponseLog);
                continue;
            }
            SmsVO smsVO=new SmsVO();
            smsVO.setContent(smsSendVO.getContent());
            smsVO.setRecipient(responseTypeVO.getPhone());
            smsVO.setAlarmName(alarmName);
            smsVOList.add(smsVO);
        }
        return smsVOList;
    }
}

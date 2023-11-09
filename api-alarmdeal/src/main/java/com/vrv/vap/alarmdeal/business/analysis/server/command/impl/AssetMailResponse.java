package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.vrv.vap.alarmdeal.business.analysis.model.AlarmResponseLog;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseCommonVo;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseTypeVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailSendVO;
import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.AlarmReponseLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AssetMailResponse extends AssetResponse {


    @Autowired
    private AlarmReponseLogService alarmReponseLogService;

    public List<MailVO> operation(MailSendVO mailSendVO, String assetGuids, String alarmName){
        List<MailVO> mailVOList=new ArrayList<>();
        Set<ResponseTypeVO> userset=getAssetResponsibility(assetGuids,alarmName, ResponseCommonVo.mailType);
        for(ResponseTypeVO responseTypeVO :userset){
            if (StringUtils.isEmpty(responseTypeVO.getEmail())) {
                AlarmResponseLog alarmResponseLog = contructAlarmResponseLog(alarmName, ResponseCommonVo.mailType);
                alarmResponseLog.setGuid(UUID.randomUUID().toString());
                alarmResponseLog.setResponseReason("未匹配的资产责任人邮箱");
                alarmReponseLogService.save(alarmResponseLog);
                continue;
            }
            MailVO mailVO=new MailVO();
            mailVO.setContent(mailSendVO.getContent());
            mailVO.setSendTo(responseTypeVO.getEmail());
            mailVO.setTitle(mailSendVO.getTitle());
            mailVO.setAlarmName(alarmName);
            mailVOList.add(mailVO);
        }
        return mailVOList;
    }
}

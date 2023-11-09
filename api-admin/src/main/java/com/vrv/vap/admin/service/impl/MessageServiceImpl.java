package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.mapper.MessageMapper;
import com.vrv.vap.admin.model.Message;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.service.MessageService;
import com.vrv.vap.admin.service.SendMailService;
import com.vrv.vap.admin.service.UserService;
import com.vrv.vap.admin.vo.MailVO;
import com.vrv.vap.admin.vo.MessageVo;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by CodeGenerator on 2018/05/28.
 */
@Service
@Transactional
public class MessageServiceImpl extends BaseServiceImpl<Message> implements MessageService {
    private static Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    @Resource
    private MessageMapper messageMapper;

    @Autowired
    private SendMailService sendMailService;

    @Value("${pushUrl:https://127.0.0.1:8780/push/user}")
    private String pushUrl;

    @Autowired
    private UserService userService;

    //告警级别-低
    private static final Integer ALARM_GRADE_LOW = 1;

    @Override
    public int markReads(String ids) {
        return  messageMapper.markReads(ids.split(","));
    }

    @Override
    public int pushMessage(MessageVo messageVo) {
        int sendflag = 0;
        try {
            if(StringUtils.isEmpty(messageVo.getSendType()) || messageVo.getSendType().equals("1")){
                pushInfo(messageVo);
            }
        }catch (Exception ex){
            logger.error("推送发送失败："+ex);
            sendflag++;
        }
        try {
            if(StringUtils.isEmpty(messageVo.getSendType()) || messageVo.getSendType().equals("2")) {
                MailVO mailVO = new MailVO();
                mailVO.setContent(messageVo.getContent());
                mailVO.setTitle(messageVo.getTitle());
                if (StringUtils.isNotEmpty(messageVo.getSendEmailTo())) {
                    mailVO.setSendTo(messageVo.getSendEmailTo());
                } else {
                    User user = userService.findById(messageVo.getUserId());
                    if (user != null && StringUtils.isNotEmpty(user.getEmail())) {
                        mailVO.setSendTo(user.getEmail());
                    }
                }
                logger.info("邮件发送信息：" + JSON.toJSONString(mailVO));
                sendMailService.sendSimpleEmail(mailVO);
            }
        }catch (Exception ex){
            logger.error("发送失败："+ex);
            sendflag++;
        }
        return sendflag;
    }


    private void pushInfo(MessageVo messageVo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("content",messageVo.getContent());
        paramMap.put("title",messageVo.getTitle());
        paramMap.put("url",messageVo.getUrl());
        paramMap.put("userId",messageVo.getUserId());
        paramMap.put("alarmGrade",messageVo.getAlarmGrade() == null ? ALARM_GRADE_LOW : messageVo.getAlarmGrade());
        String requestParam = JSON.toJSONString(paramMap);
        logger.info("pushParam:" + requestParam);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        try {
            logger.info("pushUrl:" + pushUrl);
            HTTPUtil.PUT(pushUrl, headers, requestParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

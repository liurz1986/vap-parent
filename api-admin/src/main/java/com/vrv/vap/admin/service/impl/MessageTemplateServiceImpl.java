package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.mapper.MessageTemplateMapper;
import com.vrv.vap.admin.model.MessageTemplate;
import com.vrv.vap.admin.service.MessageTemplateService;
import com.vrv.vap.admin.vo.MessageTemplateQuery;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class MessageTemplateServiceImpl extends BaseServiceImpl<MessageTemplate> implements MessageTemplateService{
    @Resource
    private MessageTemplateMapper messageTemplateMapper;


    @Override
    public List<MessageTemplate> queryMessageTemplate(MessageTemplateQuery messageTemplateQueryVo) {
        MessageTemplateQuery messageTemplateQuery = new MessageTemplateQuery();
        BeanUtils.copyProperties(messageTemplateQueryVo, messageTemplateQuery);
        return messageTemplateMapper.queryMessageTemplate(messageTemplateQuery);
    }

    @Override
    public long queryMessageTemplateCount(MessageTemplateQuery messageTemplateQueryVo) {
        MessageTemplateQuery messageTemplateQuery = new MessageTemplateQuery();
        BeanUtils.copyProperties(messageTemplateQueryVo, messageTemplateQuery);
        return messageTemplateMapper.queryMessageTemplateCount(messageTemplateQuery);
    }

    @Override
    public int deleteMessageTemplateByGuid(String guid) {
        return messageTemplateMapper.deleteMessageTemplateByGuid(guid);
    }

    @Override
    public int updateMessageTemplate(MessageTemplate messageTemplate) {
        MessageTemplate template = new MessageTemplate();
        BeanUtils.copyProperties(messageTemplate, template);
        return messageTemplateMapper.updateMessageTemplate(template);
    }

    @Override
    public  List<MessageTemplate> findAll() {
        return messageTemplateMapper.findAll();
    }

    @Override
    public int saveMssageTemplate(MessageTemplate messageTemplate) {
        MessageTemplate template = new MessageTemplate();
        BeanUtils.copyProperties(messageTemplate, template);
        return messageTemplateMapper.saveMssageTemplate(template);
    }


}

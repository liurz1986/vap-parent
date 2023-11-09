package com.vrv.vap.admin.service;


import com.vrv.vap.admin.model.MessageTemplate;
import com.vrv.vap.admin.vo.MessageTemplateQuery;
import com.vrv.vap.base.BaseService;

import java.util.List;

public interface MessageTemplateService extends BaseService<MessageTemplate> {
    List<MessageTemplate> queryMessageTemplate(MessageTemplateQuery messageTemplateQuery);
    long queryMessageTemplateCount(MessageTemplateQuery messageTemplateQuery);
    int deleteMessageTemplateByGuid(String guid);
    int updateMessageTemplate(MessageTemplate messageTemplate);
    List<MessageTemplate>  findAll();
    int saveMssageTemplate(MessageTemplate messageTemplate);

}

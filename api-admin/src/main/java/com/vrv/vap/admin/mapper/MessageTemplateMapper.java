package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.MessageTemplate;
import com.vrv.vap.admin.vo.MessageTemplateQuery;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {
    List<MessageTemplate> queryMessageTemplate(@Param("messageTemplateQuery") MessageTemplateQuery messageTemplateQuery);
    long  queryMessageTemplateCount(@Param("messageTemplateQuery")MessageTemplateQuery messageTemplateQuery);
    int deleteMessageTemplateByGuid(@Param("guid") String guid);
    int updateMessageTemplate(@Param("messageTemplate")MessageTemplate messageTemplate);
    List<MessageTemplate> findAll();
    int saveMssageTemplate(@Param("messageTemplate")MessageTemplate messageTemplate);
}

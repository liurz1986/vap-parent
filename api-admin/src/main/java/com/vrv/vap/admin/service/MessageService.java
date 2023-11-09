package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.Message;
import com.vrv.vap.admin.vo.MessageVo;
import com.vrv.vap.base.BaseService;

/**
 * Created by CodeGenerator on 2018/05/28.
 */
public interface MessageService extends BaseService<Message> {

    int markReads(String ids);

    int pushMessage(MessageVo messageVo);
}

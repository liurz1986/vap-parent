package com.vrv.vap.server.push.service;

import com.vrv.vap.server.push.model.Message;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.server.push.vo.MessageVO;

import java.util.List;

/**
 * Created by CodeGenerator on 2018/05/28.
 */
public interface MessageService extends BaseService<Message>{

    int insertByRole(MessageVO group);

}

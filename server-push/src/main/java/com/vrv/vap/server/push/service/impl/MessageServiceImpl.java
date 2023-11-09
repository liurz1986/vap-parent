package com.vrv.vap.server.push.service.impl;

import com.vrv.vap.server.push.mapper.MessageMapper;
import com.vrv.vap.server.push.model.Message;
import com.vrv.vap.server.push.service.MessageService;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.server.push.vo.MessageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by CodeGenerator on 2018/05/28.
 */
@Service
@Transactional
public class MessageServiceImpl extends BaseServiceImpl<Message> implements MessageService {
    @Resource
    private MessageMapper messageMapper;

    @Override
    public int insertByRole(MessageVO group) {
        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(group, messageVO);
        return  messageMapper.insertByRole(messageVO);
    }

}

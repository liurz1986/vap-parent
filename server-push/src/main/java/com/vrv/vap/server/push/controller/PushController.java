package com.vrv.vap.server.push.controller;

import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.server.push.enums.ErrorCode;
import com.vrv.vap.server.push.service.MessageService;
import com.vrv.vap.server.push.vo.MessageVO;
import com.vrv.vap.server.push.vo.Tip;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import com.vrv.vap.server.push.model.Message;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@ApiModel(value = "消息推送服务")
@RequestMapping(path = "/push")
@RestController
public class PushController extends ApiController {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private MessageService messageService;


    @ApiOperation(value = "向全站所有在线用户推送消息")
    @PutMapping("/tip")
    public Result pushTip(@RequestBody Tip tip) {
        tip.setSendtime(new Date());
        simpMessageSendingOperations.convertAndSend("/tip", tip);
        return Global.OK;
    }

    @ApiOperation(value = "向一个用户组推送消息")
    @PutMapping("/group")
    public Result pushAnnounce(@RequestBody MessageVO group) {
        group.setSource((byte) 1);
        group.setStatus((byte) 0);
        group.setSendtime(new Date());
        group.setReadtime(null);
        String batchId = UUID.randomUUID().toString();
        group.setSendbatch(batchId);
        if (group.getRoleId() == 0) {
            return this.result(ErrorCode.MUST_HAVE_ROLEID);
        }
        int result = messageService.insertByRole(group);
        if (result <= 0) {
            return this.result(ErrorCode.EMPTY_USERS);
        }
        List<Message> messages = messageService.findByProperty(Message.class, "sendbatch", batchId);
        System.out.println(messages.size());
        for (Message message : messages) {
            simpMessageSendingOperations.convertAndSendToUser(String.valueOf(message.getUserId()), "/message", message);
        }
        return Global.OK;
    }


    @ApiOperation( value = "向一个指定用户推送消息" )
    @PutMapping("/user")
    public Result pushMessage(@RequestBody Message message) {
        message.setSource((byte) 1);
        message.setStatus((byte) 0);
        message.setSendtime(new Date());
        message.setReadtime(null);
        int result = messageService.save(message);
        if (result == 1) {
            simpMessageSendingOperations.convertAndSendToUser(String.valueOf(message.getUserId()), "/message", message);
            return Global.OK;
        }
        return Global.ERROR;
    }
}

package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.Message;
import com.vrv.vap.admin.service.MessageService;
import com.vrv.vap.admin.vo.MessageQuery;
import com.vrv.vap.admin.vo.MessageVo;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(path = "/message")
public class MessageController  extends ApiController {

    @Autowired
    private MessageService messageService;



    /**
     * 查询消息（分页）
     * */
    @ApiOperation(value = "查询消息（分页）")
    @PostMapping
    public VList<Message> queryMessage(HttpServletRequest request, @RequestBody MessageQuery messageQuery){
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        messageQuery.setUserId(user.getId());
        messageQuery.setOrder_("id");
        messageQuery.setBy_("desc");
        Example example   = this.pageQuery(messageQuery,Message.class);
        return this.vList(messageService.findByExample(example));
    }

    /**
     * 标记已读
     * */
    @ApiOperation(value = "标记已读")
    @PatchMapping
    public Result updateMessage(@RequestBody DeleteQuery ids){
        messageService.markReads(ids.getIds());
        return Global.OK;
    }

    /**
     * 删除消息
     * */
    @ApiOperation(value = "删除消息")
    @DeleteMapping
    public Result deleteMessage(@RequestBody DeleteQuery ids){
        messageService.deleteByIds(ids.getIds());
        return Global.OK;
    }


    //消息推送及邮件发送
    @ApiOperation(value = "发送报警及邮件接口")
    @PutMapping("/push")
    public Result pushMessage(@RequestBody MessageVo messageVo){
        int sendFlag = messageService.pushMessage(messageVo);
        if(sendFlag>0){
            Map<String,Integer> statusMap = new HashMap<>();
            statusMap.put("status",sendFlag);
            return this.vData(statusMap);
        }
        return Global.OK;
    }
}

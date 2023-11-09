package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.model.MessageTemplate;
import com.vrv.vap.admin.service.MessageTemplateService;
import com.vrv.vap.admin.vo.MessageTemplateQuery;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Api(value = "短信模板操作")
@RequestMapping(path = "/messageTemplate")
public class MessageTemplateController extends ApiController {
    @Autowired
    private MessageTemplateService messageTemplateService;

    @ApiOperation(value = "获取所有短信模板")
    @GetMapping
    @SysRequestLog(description = "获取所有短信模板", actionType = ActionType.SELECT)
    public Result getMessageList() {
        return this.vData(messageTemplateService.findAll());
    }

    /**
     *  查询短信模板
     * */
    @ApiOperation(value = "查询短信模板")
    @PostMapping
    @SysRequestLog(description = "查询短信模板", actionType = ActionType.SELECT)
    public VList<MessageTemplate> queryMessage( @RequestBody MessageTemplateQuery messageTemplateQuery){
        SyslogSenderUtils.sendSelectSyslog();
        List<MessageTemplate> messageTemplateList = messageTemplateService.queryMessageTemplate(messageTemplateQuery);
        long count = messageTemplateService.queryMessageTemplateCount(messageTemplateQuery);
        return this.vList(messageTemplateList,(int)count);
    }

    /**
     *  编辑短信模板
     * */
    @ApiOperation(value = "编辑短信模板")
    @PatchMapping
    @SysRequestLog(description = "编辑短信模板", actionType = ActionType.UPDATE)
    public Result updateMessageTemplate(@RequestBody MessageTemplate messageTemplate){
        MessageTemplateQuery messageTemplateQuery = new MessageTemplateQuery();
        messageTemplateQuery.setGuid(messageTemplate.getGuid());
        List<MessageTemplate> messageTemplate1List = messageTemplateService.queryMessageTemplate(messageTemplateQuery);
        MessageTemplate messageTemplateSrc = null;
        if (CollectionUtils.isNotEmpty(messageTemplate1List)) {
            messageTemplateSrc = messageTemplate1List.get(0);
        }
        int conut = messageTemplateService.updateMessageTemplate(messageTemplate);
        if (conut == 1) {
            SyslogSenderUtils.sendUpdateSyslog(messageTemplateSrc,messageTemplate,"编辑短信模板");
        }
        return this.result(conut>0);
    }

    /**
     *  删除短信模板
     * */
    @ApiOperation(value = "删除短信模板")
    @DeleteMapping ("/{guid}")
    @SysRequestLog(description = "删除短信模板", actionType = ActionType.DELETE)
    public Result deleteMessage(@PathVariable("guid") String guid ){
        MessageTemplateQuery messageTemplateQuery = new MessageTemplateQuery();
        messageTemplateQuery.setGuid(guid);
       List<MessageTemplate> list = messageTemplateService.queryMessageTemplate(messageTemplateQuery);
        if(CollectionUtils.isNotEmpty(list)){
            MessageTemplate messageTemplate = list.get(0);
            String num = messageTemplate.getNum();
            if(Integer.valueOf(num)<1000){
                return this.result(ErrorCode.MESSAGE_DELETE_WRONG);
            }
        }
       int count = messageTemplateService.deleteMessageTemplateByGuid(guid);
        if (count ==1) {
            list.forEach(messageTemp -> {
                SyslogSenderUtils.sendDeleteSyslog(messageTemp,"删除短信模板");
            });
        }
       return this.result(count>0);
    }

    /**
     *  新增短信模板
     * */
    @ApiOperation(value = "新增短信模板")
    @PutMapping
    @SysRequestLog(description = "新增短信模板", actionType = ActionType.ADD)
    public Result addMessage(@RequestBody MessageTemplate messageTemplate){
        List<MessageTemplate> messageTemplateList = messageTemplateService.findAll();
        Integer maxNum = 0;
        if(CollectionUtils.isNotEmpty(messageTemplateList)){
           maxNum = messageTemplateList .stream().
                    map(p->Integer.valueOf(p.getNum())).
                    max(Integer::compareTo).get();
        }
        if(maxNum>=1000){
            String num = String.valueOf(maxNum+1);
            messageTemplate.setNum(num);
        }
        else {
            messageTemplate.setNum(String.valueOf(1000));
        }
        messageTemplate.setGuid(Uuid.uuid());
        int count = messageTemplateService.saveMssageTemplate(messageTemplate);
        if (count == 1) {
            SyslogSenderUtils.sendAddSyslog(messageTemplate,"新增短信模板");
        }
        return this.result(count>0);
    }





}

package com.vrv.vap.alarmdeal.business.flow.core.listener;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.EmailService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.log.LoggerUtil;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 邮件监听器
 * 2023-1-3
 */
@Service("emailListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EmailListener implements TaskListener {

    private static LoggerUtil logger = LoggerUtil.getLogger(EmailListener.class);

    @Autowired
    private FlowService flowService;

    @Autowired
    private EmailService emailService;


    private  FixedValue emailKey; //邮件模板



    @Override
    public void notify(DelegateTask delegateTask){
        String eventName = delegateTask.getEventName();
        logger.info(eventName+":"+delegateTask+":"+delegateTask.getProcessInstanceId());
        switch (eventName) {
            case "create":
                emailSend(delegateTask);
                break;
            default:
                break;
        }

    }

    /**
     * 邮件发送处理
     * @param delegateTask
     */
    private void emailSend(DelegateTask delegateTask) {
        try{
            String emailMailTemplate=emailKey.getValue(delegateTask).toString();
            if(StringUtils.isBlank(emailMailTemplate)){
                logger.info("邮件模板为空，不发邮件!");
                return;
            }
            BusinessIntance instance=flowService.getFlowInstance(delegateTask.getExecutionId());
            Map<String, Object> argMap = getStringObjectMap(instance);
            // 获取任务节点处置人，作为邮件发送人
            Object userObj = flowService.getVariable(delegateTask.getId(),"nodeHandleUser");
            if(null == userObj){
                logger.info("当前任务节点没有处理人,不进行邮件发送处理");
                return;
            }
            Set<String> users =(Set<String>)userObj;
            if(users.size() <=0 ){
                logger.info("发送人为空，不发邮件!");
                return;
            }
            emailService.sendMessage(users,emailMailTemplate,argMap);
        }catch(Exception e){
            logger.error("邮件发送异常",e);
        }
    }


    /**
     * 邮件全部可以处理的内容
     * @param instance
     * @return
     */
    private Map<String, Object> getStringObjectMap(BusinessIntance instance) {
        Map<String, Object> argMap = JsonMapper.fromJsonString(instance.getBusiArgs(), Map.class);
        argMap.put("code", instance.getCode()); // 工单编号
        argMap.put("name", instance.getName()); // 工单名称
        argMap.put("deadlineDate", instance.getDeadlineDate()); // 逾期时间
        argMap.put("createUserName", instance.getCreateUserName()); // 创建人姓名
        argMap.put("createDate", getDataFormat(instance.getCreateDate())); // 创建时间
        String hostAddress = System.getenv("LOCAL_SERVER_IP");
        argMap.put("loginIp", hostAddress); // 当前登陆ip
        return argMap;
    }

    private String getDataFormat(Date createDate) {
        try{
            if(null == createDate){
                return "";
            }
            SimpleDateFormat formatTool = new SimpleDateFormat();
            formatTool.applyPattern("yyyy年M月d日h时m分s秒");
            return formatTool.format(createDate);
        }catch (Exception e){
           logger.error("时间转换异常",e);
           return "";
        }
    }



}

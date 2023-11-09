package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.core.service.SmsService;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.log.LoggerUtil;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service("smsListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SmsListener implements TaskListener {

    private static LoggerUtil logger = LoggerUtil.getLogger(SmsListener.class);

    @Autowired
    private FlowService flowService;

    @Autowired
    private SmsService smsService;


    private  FixedValue smsKey; //短信模板



    @Override
    public void notify(DelegateTask delegateTask){
        String eventName = delegateTask.getEventName();
        logger.info(eventName+":"+delegateTask+":"+delegateTask.getProcessInstanceId());
        switch (eventName) {
            case "create":
                excSms(delegateTask);
                break;
            default:
                break;
        }
    }

    private void excSms(DelegateTask delegateTask) {
        try{
            // 获取任务节点处置人，作为邮件发送人
            Object userObj = flowService.getVariable(delegateTask.getId(),"nodeHandleUser");
            if(null == userObj){
                logger.warn("当前任务节点没有处理人,不进行短信发送处理");
                return;
            }
            Set<String> users =(Set<String>)userObj;
            if(users.size() <=0 ){
                logger.warn("发送人为空，不发短信!");
                return;
            }
            Object object=smsKey.getValue(delegateTask);
            if(null == object){
                logger.warn("短信模板为空，不发短信!");
                return;
            }
            logger.info("短信模板: " +object.toString());
            String smsModel=object.toString();
            BusinessIntance intance=flowService.getFlowInstance(delegateTask.getExecutionId());
            Map<String,Object>  argMap=initArg(intance);
            String args= JSON.toJSONString(argMap);
            smsService.sendMessage(users,smsModel,args);
        }catch(Exception e){
             logger.error("发送短信异常",e);
        }
    }


    public  Map<String,Object> initArg(BusinessIntance instance){
        Map<String,Object> arg=new HashMap<String, Object>();
        try {
            logger.info("intance:"+JSON.toJSONString(instance));
            arg= JsonMapper.fromJsonString(instance.getBusiArgs(), arg.getClass());
            arg.put("name",instance.getName());
            logger.info("instance: "+JSON.toJSONString(arg));
        } catch (Exception e) {
            logger.error("格式错误", e);
        }
        logger.info("arg");
        return arg;
    }

}

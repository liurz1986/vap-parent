package com.vrv.vap.alarmdeal.business.flow.core.listener.customized;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model.EventAlarmTaskNodeMsg;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件处理流程结束监听处理
 *
 * 配置在最后一个节点的，delete状态下   第二个节点审核通过后执行的处理
 * 2022-11-23
 */
@Service("eventHandleEndListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EventHandleEndListener implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(EventHandleEndListener.class);
    @Autowired
    private FlowService flowService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;
    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        logger.info("事件处理流程定制结束监听器"+delegateTask.getName()+":"+delegateTask.getProcessInstanceId());
        switch (eventName) {
            case "delete":
                Object actionObj= runtimeService.getVariable(delegateTask.getExecutionId(), FlowConstant.ACTION);
                String action = actionObj==null?"":String.valueOf(actionObj);;
                if (StringUtils.isNotEmpty(action) && "通过".equals(action)){
                    User cuurentUser = flowService.getFlowCurrentuser(delegateTask.getProcessInstanceId());
                    logger.info("cuurentUser" + cuurentUser);
                    if (null == cuurentUser) {
                        logger.info("获取当前用户为空");
                    }
                    BusinessIntance instance = flowService.getFlowInstance(delegateTask.getExecutionId());
                    EventAlarmTaskNodeMsg msg = new EventAlarmTaskNodeMsg();
                    msg.setEventId(instance.getGuid()); // 事件id，传过来的
                    msg.setTaskName(delegateTask.getName());//流程节点名称、
                    msg.setDealedPersonId(cuurentUser.getId());// 实际处理人id
                    msg.setDealedPersonName(cuurentUser.getName());// 实际处理人名称
                    msg.setEventAlarmStatus(3);// 结束为3
                    alarmEventMsg(msg);
                }
                break;
            default:
                break;
        }
    }
    // 流程结束处理调用事件处理接口
    private void alarmEventMsg(EventAlarmTaskNodeMsg msg) {
        try {
            logger.warn("流程结束处理调用事件处理接口：" + JSON.toJSONString(msg));
            alarmEventManagementForESService.saveEventAlarmDealChange(msg);
            logger.info("流程结束处理调用事件处理接口成功");
        } catch (Exception e) {
            logger.error("流程结束处理调用事件处理接口异常", e);
        }
    }

    private List<String> listHandle(List<Integer> roleIds) {
        List<String> roles = new ArrayList<>();
        for(Integer roleId : roleIds){
            roles.add(roleId.toString());
        }
        return roles;
    }
}

package com.vrv.vap.alarmdeal.business.flow.core.listener.customized;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model.EventAlarmTaskNodeMsg;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskCandidate;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskCandidateService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.util.GwParamsUtil;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 事件处理流程定制节点过程监听器
 *
 * 配置在节点上
 * 2022-11-8
 */
@Service("eventHandleProcessListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EventHandleProcessListener implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(EventHandleProcessListener.class);
    @Autowired
    private FlowService flowService;
    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;
    @Autowired
    private BusinessTaskCandidateService businessTaskCandidateService;
    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        logger.info("事事件处理流程定制节点过程监听器"+delegateTask.getName()+":"+delegateTask.getProcessInstanceId());
        switch (eventName) {
            case "create":
                    BusinessIntance instance = flowService.getFlowInstance(delegateTask.getExecutionId());
                    sendMsgEventAlarm(delegateTask, instance);
                break;
            default:
                break;
        }
    }

    /**
     * 针对事件处理流程：流程节点创建时调用告警接口
     *  调用告警接口 2021-10-11   将审批节点消息和成因分析的值进行和并处理  2022-06-30
     * @param delegateTask
     * @param instance
     */
    private void sendMsgEventAlarm(DelegateTask delegateTask,BusinessIntance instance) {
        // 获取当前登录用户
        User cuurentUser = flowService.getFlowCurrentuser(delegateTask.getExecutionId());
        logger.info("cuurentUser"+cuurentUser);
        if (null == cuurentUser) {
            logger.info("获取当前用户为空");
            throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(), delegateTask.getName() + "获取当前用户为空！");
        }
        //修改 工单id不一定是事件id，是通过事件id+时间戳拼接而成的2022/09/07
        String eventId = GwParamsUtil.getEventIdByFlowId(instance.getGuid());
        EventAlarmTaskNodeMsg msg = new EventAlarmTaskNodeMsg();
        msg.setCauseAnalysis(getCauseAnalysis(instance.getBusiArgs()));
        msg.setEventId(eventId); // 事件id，传过来的
        msg.setTaskName(delegateTask.getName());//流程节点名称、
        msg.setDealedPersonId(cuurentUser.getId());// 实际处理人id
        msg.setDealedPersonName(cuurentUser.getName());// 实际处理人名称
        msg.setCanOperateUser(getTaskCandidateUser(delegateTask));// 处理人id，现有工单只存放了处理id，没有角色
        msg.setEventAlarmStatus(1);//告警事件状态 1 ：表示进行中 3 表示结束
        alarmEventMsg(msg);
    }

    private List<String> getTaskCandidateUser(DelegateTask delegateTask) {
        String taskId = delegateTask.getId();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("taskId",taskId));
        List<BusinessTaskCandidate> candidates = businessTaskCandidateService.findAll(conditions);
        if(CollectionUtils.isEmpty(candidates)){
            logger.error("事件处理流程sendMsgEventAlarm监听器中，当前任务处理人为null");
            return null;
        }
        List<String> candidateIds = candidates.stream().map(item -> item.getCandidate()).collect(Collectors.toList());
        return candidateIds;
    }

    private String getCauseAnalysis(String busiArgs) {
        if(StringUtils.isEmpty(busiArgs)){
            return null;
        }
        Map<String,Object> arg=new HashMap<String, Object>();
        arg= JsonMapper.fromJsonString(busiArgs, arg.getClass());
        Object zijReason = arg.get("zjgReason");   //成因分析
        if(null == zijReason){
            return null;
        }
        return String.valueOf(zijReason);
    }

    //todo 节点审批直接调用事件处理接口 2022/09/06
    private void alarmEventMsg(EventAlarmTaskNodeMsg msg) {
        try {
            logger.warn("节点创建直接调用事件处理接口：" + JSON.toJSONString(msg));
            alarmEventManagementForESService.saveEventAlarmDealChange(msg);
            logger.info("节点创建直接调用事件处理接口成功");
        } catch (Exception e) {
            logger.error("节点审批直接调用事件处理接口异常", e);
        }
    }
}

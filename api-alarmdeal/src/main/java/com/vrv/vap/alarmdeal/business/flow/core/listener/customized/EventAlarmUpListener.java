package com.vrv.vap.alarmdeal.business.flow.core.listener.customized;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.DisponseConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport.IUpReportEventService;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.util.GwParamsUtil;
import com.vrv.vap.jpa.json.JsonMapper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 事件上报、线索上报监听器
 *
 * 2022-11-8
 */
@Service("eventAlarmUpListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EventAlarmUpListener implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(EventAlarmUpListener.class);
    @Autowired
    private FlowService flowService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IUpReportCommonService upReportCommonService;

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        switch (eventName) {
            case "complete":
                    logger.info("事件上报、线索上报监听器开始" + delegateTask.getName() + ":" + delegateTask.getProcessInstanceId());
                    BusinessIntance instance = flowService.getFlowInstance(delegateTask.getExecutionId());
                    Object actionObj = runtimeService.getVariable(delegateTask.getExecutionId(), FlowConstant.ACTION);
                    String action = actionObj == null ? "" : String.valueOf(actionObj);
                    Map map = JsonMapper.fromJsonString(instance.getBusiArgs(), Map.class);
                    //共性校验：审核通过才会上报事件处置或者线所
                    if (!"通过".equals(action)) {
                        return;
                    }
                    try{
                        logger.warn("流程结束执行事件上报、线索上报");
                        upEventDisposeComplete( map, delegateTask.getExecutionId());
                    }catch (Exception e){
                        logger.error("----流程结束执行事件上报、线索上报异常", e);
                    }
                break;
            default:
                break;
        }

    }

    //上报事件处置完成 事件处置完成也就是需要上报线所，20230901上报线所进行调整：必须是造成了失泄密才会上报，没有造成是不用上报的
    private void upEventDisposeComplete( Map busiArgs, String executionId) {
        UpEventDTO eventDTO = new UpEventDTO();
        User currentUser = flowService.getFlowCurrentuser(executionId);
        eventDTO.setEventId(busiArgs.get("businessId") != null ? GwParamsUtil.getEventIdByFlowId(busiArgs.get("businessId").toString()) : "");
        logger.info("#####upEventDisposeComplete eventId={}", eventDTO.getEventId());
        if (currentUser != null) {
            eventDTO.setName(currentUser.getName());
            eventDTO.setDepartmentName(currentUser.getOrgName());
            eventDTO.setRoleId(currentUser.getRoleId());
        }
        eventDTO.setBusiArgs(busiArgs);
        eventDTO.setDisposeStatus(DisponseConstant.DISPONSE_COMPLETE);

        //1 上报事件处置
        eventDTO.setUpReportBeanName(IUpReportEventService.UpReportDispose_BEAN_NAME);
        upReportCommonService.upReportEvent(eventDTO);
        //失泄密评估是必填参数，可以不用判断
        String result_evaluation = busiArgs.get("result_evaluation").toString();
        logger.info("#######获取的失泄密评估值result_evaluation={}", result_evaluation);

        //线所定制校验：未造成失泄密是不用上报线所的！
        if(!"1".equals(result_evaluation)){
            return;
        }
        //2 上报线所
        eventDTO.setUpReportBeanName(IUpReportEventService.UpReportLinePost_BEAN_NAME);
        upReportCommonService.upReportEvent(eventDTO);
    }


}

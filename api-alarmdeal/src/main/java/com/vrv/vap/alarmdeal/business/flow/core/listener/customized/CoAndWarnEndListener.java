package com.vrv.vap.alarmdeal.business.flow.core.listener.customized;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.core.vo.FlowMessageVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.util.FlowQueUtil;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.jpa.json.JsonMapper;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 协办与预警定制流程监听器
 * 流程结束发消息，配置最后一个节点 delete状态下
 *
 * 2022-11-23
 */
@Service("coAndWarnEndListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CoAndWarnEndListener implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(CoAndWarnEndListener.class);
    @Autowired
    private FlowService flowService;
    @SneakyThrows
    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        switch (eventName) {
            case "delete":
                logger.info("协办与预警定制流程监听器，流程结束发消息"+delegateTask.getName()+":"+delegateTask.getProcessInstanceId());
                BusinessIntance instance = flowService.getFlowInstance(delegateTask.getExecutionId());
                User currentUser = flowService.getFlowCurrentuser(delegateTask.getExecutionId());
                String busiArgs = instance.getBusiArgs();
                FlowMessageVO message = new FlowMessageVO();
                message.setBusiArgs(busiArgs);
                message.setStatus(FlowMessageVO.END); // 流程结束
                message.setTicketName(instance.getProcessDefName());
                message.setInstanceId(instance.getGuid());
                message.setUser(currentUser);
                logger.warn(instance.getProcessDefName()+"流程结束，发消息给队列,消息数据："+JSON.toJSONString(message));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                FlowQueUtil.flowMessagePut(message);
                            }catch (Exception e){
                                logger.error("数据存入队列异常",e);
                            }
                        }
                    }).start();
                break;
            default:
                break;
        }
    }
}

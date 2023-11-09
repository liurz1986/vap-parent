package com.vrv.vap.alarmdeal.business.flow.core.listener;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonSyntaxException;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessIntanceService;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.jpa.log.LoggerUtil;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * 追加属性监听器
 *  business_intance表中busiArgs增加数据
 */
@Service("busiArgAppendListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BusiArgAppendListener implements TaskListener {

    private static LoggerUtil logger = LoggerUtil.getLogger(BusinessTaskListenerAbs.class);

    private FixedValue busiArg;

    @Autowired
    private FlowService flowService;

    @Autowired
    private BusinessIntanceService businessIntanceService;

    @Override
    public void notify(DelegateTask delegateTask){
        String eventName = delegateTask.getEventName();
        logger.info("eventName :"+eventName);
        switch (eventName) {
            case "complete":
                addBusArg(delegateTask,delegateTask.getProcessInstanceId());
                break;
            default:
                break;

        }

    }


    public void addBusArg(VariableScope variableScope, String processInstanceId){
        if(busiArg!=null){
            try {
                String busiArgStr =busiArg.getValue(variableScope).toString();
                logger.info("busiArgStr: "+ busiArgStr);
                Map<String, Object> busArgMap=stringToMap(busiArgStr);
                BusinessIntance intance=businessIntanceService.getByInstanceId(processInstanceId);
                if(intance==null){
                    throw new RuntimeException("流程实例为null");
                }
                logger.info("intance: "+intance);
                String formArg=intance.getBusiArgs();
                Map<String,Object> formArgMap= JSON.parseObject(formArg,Map.class);
                formArgMap.putAll(busArgMap);
                intance.setBusiArgs(JSON.toJSONString(formArgMap));
                businessIntanceService.save(intance); // 更新instance中busiArg的数据
                this.flowService.setVariables(intance.getProcessInstanceId(),formArgMap); // 更新流程中busiArg的数据
            } catch (JsonSyntaxException e) {
                logger.info("追加属性异常",e);
            }
        }
    }

    public Map<String,Object> stringToMap(String str){
        Map<String, Object> map = null;
        try {
            String[] strs = str.split(",");
            map = new HashMap<>();
            for(String s:strs) {
                String[] ms = s.split(":");
                map.put(ms[0], ms[1]);
            }
        } catch (Exception e) {
            logger.info("追加属性数据格式有误");
            e.printStackTrace();
        }
        return  map;
    }






}

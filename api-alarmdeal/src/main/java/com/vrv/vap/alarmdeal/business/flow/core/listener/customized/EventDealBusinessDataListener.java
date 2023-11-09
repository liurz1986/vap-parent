package com.vrv.vap.alarmdeal.business.flow.core.listener.customized;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model.EventDealBusiData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.IEventDealBusiDataService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.evaluation.util.EventQueUtil;
import com.vrv.vap.alarmdeal.business.evaluation.vo.EventResultVO;
import com.vrv.vap.alarmdeal.business.flow.core.constant.FlowConstant;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.common.DateUtil;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 事件处置业务数据表监听器
 * 采用原型模式进行注入
 */
@Service("eventDealBusinessDataListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EventDealBusinessDataListener implements TaskListener {
    private static Logger logger = LoggerFactory.getLogger(EventDealBusinessDataListener.class);
    @Autowired
    private FlowService flowService;
    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;
    @Autowired
    private IEventDealBusiDataService eventDealBusiDataService;
    @Autowired
    private AdminFeign adminFeign;
    private static Gson gson = new Gson();
    @Autowired
    private RuntimeService runtimeService;
    String[] causeTypeNames = new String[]{"", "正常业务行为", "未履行审批手续", "未及时登记或变更基础信息和审批信息", "人员不知悉相关规定", "人员误操作",
            "人员故意违规", "运维管理不当", "防护策略失效", "设备管理不当",
            "感染病毒或恶意程序", "系统自身漏洞", "其他原因", "误报"};

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        logger.info("##########eventName={},time={}", eventName, DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN));
        String executionId = delegateTask.getExecutionId();
        //根据执行id获取
        BusinessIntance instance = flowService.getFlowInstance(executionId);
        //业务数据参数
        String businessArgs = instance.getBusiArgs();
        EventDealBusiData eventDealBusiData = getEventDealBusinessDataByBusinessArgs(businessArgs);
        switch (eventName) {
            //流程节点结束
            case "delete":
                eventDealBusiData.setDealStatus(1);
                eventDealBusiData.setCreateTime(new Date());
                //查询数据库的事件业务处置数据
                Object actionObj = runtimeService.getVariable(delegateTask.getExecutionId(), FlowConstant.ACTION);
                String action = actionObj == null ? "" : String.valueOf(actionObj);
                logger.warn("事件处置业务数据表监听器,action={}", action);
                if ("通过".equals(action)) {
                    //审核通过了
                    eventDealBusiData.setDealStatus(2);
                    eventDealBusiData.setFinishTime(new Date());
                    //发送事件处置流程数据
                    sendEventMsgQueue(eventDealBusiData);
                }
                //如果是打回的话，action="不通过"
                //入库保存或者更新
                saveEventDealBusiData(eventDealBusiData);
                break;
            default:
                break;
        }

    }

    private void saveEventDealBusiData(EventDealBusiData eventDealBusiData) {
        try{
            eventDealBusiDataService.save(eventDealBusiData);
        }catch (Exception e){
            logger.error("业务数据参数入库异常",e);
        }
    }

    /**
     * 事件处置完成后发送事件发送队列消息
     * 发送的格式
     * //{"orgName":"江岸区保密办","orgCode":"JG000006","geneticType":"未履行审批手续","roleName":"运维主管","roleCode":"operationMgr","eventGuid":"7d669e06dece42a0b6bd70745e8d87df","userName":"运维主管"}
     *
     * @param eventDealBusiData 事件处置业务数据
     */
    private void sendEventMsgQueue(EventDealBusiData eventDealBusiData) {
        try{
            AlarmEventAttribute doc = alarmEventManagementForEsService.getDoc(eventDealBusiData.getEventId());
            List<StaffInfo> staffInfos = doc.getStaffInfos();
            StaffInfo staffInfo=null;
            if(staffInfos!=null&&staffInfos.size()>0){
                staffInfo=staffInfos.get(0);
            }
            logger.info("###########sendEventMsgQueue eventId={},staffInfo={}",eventDealBusiData.getEventId(),new Gson().toJson(staffInfo));
            EventResultVO eventResultVO = new EventResultVO();
            if(staffInfo!=null){
                eventResultVO.setUserName(staffInfo.getStaffName());
                eventResultVO.setOrgName(staffInfo.getStaffCompany());
                VData<Role> vData = adminFeign.getRoleByRoleId(staffInfo.getStaffRole());
                Role role = vData.getData();
                //集合里面有且只有可能一个元素，故这样写了。
                if(role!=null){
                    eventResultVO.setRoleName(role.getName());
                    eventResultVO.setRoleCode(role.getCode());
                }
            }
            eventResultVO.setEventGuid(eventDealBusiData.getEventId());
            if (StringUtils.isNotEmpty(eventDealBusiData.getCauseType())) {
                eventResultVO.setGeneticType(causeTypeNames[Integer.parseInt(eventDealBusiData.getCauseType())]);
            }
            logger.info("###########EventQueUtil eventResultVO={}", gson.toJson(eventResultVO));
            EventQueUtil.put(eventResultVO);
        }catch (Exception e){
            logger.error("事件处置完成后发送事件发送队列消息异常",e);
        }

    }

    /**
     * 通过业务参数数据获取事件处置业务数据
     *
     * @param businessArgs 业务参数数据
     */
    private EventDealBusiData getEventDealBusinessDataByBusinessArgs(String businessArgs) {
        EventDealBusiData eventDealBusiData = new EventDealBusiData();
        //业务数据
        Map<String, Object> businessMap = gson.fromJson(businessArgs, Map.class);
        logger.info("##############businessArgs={}", businessArgs);
        Object isBusinessNeed = businessMap.get("is_business_need");
        if (isBusinessNeed != null) {
            //是否业务需要
            eventDealBusiData.setBusinessNeed(Integer.parseInt(isBusinessNeed.toString()));
        }
        //是否误报
        Object isMisreport = businessMap.get("is_misreport");
        if (isMisreport != null) {
            eventDealBusiData.setFalsePositive(Integer.parseInt(isMisreport.toString()));
        }
        //审批登记
        Object approveInfo = businessMap.get("approve_info");
        if (approveInfo != null) {
            eventDealBusiData.setApproveInfo(gson.toJson(approveInfo));
        }
        //设备基本信息
        Object deviceInfo = businessMap.get("device_info");
        if (deviceInfo != null) {
            eventDealBusiData.setDeviceInfo(gson.toJson(deviceInfo));
        }
        //设备责任人
        Object deviceResPersonList = businessMap.get("device_res_person_list");
        if (deviceResPersonList != null) {
            eventDealBusiData.setDeviceResPersonList(gson.toJson(deviceResPersonList));
        }
        //涉事人员情况
        Object eventResPersonList = businessMap.get("event_res_person_list");
        if (eventResPersonList != null) {
            eventDealBusiData.setEventResPersonList(gson.toJson(eventResPersonList));
        }
        //防护策略
        Object protectionStrategy = businessMap.get("protection_strategy");
        if (protectionStrategy != null) {
            eventDealBusiData.setProtectionStrategy(gson.toJson(protectionStrategy));
        }
        //恶意程序情况
        Object malwareInfo = businessMap.get("malware_info");
        if (malwareInfo != null) {
            eventDealBusiData.setMalwareInfo(gson.toJson(malwareInfo));
        }
        //文件下载或刻录记录
        Object downloadFilesList = businessMap.get("download_files_list");
        if (downloadFilesList != null) {
            eventDealBusiData.setDownloadFilesList(gson.toJson(downloadFilesList));
        }
        //失泄密评估
        Object resultEvaluation = businessMap.get("result_evaluation");
        if (resultEvaluation != null) {
            eventDealBusiData.setResultEvaluation(Integer.parseInt(resultEvaluation.toString()));
        }
        //详细描述失泄密情况
        Object resultDetails = businessMap.get("result_details");
        if (resultDetails != null) {
            eventDealBusiData.setResultDetails(resultDetails.toString());
        }
        //保密宣传教育情况
        Object confidentialityPublicity = businessMap.get("confidentiality_publicity");
        if (confidentialityPublicity != null) {
            eventDealBusiData.setConfidentialityPublicity(confidentialityPublicity.toString());
        }
        //相关保密制度情况
        Object confidentialityRules = businessMap.get("confidentiality_rules");
        if (confidentialityRules != null) {
            eventDealBusiData.setConfidentialityRules(confidentialityRules.toString());
        }
        // 事件成因类型
        Object causeType = businessMap.get("cause_type");
        if (causeType != null) {
            eventDealBusiData.setCauseType(causeType.toString());
        }
        //事件成因
        Object cause = businessMap.get("cause");
        if (cause != null) {
            eventDealBusiData.setCause(cause.toString());
        }
        //事件详细过程
        Object eventInquriy = businessMap.get("event_inquriy");
        if (eventInquriy != null) {
            eventDealBusiData.setEventInquriy(eventInquriy.toString());
        }
        //整改措施
        Object rectification = businessMap.get("zjgRevise");
        if (rectification != null) {
            eventDealBusiData.setRectification(rectification.toString());
        }
        //必填字段-ruleId
        eventDealBusiData.setRuleId(businessMap.get("ruleId").toString());
        //必填字段--事件id
        eventDealBusiData.setEventId(businessMap.get("businessId").toString());
        eventDealBusiData.setGuid(eventDealBusiData.getEventId());
        return eventDealBusiData;
    }
}

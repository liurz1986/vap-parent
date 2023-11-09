package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.NumericBooleanSerializer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfoDispose;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.StringNullAdapter;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.UnitInfo;
import com.vrv.vap.jpa.common.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 事件线所上报 type=3
 * 保密主管审核通过，而且要失泄密评估为是才会发送线所
 */
@Service("UpReportLinePostService")
public class UpReportLinePostService implements IUpReportEventService {
    private static Logger logger = LoggerFactory.getLogger(UpReportLinePostService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private IUpReportCommonService upReportCommonService;
    /**
     * 空值也不会忽略
     */
    private static Gson gsonNotIgnoreNull = new GsonBuilder().serializeNulls().registerTypeAdapter(String.class,new StringNullAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Boolean.class, new NumericBooleanSerializer())
            .create();


    @Override
    public void upEventToKafka(UpEventDTO eventDTO) {
        logger.info("#############线所上报开始,eventDTO={}", gsonNotIgnoreNull.toJson(eventDTO));
        try {
            //1 构造数据
            AbstractUpEvent upEvent = constructUpEvent(eventDTO);
            //2 发送数据
            kafkaTemplate.send(getTopicName(), gsonNotIgnoreNull.toJson(upEvent));
        } catch (Exception e) {
            logger.error("#################线所上报失败了，失败的原因为{}", e);
        }
        logger.info("#########线所上报结束##############");
    }

    @Override
    public String getTopicName() {
        return "SuperviseDataSubmit";
    }

    @Override
    public AbstractUpEvent constructUpEvent(UpEventDTO eventDTO) {
        //构造es数据
        AlarmEventAttribute doc = upReportCommonService.getAlarmEventAttribute(eventDTO);
        DataLinePost dataLinePost = constractUpDisposeEs(doc);


        //基础数据
        dataLinePost.setDisposal_person_name(eventDTO.getName());
        dataLinePost.setDisposal_department_name(eventDTO.getDepartmentName());
        dataLinePost.setDisposal_person_role(upReportCommonService.getRoleNameByRoleId(eventDTO.getRoleId()));
        dataLinePost.setDisposal_time(DateUtil.format(new Date(),DateUtil.DEFAULT_DATE_PATTERN));


        //工单数据
        constractFormData(eventDTO, dataLinePost);


        LinePostEvent linePostEvent = new LinePostEvent();
        List<AbstractUpData> data = new ArrayList<>();
        data.add(dataLinePost);
        linePostEvent.setData(data);
        //线所上报
        linePostEvent.setType(IUpReportEventService.UPREPORT_LINE_POST_TYPE);
        linePostEvent.setClient_id(upReportCommonService.getClientId());
        linePostEvent.setUpdate_time(new Date());
        return linePostEvent;
    }
    /**
     * 赋值补全涉事部门和涉事单位
     * @param dataLinePost
     * @param doc 告警事件
     */
    private void setDeptAndUnit(AlarmEventAttribute doc, DataLinePost dataLinePost) {
        List<UnitInfo> unitList = doc.getUnitList();
        if(unitList==null){
            return;
        }
        //单位名称集合
        List<String> unitNameList=new ArrayList<>();
        //部门名称集合
        List<String> deptNameList=new ArrayList<>();
        for (UnitInfo unitInfo : unitList) {
            unitNameList.add(unitInfo.getUnitName());
            deptNameList.add(unitInfo.getUnitDepartName());
        }
        //涉事单位
        dataLinePost.setEvent_unit(StringUtils.join(unitNameList,","));
        //涉事部门
        dataLinePost.setEvent_dept(StringUtils.join(deptNameList,","));
    }

    /**
     * 事件处置数据构造一：构造上报es数据
     *
     * @param doc es数据
     */
    private DataLinePost constractUpDisposeEs(AlarmEventAttribute doc) {
        DataLinePost dataLinePost = gsonNotIgnoreNull.fromJson(gsonNotIgnoreNull.toJson(doc), DataLinePost.class);
        if (dataLinePost.getPerson_list() != null) {
            for (StaffInfoDispose staffInfoDispose : dataLinePost.getPerson_list()) {
                staffInfoDispose.setPerson_level(upReportCommonService.getPersonLevelName(staffInfoDispose.getPerson_level()));
            }
        }else{
            dataLinePost.setPerson_list(new ArrayList<>());
        }
        dataLinePost.setPerson_count(dataLinePost.getPerson_list() == null ? 0 : dataLinePost.getPerson_list().size());
        //补全涉事部门和单位
        setDeptAndUnit(doc,dataLinePost);
        return dataLinePost;
    }

    /**
     * 处置表单
     * 表单里面的值，如果不是整型的话给个空的字符串
     */
    private void constractFormData(UpEventDTO eventDTO, DataLinePost dataLinePost) {
        //使用卫语句，减少嵌套分支判断
        if (eventDTO == null) {
            return;
        }
        Map<String, Object> busiArgs = eventDTO.getBusiArgs();
        if (busiArgs == null) {
            return;
        }
        //表单数据
        for (String key : busiArgs.keySet()) {
            if (key == null || busiArgs.get(key) == null) {
                continue;
            }
            //表单的值
            String value = busiArgs.get(key).toString();
            switch (key) {
                case "zjgRevise":
                    //表单-技术整改措施
                    dataLinePost.setRectification(value);
                    break;
                case "zjgEventsDealTime":
                    //表单-事件处置时间
                    dataLinePost.setDisposal_time(value);
                    break;
                case "zjgReason":
                    //表单-成因分析
                    dataLinePost.setCause(value);
                    break;
                //失泄密评估
                case "result_evaluation":
                    //todo 这里需要将表单数据类型进行转变
                    dataLinePost.setResult_details(Integer.parseInt(value));
                    break;
                //事件过程(新增)
                case "event_inquriy":
                    dataLinePost.setEvent_inquriy(value);
                    break;
                //残留风险情况（新增）
                case "risidual_risk":
                    dataLinePost.setRisidual_risk(value);
                    break;
                //处置过程（新增）
                case "disposal_process":
                    dataLinePost.setDisposal_process(value);
                    break;
                    //成因类型，来自表单下拉选择获取
                case "cause_type":
                    dataLinePost.setCause_type(value);
                    break;
                    //案例依据,表单手动填写
                case "case_basis":
                    dataLinePost.setCase_basis(value);
                    break;
                default:
                    break;
            }
        }
    }

}

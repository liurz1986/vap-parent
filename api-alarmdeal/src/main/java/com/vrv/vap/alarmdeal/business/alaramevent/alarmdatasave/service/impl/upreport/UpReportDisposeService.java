package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.AbstractUpData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.AbstractUpEvent;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.DataDispose;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.DisposeEvent;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.StringNullAdapter;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventUrge;
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
 * 上报事件处置 type=2
 *
 * @author zzf
 */
@Service("UpReportDisposeService")
public class UpReportDisposeService implements IUpReportEventService {
    private static Logger logger = LoggerFactory.getLogger(UpReportDisposeService.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private IUpReportCommonService upReportCommonService;
    /**
     * 空值也不会忽略
     */
    private static Gson gsonNotIgnoreNull = new GsonBuilder().serializeNulls().registerTypeAdapter(String.class, new StringNullAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Boolean.class, new NumericBooleanSerializer())
            .create();

    /**
     * 上报事件处置数据
     * 0：待处置         告警事件产生和监管事件一起
     * 1：保密办督办     保密主管发出督促
     * 2：上级督办      真正的督办
     * 3：处置完成      保密主管审核通过
     * 4：协查处置     （等待做）协办请求
     * 这5中情况需要上报事件处置信息
     */
    @Override
    public void upEventToKafka(UpEventDTO eventDTO) {
        try {
            //1 构造数据
            AbstractUpEvent upEvent = constructUpEvent(eventDTO);
            //2 发送数据
            kafkaTemplate.send(getTopicName(), gsonNotIgnoreNull.toJson(upEvent));
        } catch (Exception e) {
            logger.error("#################事件处置上报失败了，失败的原因为{}", e);
        }
    }

    @Override
    public String getTopicName() {
        return "SuperviseDataSubmit";
    }

    @Override
    public AbstractUpEvent constructUpEvent(UpEventDTO eventDTO) {

        List<AlarmEventAttribute> docs = eventDTO.getDocs();
        DisposeEvent disposeEvent = new DisposeEvent();
        List<AbstractUpData> data = new ArrayList<>();
        for (AlarmEventAttribute doc:docs){
            if (doc == null) {
                //返回空就是不会发送，事件id有问题，查询不到数据
                logger.warn("##############eventId无法关联对应的告警数据，无法上报##################");
                return null;
            }
            //构造一：es
            DataDispose dataDispose = constractUpDisposeEs(doc);
            //构造二：公共
            constractUpDisposeCommon(disposeEvent, dataDispose, eventDTO);
            //构造三：表单
            constractUpDisposeForm(eventDTO, dataDispose);
            data.add(dataDispose);
        }
        disposeEvent.setData(data);
        return disposeEvent;
    }


    /**
     * 事件处置数据构造一：构造上报es数据
     *
     * @param doc es数据
     */
    private DataDispose constractUpDisposeEs(AlarmEventAttribute doc) {
        DataDispose eventDispose = gsonNotIgnoreNull.fromJson(gsonNotIgnoreNull.toJson(doc), DataDispose.class);
        List<AlarmEventUrge> urgeInfos = doc.getUrgeInfos();
        //单位督办描述也就是督促
        if (urgeInfos != null) {
            for (AlarmEventUrge urgeInfo : urgeInfos) {
                eventDispose.setSupervise_descripiton(urgeInfo.getUrgeRemark());
            }
        }
        FileInfoDispose fileInfoDispose = new FileInfoDispose();
        //设值文件处置对象
        upReportCommonService.setFileInfoDispose(doc.getFileInfos(), fileInfoDispose);
        List<FileInfoDispose> fileMmList = new ArrayList<>();
        fileMmList.add(fileInfoDispose);
        eventDispose.setFile_mm_list(fileMmList);
        if (eventDispose.getPerson_list() != null) {
            for (StaffInfoDispose staffInfoDispose : eventDispose.getPerson_list()) {
                staffInfoDispose.setPerson_level(upReportCommonService.getPersonLevelName(staffInfoDispose.getPerson_level()));
            }
        } else {
            eventDispose.setPerson_list(new ArrayList<>());
        }
        eventDispose.setPerson_count(eventDispose.getPerson_list() == null ? 0 : eventDispose.getPerson_list().size());
        if (eventDispose.getAssociated_device() != null) {
            eventDispose.setAssociated_device_count(eventDispose.getAssociated_device().size());
        } else {
            eventDispose.setAssociated_device(new ArrayList<>());
        }
        return eventDispose;
    }


    /**
     * 事件处置数据构造二：表单
     * 事件处置中的表单
     */
    private void constractUpDisposeForm(UpEventDTO eventDTO, DataDispose dataDispose) {
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
                    dataDispose.setRectification(value);
                    break;
                case "zjgEventsDealTime":
                    //表单-事件处置时间
                    dataDispose.setDisposal_time(value);
                    break;
                case "is_misreport":
                    //表单-是否误报  0 1 如果是误报：是误报则为0  不是误报则为1
                    dataDispose.setIs_misreport(Integer.parseInt(value));
                    break;
                case "zjgReason":
                    //表单-成因分析
                    dataDispose.setCause(value);
                    break;
                case "result_evaluation":
                    //表单-失泄密评估  造成泄密未1  未造成泄密则为0
                    dataDispose.setResult_evaluation(Integer.parseInt(value));
                    break;
                case "upload_code_162994822534464398":
                    //赋值表单附件
                    List<Attachment> attachments = upReportCommonService.getAttachmentListByArgs(busiArgs, key);
                    dataDispose.setAttachment(attachments);
                    break;
                //事件过程(新增)
                case "event_inquriy":
                    dataDispose.setEvent_inquriy(value);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 事件处置数据构造三：公共
     *
     * @param disposeEvent 处置事件，发送kafka对象
     * @param dataDispose  处置data，抽象data对象
     * @param eventDTO     事件传输对象
     */
    private void constractUpDisposeCommon(DisposeEvent disposeEvent, DataDispose dataDispose, UpEventDTO eventDTO) {
        //处置人姓名
        dataDispose.setDisposal_person_name(eventDTO.getName());
        //处置状态
        dataDispose.setDisposal_status(eventDTO.getDisposeStatus());
        //处置人角色
        List<String> roleName = eventDTO.getRoleName();
        //处置部门名称
        dataDispose.setDisposal_department_name(eventDTO.getDepartmentName());
        //处置时间按
        dataDispose.setDisposal_time(DateUtil.format(new Date(), DateUtil.DEFAULT_DATE_PATTERN));
        if (roleName != null && roleName.size() > 0) {
            dataDispose.setDisposal_person_role(StringUtils.join(roleName, ","));
        } else {
            dataDispose.setDisposal_person_role(upReportCommonService.getRoleNameByRoleId(eventDTO.getRoleId()));
        }
        List<AbstractUpData> data = new ArrayList<>();
        data.add(dataDispose);
        disposeEvent.setData(data);
        //处置的类型type=2
        disposeEvent.setType(IUpReportEventService.UPREPORT_DISPOSE_TYPE);
        disposeEvent.setClient_id(upReportCommonService.getClientId());
        disposeEvent.setUpdate_time(new Date());
    }


}

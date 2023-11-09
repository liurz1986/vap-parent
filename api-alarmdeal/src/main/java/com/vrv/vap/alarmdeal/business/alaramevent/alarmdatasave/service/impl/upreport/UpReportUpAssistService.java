package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.NumericBooleanSerializer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfoDispose;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.StringNullAdapter;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.SuperviseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 协查请求服务类，也就是协办申请时候发送的上报数据
 * type=4
 */
@Service("UpReportUpAssistService")
public class UpReportUpAssistService implements IUpReportEventService {
    private static Logger logger = LoggerFactory.getLogger(UpReportUpAssistService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private IUpReportCommonService upReportCommonService;
    /**
     * 空值也不会忽略
     */
    private static Gson gsonNotIgnoreNull = new GsonBuilder().serializeNulls().registerTypeAdapter(String.class, new StringNullAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Boolean.class, new NumericBooleanSerializer())
            .create();

    @Override
    public void upEventToKafka(UpEventDTO eventDTO) {
        try {
            //1 构造数据
            AbstractUpEvent upEvent = constructUpEvent(eventDTO);
            //2 发送数据
            kafkaTemplate.send(getTopicName(), gsonNotIgnoreNull.toJson(upEvent));
        } catch (Exception e) {
            logger.error("协查申请上报失败了，失败的原因为{}", e);
        }

    }

    @Override
    public String getTopicName() {
        return "SuperviseDataSubmit";
    }

    @Override
    public AbstractUpEvent constructUpEvent(UpEventDTO eventDTO) {
        SuperviseTask superviseTask = eventDTO.getSuperviseTask();
        AssistEvent assistEvent = new AssistEvent();
        assistEvent.setCo_file(getAttachments(superviseTask));
        List<AbstractUpData> data = new ArrayList<>();
        Map map = gsonNotIgnoreNull.fromJson(superviseTask.getBusiArgs(), Map.class);
        Object appName = map.get("app_name");
        Object appAccount = map.get("app_account");
        map.remove("app_name");
        map.remove("app_account");
        DataAssistApply dataItem = gsonNotIgnoreNull.fromJson(gsonNotIgnoreNull.toJson(map), DataAssistApply.class);
        if (appName != null) {
            dataItem.setApp_account(Arrays.asList(appName.toString().split(",")));
        }
        if (appAccount != null) {
            dataItem.setApp_account(Arrays.asList(appAccount.toString().split(",")));
        }
        //协办id
        dataItem.setAssis_id(superviseTask.getAssistId());
        //协查结果
        dataItem.setAssis_conclusion(superviseTask.getResponseNote());
        //将为null的集合调整为空的集合，根据规范要求来
        completeNullArrayToEmpty(dataItem);
        data.add(dataItem);
        assistEvent.setData(data);
        assistEvent.setClient_id(upReportCommonService.getClientId());
        assistEvent.setNotice_id(superviseTask.getNoticeId());
        assistEvent.setUpdate_time(new Date());
        assistEvent.setType(IUpReportEventService.UPREPORT_UP_ASSIST_TYPE);
        return assistEvent;

    }


    /**
     * 完善数组为null的字段完善为空的集合，不要用null
     */
    private void completeNullArrayToEmpty(DataAssistApply dataAssistApply) {
        //1 数组一 未知软件或病毒情况
        List<UnKnownFile> unknown_file_list = dataAssistApply.getUnknown_file_list();
        if(unknown_file_list==null){
            dataAssistApply.setUnknown_file_list(new ArrayList<>());
        }

        //数组 关联设备情况
        List<DeviceInfo> associated_device_list = dataAssistApply.getAssociated_device_list();
        if(associated_device_list==null){
            dataAssistApply.setAssociated_device_list(new ArrayList<>());
        }

        //数组  未知外部地址联通范围
        List<ConnectDevice> connect_range = dataAssistApply.getConnect_range();
        if(connect_range==null){
            dataAssistApply.setConnect_range(new ArrayList<>());
        }

        //数组 未知外部地址访问应用名称
        List<String> app_name = dataAssistApply.getApp_name();
        if(app_name==null){
            dataAssistApply.setApp_name(new ArrayList<>());
        }

        //数组 未知外部地址使用的账号
        List<String> app_account = dataAssistApply.getApp_account();
        if(app_account==null){
            dataAssistApply.setApp_account(new ArrayList<>());
        }

        //数组  涉事人员信息
        List<StaffInfoDispose> person_list = dataAssistApply.getPerson_list();
        if(person_list==null){
            dataAssistApply.setPerson_list(new ArrayList<>());
        }

    }


    /**
     * 获取附件文件列表
     *
     * @param superviseTask
     */
    private List<CoFile> getAttachments(SuperviseTask superviseTask) {
        //附件json数据
        String attachmentJSON = superviseTask.getApplyAttachment();
        //文件转二进制字符串
        return upReportCommonService.getColFilesByAttachmentJSON(attachmentJSON, gsonNotIgnoreNull);
    }
}

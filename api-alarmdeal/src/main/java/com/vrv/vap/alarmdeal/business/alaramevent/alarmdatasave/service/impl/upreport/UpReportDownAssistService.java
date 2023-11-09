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
 * 协办反馈所填写的数据进行上报
 * type=5
 */
@Service("UpReportDownAssistService")
public class UpReportDownAssistService implements IUpReportEventService {
    //
    private static Logger logger = LoggerFactory.getLogger(UpReportDownAssistService.class);

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
            logger.error("协办反馈上报失败了，失败的原因为{}", e);
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
        //表单中的数据存入了业务字段里面，直接json转对象可以进行获取
        DataAssistResponse dataItem = strToArrayGetDataAssistResponse(superviseTask.getBusiArgs());
        //将字段为null的转为空的数组
        completeNullArrayToEmpty(dataItem);
        data.add(dataItem);
        assistEvent.setData(data);
        assistEvent.setClient_id(upReportCommonService.getClientId());
        assistEvent.setNotice_id(superviseTask.getNoticeId());
        assistEvent.setUpdate_time(new Date());
        assistEvent.setType(IUpReportEventService.UPREPORT_DOWN_ASSIST_TYPE);
        return assistEvent;

    }

    /**
     * 字符串变为数组得到协助返回对象
     * 应用名称和应用账号表单里面填充的字符串，但是上报需要是数组
     *
     * @param busiArgs 业务参数数据
     */
    private static DataAssistResponse strToArrayGetDataAssistResponse(String busiArgs) {
        Map map = gsonNotIgnoreNull.fromJson(busiArgs, Map.class);
        Object appName = map.get("app_name");
        Object connect_range = map.get("connect_range");
        Object appAccount = map.get("app_account");
        map.remove("app_name");
        map.remove("app_account");
        map.remove("connect_range");
        DataAssistResponse dataItem = gsonNotIgnoreNull.fromJson(gsonNotIgnoreNull.toJson(map), DataAssistResponse.class);
        if (appName != null) {
            dataItem.setApp_account(gsonNotIgnoreNull.fromJson(appName.toString(),List.class));
        }
        if (appAccount != null) {
            dataItem.setApp_account(gsonNotIgnoreNull.fromJson(appAccount.toString(),List.class));
        }
        List<ConnectDevice> connectRanges = gsonNotIgnoreNull.fromJson(connect_range.toString(), List.class);
        if(connectRanges!=null){
            dataItem.setConnect_range(connectRanges);
        }
        return dataItem;
    }



    /**
     * 需要将上报的字段中是null的数组变为空的数组，不要用null
     */
    private void completeNullArrayToEmpty(DataAssistResponse dataAssistResponse) {

        //数组  person_list
        List<StaffInfoDispose> person_list = dataAssistResponse.getPerson_list();
        if (person_list == null) {
            dataAssistResponse.setPerson_list(new ArrayList<>());
        }

    }


    /**
     * 获取附件文件列表
     *
     * @param superviseTask
     */
    private List<CoFile> getAttachments(SuperviseTask superviseTask) {
        //附件json数据
        String attachmentJSON = superviseTask.getResponseAttachment();
        //文件转二进制字符串
        return upReportCommonService.getColFilesByAttachmentJSON(attachmentJSON, gsonNotIgnoreNull);
    }
}

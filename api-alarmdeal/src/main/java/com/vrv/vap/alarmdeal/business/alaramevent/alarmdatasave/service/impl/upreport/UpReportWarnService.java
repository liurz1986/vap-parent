package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.NumericBooleanSerializer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.AbstractUpData;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.AbstractUpEvent;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.DataWarn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.WarnEvent;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.StringNullAdapter;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.SuperviseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 上报预警数据
 * 预警数据type=6
 */
@Service("UpReportWarnService")
public class UpReportWarnService implements IUpReportEventService {
    private static Logger logger = LoggerFactory.getLogger(UpReportWarnService.class);

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
            logger.error("预警上报失败了，失败的原因为{}", e);
        }

    }

    @Override
    public String getTopicName() {
        return "SuperviseDataSubmit";
    }

    @Override
    public AbstractUpEvent constructUpEvent(UpEventDTO eventDTO) {
        SuperviseTask superviseTask = eventDTO.getSuperviseTask();
        //预警
        WarnEvent warnEvent = new WarnEvent();
        warnEvent.setWarn_file(getAttachments(superviseTask));
        warnEvent.setClient_id(upReportCommonService.getClientId());
        warnEvent.setNotice_id(superviseTask.getNoticeId());
        warnEvent.setType(IUpReportEventService.UPREPORT_WARN_TYPE);
        warnEvent.setUpdate_time(new Date());
        List<AbstractUpData> data = new ArrayList<>();
        AbstractUpData dataItem = gsonNotIgnoreNull.fromJson(gsonNotIgnoreNull.toJson(superviseTask), DataWarn.class);
        data.add(dataItem);
        warnEvent.setData(data);
        return warnEvent;
    }


    /**
     * 获取附件文件列表
     *
     * @param superviseTask
     */
    private List<CoFile> getAttachments(SuperviseTask superviseTask) {
        //附件文件json字符串
        String attachmentJSON = superviseTask.getApplyAttachment();
        //文件转二进制字符串
        return upReportCommonService.getColFilesByAttachmentJSON(attachmentJSON,gsonNotIgnoreNull);
    }
}

package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import com.google.gson.annotations.SerializedName;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.EventInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lps 2021/8/4
 */

@Data
public class SuperviseTaskReceiveVo {
    private String id;  //协办的话是协办id  预警的话是预警id

    /**
     * 名称
     */
    @SerializedName(value = "noticeName", alternate = {"notice_name","event_name"})
    private String noticeName;

    /**
     * 类型
     */
    @SerializedName(value = "noticeType", alternate = {"notice_type"})
    private String noticeType;


    /**
     * 任务下发时间
     */
    @SerializedName(value = "sendTime", alternate = "send_time")
    private String sendTime;

    /**
     * 创建时间
     */
    @SerializedName(value = "createTime", alternate = {"create_time"})
    private String createTime;

    /**
     * 描述
     */
    @SerializedName(value = "noticeDesc", alternate = {"notice_desc","warnning_description"})
    private String noticeDesc;

    //    /**
//     * 附件信息
//     */
    @SerializedName(value = "attachment",alternate = {"warn_file"})
    private List<CoFile> attachment = new ArrayList<>();
    ////    /**
//     * 关联事件及描述信息
//     */
    private List<EventInfo> event;


    /**
     * 附件名称
     */
    private String fileName;

    /**
     * 事件id
     */
    @SerializedName(value = "eventId", alternate = {"event_id"})
    private String eventId;
    /**
     * 申请
     */
    @SerializedName(value = "applyUnit", alternate = {"apply_unit"})
    private String applyUnit;
    @SerializedName(value = "assistUnit", alternate = {"assist_unit"})
    private String assistUnit;
    /**
     * 响应内容
     */
    @SerializedName(value = "responseNote", alternate = {"conclusion","warnning_conlusion","assis_conclusion"})
    private String responseNote;
    //事件简要描述
    @SerializedName(value = "eventDescription", alternate = {"event_description"})
    private String eventDescription;

    @SerializedName(value = "noticeId", alternate = {"notice_id","warnning_id","assis_id"})
    private String noticeId;
    /**
     * 业务数据
     */
    private String busiArgs;

}

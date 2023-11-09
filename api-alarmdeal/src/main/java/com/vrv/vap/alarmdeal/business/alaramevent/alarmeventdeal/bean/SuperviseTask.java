package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author lps 2021/8/4
 */

@Entity
@Data
@Table(name = "supervise_task")
public class SuperviseTask implements Serializable {
    public static final String TICKET_NAME_WARING = "事件预警";
    public static final String TICKET_NAME_CO = "事件协办";
    public static final String TICKET_NAME_ASSIST = "事件协查";
    /**
     * 已处置
     */
    public static final String COMPLETE = "1";
    /**
     * 待处置
     */
    public static final String TODO = "0";


    /**
     * 督办
     */
    public static final String SUPERVISE = "1";


    /**
     * 预警
     */
    public static final String WARNING = "2";
    /**
     *
     */
    public static final String ASSISTING_UP = "4";

    /**
     * 收到协办请求
     */
    public static final String ASSISTING_DOWN = "3";

    /**
     * 下发 从上级拉取协办任务，已经对协办任务处理后上报，它的taskCreate都是down--->别人发给我的
     */
    public static final String TASK_DOWN = "down";

    /**
     * 上报  注意了：这里协办申请后，从上级来取反馈的结果，它的taskCreate都是up；--->我主动发起的
     */
    public static final String TASK_UP = "up";

    /**
     * 主键id
     */
    @Id
    @Column(name = "guid", length = 50)
    private String guid;


    /**
     * 名称
     */
    @Column(name = "notice_name")
    private String noticeName;

    /**
     * 类型
     */
    @Column(name = "notice_type")
    private String noticeType;


    /**
     * 任务创建时间
     */
    @Column(name = "create_time")
    private String createTime;
    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private String sendTime;
    /**
     * 协查编码
     */
    @Column(name = "assist_id")
    private String assistId;


    /**
     * 描述    预警描述        协办说明
     */
    @Column(name = "notice_desc")
    private String noticeDesc;


    /**
     * 处理状态
     */
    @Column(name = "deal_status")
    private String dealStatus;

    /**
     * 事件id
     * 字段命名由上级确定
     */
    @Column(name = "event_id")
    private String eventId;

    /**
     * 响应信息  反馈结果
     */
    @Column(name = "response_note")
    @SerializedName(value = "responseNote",alternate = {"warnning_conlusion"})
    private String responseNote;


    /**
     * 任务产生途径
     * up--上报，down下发
     */
    @Column(name = "task_create")
    private String taskCreate;

    /**
     * 响应时间
     */
    @Column(name = "response_time")
    private String responseTime;
    ///////////////////////////////////////////新增的字段注意补充到sql中////////////////////////
    /**
     * 申请单位 新增
     */
    @Column(name = "apply_unit")
    private String applyUnit;
    /**
     * 协办单位 新增
     */
    @Column(name = "assist_unit")
    @SerializedName(value = "assistUnit",alternate = {"assist_unit,assis_unit"})
    private String assistUnit;

    /**
     * 预警任务描述     事件简要描述
     */
    @Column(name = "task_desc")
    @SerializedName(value = "taskDesc", alternate = {"eventDescription","warnning_description"})
    private String taskDesc;
    /**
     * 申请附件
     */
    @Column(name = "apply_attachment")
    private String applyAttachment;
    /**
     * 反馈附件
     */
    @Column(name = "response_attachment")
    private String responseAttachment;

    @Column(name = "notice_id")
    private String noticeId;
    /**
     * 协查单位目前对事件采取的措施  工单数据
     */
    @Column(name = "disposal_describe")
    @SerializedName(value = "disposalDescribe", alternate = "disposal_describe")
    private String disposalDescribe;
    /**
     * 预警id
     */
    @Column(name = "warnning_id")
    @SerializedName(value = "warnningId",alternate = "warnning_id")
    private String warnningId;
    ///////////////////////////////////////////新增的字段注意补充到sql中 20230831 新标准////////////////////////
    /**
     * 表单中的业务数据
     */
    @Column(name = "busi_args")
    private String busiArgs;

}

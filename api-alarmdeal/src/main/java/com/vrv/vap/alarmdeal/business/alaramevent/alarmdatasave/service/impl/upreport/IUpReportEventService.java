package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport.AbstractUpEvent;

/**
 * 上报事件抽象接口
 * 上报行为可以抽象为2个步骤
 *
 * 1 构造上报的数据
 *
 * 2  发送数据
 *
 * @author zzf
 */
public interface IUpReportEventService {
    //上报事件处置的bean名称
    String UpReportDispose_BEAN_NAME = "UpReportDisposeService";
    //上报线所的bean名称
    String UpReportLinePost_BEAN_NAME = "UpReportLinePostService";
    //上报事件监管bean名称
    String UpReportRegular_BEAN_NAME = "UpReportRegularService";
    //协查上报服务类
    String UpReportUpAssist_BEAN_NAME="UpReportUpAssistService";
    //协办反馈服务类
    String UPReportDownAssist_BEAN_NAME="UpReportDownAssistService";
    //上报预警服务类
    String UpReportWarn_BEAN_NAME = "UpReportWarnService";
    //上报处置类型type=1
    int UPREPORT_DISPOSE_TYPE=2;
    //协办反馈类型type
    int UPREPORT_DOWN_ASSIST_TYPE=5;
    //协查申请
    int UPREPORT_UP_ASSIST_TYPE=4;
    //线所
    int UPREPORT_LINE_POST_TYPE=3;
    //监管事件
    int UPREPORT_REGULAR_TYPE=1;
    //预警
    int UPREPORT_WARN_TYPE=6;


    /**
     * 上报事件到kafka，该方法可以范围2个步骤
     * (1) 构造数据
     * (2) 发送数据
     *
     * @param eventDTO 事件数据
     */
    void upEventToKafka(UpEventDTO eventDTO);

    /**
     * 获取上报的kafka主题名称
     */
    String getTopicName();

    /**
     * 构造数据
     *
     * @param eventDTO 上报事件传输对象
     */
    AbstractUpEvent constructUpEvent(UpEventDTO eventDTO);
}

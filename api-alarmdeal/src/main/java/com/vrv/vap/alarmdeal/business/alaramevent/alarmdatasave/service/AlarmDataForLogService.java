package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventLogDstBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月28日 10:12
 */
public interface AlarmDataForLogService {
    /**
     * 处理日志数据中文件信息
     *
     * @param logDstBean
     * @param doc
     */
    void handleFileData(EventLogDstBean logDstBean, AlarmEventAttribute doc);

    /**
     * 处理日志数据中部门信息
     *
     * @param logDstBean
     * @param doc
     */
    void handleUnitInfoData(EventLogDstBean logDstBean, AlarmEventAttribute doc);

    /**
     * 处理日志数据中人员信息
     *
     * @param logDstBean
     * @param doc
     */
    void handleStaffInfosData(EventLogDstBean logDstBean, AlarmEventAttribute doc);

    /**
     * 处理日志数据中设备信息
     *
     * @param logDstBean
     * @param doc
     */
    void handleDeviceInfosData(EventLogDstBean logDstBean, AlarmEventAttribute doc);

    /**
     * 处理日志数据中应用信息
     *
     * @param logDstBean
     * @param doc
     */
    void handleApplicationInfosData(EventLogDstBean logDstBean, AlarmEventAttribute doc);

    /**
     * 处理扩展字段数据
     */
    void handleExtention(EventLogDstBean logDstBean, AlarmEventAttribute doc);
}

package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.SuperviseTask;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 事件上报传输对象
 */
@Data
public class UpEventDTO {
    /**
     * 事件id
     */
    private String eventId;
    /**
     * 表单对象
     */
    private Map<String, Object> busiArgs;
    /**
     * 处置人姓名
     */
    private String name = "";
    /**
     * 角色名称
     */
    private List<String> roleName;
    /**
     * 处置人所在的部门
     */
    private String departmentName;
    /**
     * 告警事件对象
     */
    private AlarmEventAttribute doc;
    /**
     * 上报bean对象的名曾
     */
    private String upReportBeanName;
    /**
     * 事件处置状态
     */
    private String disposeStatus;
    /**
     * 预警/协办对象
     */
    private SuperviseTask superviseTask;
    /**
     * 获取roleId
     */
    private String roleId;
}

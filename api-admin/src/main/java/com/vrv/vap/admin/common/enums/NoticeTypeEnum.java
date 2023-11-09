package com.vrv.vap.admin.common.enums;

/**
 * @author wh1107066
 * @date 2023/8/30 17:23
 */
public enum NoticeTypeEnum {
    SUPERVISE_JOB("1", "事件督办任务"),
    RISK_ALARM_JOB("2", "风险预警任务"),
    COLLABORATE_JOB("3", "事件协办任务"),
    COLLABORATE_RESULT_JOB("4", "协办结果信息");
    private String type;
    private String desc;

    NoticeTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static NoticeTypeEnum getByType(Integer type) {
        for (NoticeTypeEnum dataTypeEnum : NoticeTypeEnum.values()) {
            if (dataTypeEnum.getType().equals(type)) {
                return dataTypeEnum;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

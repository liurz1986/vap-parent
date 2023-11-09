package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums;
/**
 * 告警风险等级
 * @author sj100d
 *
 */
public enum EvenTypeEnum {
    ONE("网络安全异常", "2"),
    TWO("用户行为异常", "3"),
    THREE("运维行为异常", "4"),
    FOUR("应用异常", "5"),
    FIVE("互联互通异常", "6");



    private String title;
    private String code;

    private EvenTypeEnum(String title, String code)
    {
        this.title = title;
        this.code = code;
    }

    public static String getValue(String value) {
        EvenTypeEnum[] businessModeEnums = values();
        for (EvenTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getTitle().equals(value)) {
                return businessModeEnum.getCode();
            }
        }
        return null;
    }

    public static String getDesc(String value) {
        EvenTypeEnum[] businessModeEnums = values();
        for (EvenTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getCode().equals(value)) {
                return businessModeEnum.getTitle();
            }
        }
        return null;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}

package com.vrv.vap.admin.common.enums;

public enum EventTypeEnum {
    SECRECY("secrecy","系统事件"),
    SYSADMIN("sysadmin","系统事件"),
    AUDITOR("auditor","业务事件");
    private String code;
    private String name;
    EventTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public static String  eventTypeEscape(String code) {
        String name = "";
        switch (code) {
            case "secrecy":
                name = EventTypeEnum.SECRECY.getName();
                break;
            case "sysadmin":
                name = EventTypeEnum.SYSADMIN.getName();
                break;
            case "auditor":
                name = EventTypeEnum.AUDITOR.getName();
                break;
            default:
        }
        return name;
    }
}

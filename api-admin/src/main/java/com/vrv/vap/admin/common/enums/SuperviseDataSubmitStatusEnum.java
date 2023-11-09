package com.vrv.vap.admin.common.enums;

public enum SuperviseDataSubmitStatusEnum {
    NO_SUBMIT(0,"未上报"),
    ONLINE_SUBMIT_SUCCESS(1, "在线上报成功"),
    OFFLINE_SUBMIT_SUCCESS(2, "离线上报成功"),
    ONLINE_SUBMIT_FAIL(-1, "在线上报失败");

    private Integer code;
    private String name;

    SuperviseDataSubmitStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.vrv.vap.netflow.common.enums;

public enum SendStatusEnum {
    SENDING(2,"发送中"),
    SEND_SUCCESS(3,"发送成功"),
    SEND_ERROR(4,"发送失败");

    private Integer code;
    private String name;

    SendStatusEnum(Integer code, String name) {
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

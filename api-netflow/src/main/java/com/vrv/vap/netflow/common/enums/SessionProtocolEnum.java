package com.vrv.vap.netflow.common.enums;

/**
 * @author wh1107066
 * @date 2023/9/21 18:33
 */
public enum SessionProtocolEnum {
    TLS_SESSION_PROTOCOL(1, "TLS"),
    SSL_SESSION_PROTOCOL(2, "SSL");
    private Integer type;
    private String desc;

    SessionProtocolEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

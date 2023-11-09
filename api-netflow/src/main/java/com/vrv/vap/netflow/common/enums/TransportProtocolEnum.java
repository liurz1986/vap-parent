package com.vrv.vap.netflow.common.enums;

/**
 * @author wh1107066
 * @date 2023/9/21 18:32
 */

public enum TransportProtocolEnum {
    UNKNOWN_TRANSPORT_PROTOCOL(0, "UNKNOWN"),
    TCP_TRANSPORT_PROTOCOL(6, "TCP"),
    UDP_TRANSPORT_PROTOCOL(17, "UDP");
    private Integer type;
    private String desc;

    TransportProtocolEnum(Integer type, String desc) {
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

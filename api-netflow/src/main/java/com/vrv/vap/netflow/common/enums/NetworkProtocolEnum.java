package com.vrv.vap.netflow.common.enums;

/**
 * @author wh1107066
 * @date 2023/9/21 18:32
 */
public enum NetworkProtocolEnum {

    IP_NETWORK_PROTOCOL(0, "IP"),
    ICMP_NETWORK_PROTOCOL(1, "ICMP"),
    IPV6_NETWORK_PROTOCOL(41, "IPV6"),
    GRE_NETWORK_PROTOCOL(47, "GRE"),
    ESP_NETWORK_PROTOCOL(50, "ESP"),
    AH_NETWORK_PROTOCOL(51, "AH"),
    MPLS_NETWORK_PROTOCOL(137, "MPLS");
    private Integer type;
    private String desc;
    NetworkProtocolEnum(Integer type, String desc) {
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

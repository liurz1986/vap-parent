package com.vrv.vap.netflow.common.enums;


/**
 * @author wh1107066
 * @date 2023/8/17 11:01
 */
public enum NetFlowDataTypeEnum {
    NET_CONNECT_LOG(1,"网络通连日志"),
    APPLICATION_AUDIT_LOG(2,"应用行为审计日志"),
    STATUS_INFO_LOG(3,"状态信息"),
    OTHER_LOG(4,"其他");

    private Integer type;
    private String desc;

    NetFlowDataTypeEnum(Integer type, String desc) {
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

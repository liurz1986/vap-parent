package com.vrv.vap.netflow.common.enums;


/**
 * @author wh1107066
 * @date 2023/8/17 11:01
 */
public enum NetFlowLogTypeEnum {
    NET_CONNECT_AUDIT(1, "网络连通审计日志"),
    HTTP_PROTOCOL_TYPE(2,"http协议日志"),
    DNS_PROTOCOL_TYPE(3,"dns协议日志"),
    MAIL_PROTOCOL_TYPE(4,"邮件协议数据"),
    DATABASE_PROTOCOL_TYPE(5,"数据库协议数据"),
    SSL_PROTOCOL_TYPE(6,"ssl/tls解密协议日志"),
    FILE_TRANSFORM_PROTOCOL_TYPE(7,"文件传输协议数据"),
    COMMUNICATE_PROTOCOL_TYPE(8 , "即时通信协议日志"),
    REMOTE_PROTOCOL_TYPE(9, "远程登录协议日志"),
    REMOTE_PROCEDURE_CALL_PROTOCOL_TYPE(10, "远程过程调用协议日志"),
    CONTROL_COMMAND_TYPE(11, "控制指令日志"),
    LOGIN_ACTION_TYPE(12,"登录行为数据"),
    OTHER_PROTOCOL_TYPE(13, "其他已知协议日志");
    private Integer type;
    private String desc;
    NetFlowLogTypeEnum(Integer type, String desc) {
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

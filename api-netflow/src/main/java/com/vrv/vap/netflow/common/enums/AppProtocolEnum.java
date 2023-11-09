package com.vrv.vap.netflow.common.enums;

/**
 * @author wh1107066
 * @date 2023/9/21 18:33
 */
public enum AppProtocolEnum {
    UNKNOWN_APP_PROTOCOL(0, "UNKNOWN"),
    HTTP_APP_PROTOCOL(1, "HTTP"),
    TLS_APP_PROTOCOL(2, "TLS"),
    NFS_APP_PROTOCOL(3, "NFS"),
    SMB_APP_PROTOCOL(4, "SMB"),
    RDP_APP_PROTOCOL(5, "RDP"),
    SSH_APP_PROTOCOL(6, "SSH"),
    SMTP_APP_PROTOCOL(7, "SMTP"),
    POP3_APP_PROTOCOL(8, "POP3"),
    IMAP_APP_PROTOCOL(9, "IMAP"),
    FTP_APP_PROTOCOL(10, "FTP"),
    TELNET_APP_PROTOCOL(11, "TELNET"),
    DNS_APP_PROTOCOL(12, "DNS"),
    ORACLE_APP_PROTOCOL(13, "ORACLE"),
    MYSQL_APP_PROTOCOL(14, "MYSQL"),
    SQLSERVER_APP_PROTOCOL(15, "SQLSERVER"),
    SYBASE_APP_PROTOCOL(16, "SYBASE"),
    DB2_APP_PROTOCOL(17, "DB2"),
    INFORMIX_APP_PROTOCOL(18, "INFORMIX"),
    POSTGRESQL_APP_PROTOCOL(19, "POSTGRESQL");


    private Integer type;
    private String desc;

    AppProtocolEnum(Integer type, String desc) {
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

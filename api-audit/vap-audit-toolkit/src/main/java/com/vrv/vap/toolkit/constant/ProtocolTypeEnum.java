package com.vrv.vap.toolkit.constant;


/**
 * 协议类型枚举
 * Created by lizj on 2019/11/27.
 */

public enum ProtocolTypeEnum {

    HTTP("http", "probe-net"),

    DNS("dns", "probe-dns"),

//    FTP("ftp", "probe-ftp"),

    EMAIL("email", "probe-email"),

    TLS("tls", "probe-tls"),

    FILEINFO("fileinfo", "probe-fileinfo"),

    SSH("ssh", "probe-ssh");


    private String type;
    private String index;

    ProtocolTypeEnum(String type, String index) {
        this.type = type;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public static String protocolTypeEnumEscape(String s) {
        String index = "";
        switch (s) {
            case "http":
                index = ProtocolTypeEnum.HTTP.getIndex();
                break;
            case "dns":
                index = ProtocolTypeEnum.DNS.getIndex();
                break;
//            case "ftp":
//                index = ProtocolTypeEnum.FTP.getIndex();
//                break;
            case "email":
                index = ProtocolTypeEnum.EMAIL.getIndex();
                break;
            case "tls":
                index = ProtocolTypeEnum.TLS.getIndex();
                break;
            case "fileinfo":
                index = ProtocolTypeEnum.FILEINFO.getIndex();
                break;
            case "ssh":
                index = ProtocolTypeEnum.SSH.getIndex();
                break;
            default:
        }
        return index;
    }
}

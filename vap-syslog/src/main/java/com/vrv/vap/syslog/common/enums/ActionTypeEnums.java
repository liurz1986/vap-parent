package com.vrv.vap.syslog.common.enums;

/**
 * @author huipei.x
 * @data 创建时间 2019/2/13
 * @description 类说明 :
 */
public enum ActionTypeEnums {
    /**
     * 通过枚举类型ActionType获取操作类型，存入数据库syslog表中type类型
     */
    LOGIN(0, "登录"),
    SELECT(1, "查询"),
    ADD(2, "新增"),
    UPDATE(3, "修改"),
    DELETE(4, "删除"),
    LOGOUT(5, "退出"),
    VIEW(6, "浏览"),
    EXPORT(7, "导出"),
    IMPORT(8, "导入"),
    DOWNLOAD(9, "下载"),
    UPLOAD(10, "上传");



    private Integer code;
    private String name;

    ActionTypeEnums(Integer code, String name) {
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

    public static int actionTypeEnumsEscape(String s) {
        int info = 0;
        switch (s) {
            case "登录":
                info = ActionTypeEnums.LOGIN.getCode();
                break;
            case "查询":
                info = ActionTypeEnums.SELECT.getCode();
                break;
            case "新增":
                info = ActionTypeEnums.ADD.getCode();
                break;
            case "修改":
                info = ActionTypeEnums.UPDATE.getCode();
                break;
            case "删除":
                info = ActionTypeEnums.DELETE.getCode();
                break;
            case "导出":
                info = ActionTypeEnums.EXPORT.getCode();
                break;
            case "导入":
                info = ActionTypeEnums.IMPORT.getCode();
                break;
            case "下载":
                info = ActionTypeEnums.DOWNLOAD.getCode();
                break;
            case "上传":
                info = ActionTypeEnums.UPLOAD.getCode();
                break;
            case "浏览":
                info = ActionTypeEnums.VIEW.getCode();
                break;
            case "退出":
                info = ActionTypeEnums.LOGOUT.getCode();
                break;
            default:
        }
        return info;
    }





}


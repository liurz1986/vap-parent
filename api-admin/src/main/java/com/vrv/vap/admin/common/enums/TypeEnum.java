package com.vrv.vap.admin.common.enums;

/**
 * @author huipei.x
 * @data 创建时间 2019/3/7
 * @description 类说明 :
 */
public enum TypeEnum {
    /**
     *
     */
    LOGIN(0,"登录"),
    SELECE(1, "查询"),
    ADD(2, "新增"),
    UPDATE(3, "修改"),
    DELETE(4, "删除"),
    SIGN_OUT(5, "退出"),
    BROSE(6, "浏览"),
    EXPORT(7, "导出"),
    IMPORT(8, "导入"),
    DOWNLOAD(9, "下载"),
    UPLOAD(10, "上传");

    private Integer code;
    private String name;

    TypeEnum(Integer code, String name) {
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

    public static String typeEnum(int code){
        String info = "";
        switch (code){
            case 0:info=TypeEnum.LOGIN.getName();break;
            case 1:info=TypeEnum.SELECE.getName();break;
            case 2:info=TypeEnum.ADD.getName();break;
            case 3:info=TypeEnum.UPDATE.getName();break;
            case 4:info=TypeEnum.DELETE.getName();break;
            case 5:info=TypeEnum.SIGN_OUT.getName();break;
            case 6:info=TypeEnum.BROSE.getName();break;
            case 7:info=TypeEnum.EXPORT.getName();break;
            case 8:info=TypeEnum.IMPORT.getName();break;
            case 9:info=TypeEnum.DOWNLOAD.getName();break;
            case 10:info=TypeEnum.UPLOAD.getName();break;
            default:

        }
        return info;
    }




}

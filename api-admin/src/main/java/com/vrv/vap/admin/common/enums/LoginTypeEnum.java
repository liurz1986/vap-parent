package com.vrv.vap.admin.common.enums;



/**
 * @author huipei.x
 * @data 创建时间 2019/3/7
 * @description 类说明 :
 */
public enum LoginTypeEnum {
    lOGIN(0,"普通登录"),
    CERTIFICATE(1," 证书登录"),
    IRIS(2,"虹膜登录");

    private Integer code;
    private String name;
    LoginTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    public static String loginTypeEnumEscape(int code) {
        String name = "";
        switch (code) {
            case 0:
                name = LoginTypeEnum.lOGIN.getName();
                break;
            case 1:
                name = LoginTypeEnum.CERTIFICATE.getName();
                break;
            case 2:
                name = LoginTypeEnum.IRIS.getName();
                break;
            default:

        }
        return name;
    }
}

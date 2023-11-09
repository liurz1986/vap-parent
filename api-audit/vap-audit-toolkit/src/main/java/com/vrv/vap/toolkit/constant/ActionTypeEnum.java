package com.vrv.vap.toolkit.constant;

import org.apache.commons.lang.StringUtils;

/**
 * 操作类型
 * Created by lizj on 2019/8/8.
 */
public enum ActionTypeEnum {

    LOGIN(0, "登录"),

    LOGIN_TYPE(0, "login"),

    GET_LOGIN(0, "getLogin"),

    SELECE(1, "查询"),

    SELECE_TYPE(1, "selete"),

    FIND_TYPE(1, "find"),

    QUERY_TYPE(1, "query"),

    GET_TYPE(1, "get"),

    ADD(2, "新增"),

    ADD_TYPE(2, "add"),

    SAVE(2, "save"),

    UPDATE(3, "修改"),

    UPDATE_TYPE(3, "update"),

    EDIT_TYPE(3, "edit"),

    MODIFY_TYPE(3, "modify"),

    DELETE_TYPE(4, "delete"),

    DEL_TYPE(4, "del"),

    DELETE(4, "删除");


    private Integer code;
    private String name;

    ActionTypeEnum(Integer code, String name) {
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

    /**
     * 根据描述识别操作类型
     *
     * @String s
     * @return
     */
    public static int actionTypeEnumEscape(String s) {
        int info = 0;
        switch (s) {
            case "登录":
                info = ActionTypeEnum.LOGIN.getCode();
                break;
            case "查询":
                info = ActionTypeEnum.SELECE.getCode();
                break;
            case "新增":
                info = ActionTypeEnum.ADD.getCode();
                break;
            case "修改":
                info = ActionTypeEnum.UPDATE.getCode();
                break;
            case "删除":
                info = ActionTypeEnum.DELETE.getCode();
                break;
            default:
        }
        return info;
    }

    /**
     * 根据方法名前缀识别操作类型
     *
     * @String methodName
     * @return
     */
    public static int typeName(String methodName) {
        int type = -1;
        if (StringUtils.isNotBlank(methodName)) {
            if (methodName.startsWith(ActionTypeEnum.LOGIN_TYPE.getName()) || methodName.startsWith(ActionTypeEnum.GET_LOGIN.getName())) {
                type = ActionTypeEnum.LOGIN.getCode();
            } else if (methodName.startsWith(ActionTypeEnum.FIND_TYPE.getName())
                    || methodName.startsWith(ActionTypeEnum.QUERY_TYPE.getName()) || methodName.startsWith(ActionTypeEnum.SELECE_TYPE.getName())
                    || methodName.startsWith(ActionTypeEnum.GET_TYPE.getName())
            ) {
                type = ActionTypeEnum.SELECE.getCode();
            } else if (methodName.startsWith(ActionTypeEnum.ADD_TYPE.getName())
                    || methodName.startsWith(ActionTypeEnum.SAVE.getName())) {
                type = ActionTypeEnum.ADD.getCode();
            } else if (methodName.startsWith(ActionTypeEnum.UPDATE_TYPE.getName())
                    || methodName.startsWith(ActionTypeEnum.EDIT_TYPE.getName())
                    || methodName.startsWith(ActionTypeEnum.MODIFY_TYPE.getName())) {
                type = ActionTypeEnum.UPDATE.getCode();
            } else if (methodName.startsWith(ActionTypeEnum.DELETE_TYPE.getName())
                    || methodName.startsWith(ActionTypeEnum.DEL_TYPE.getName())) {
                type = ActionTypeEnum.DELETE.getCode();
            } else {
                type = ActionTypeEnum.SELECE.getCode();
            }
        }
        return type;
    }

}

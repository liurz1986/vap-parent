package com.vrv.vap.admin.common.enums;

import lombok.Getter;

/**
 * @author lilang
 * @date 2022/9/27
 * @description
 */
@Getter
public enum UserEnum {

    SECADM("secadm","安全保密管理员"),

    ADMIN("admin","系统超级管理员"),

    SYSADM("sysadm","系统管理员"),

    AUDITADM("auditadm","安全审计员"),

    SECRETMGR("secretmgr","保密主管"),

    BUSINESSMGR("businessmgr","业务主管"),

    OPERATIONMGR("operationmgr","运维主管");

    private String userCode;

    private String userName;

    UserEnum(String userCode,String userName) {
        this.userCode = userCode;
        this.userName = userName;
    }
}

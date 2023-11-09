package com.vrv.vap.admin.common.enums;

import lombok.Getter;

/**
 * @BelongsProject server-syslog
 * @BelongsPackage com.vrv.vap.syslog.common
 * @Author tongliang@VRV
 * @CreateTime 2019/04/08 15:25
 * @Description (三权角色名枚举)
 * @Version
 */
@Getter
public enum RoleEnum {

    SYSCONTROLLER("sysAdmin", "系统管理员"),

    AUDIT("audAdmin", "安全审计员"),

    SAFETER("secAdmin", "安全管理员"),

    NONEROLE("none", "未分配角色"),

    ADMIN("admin", "平台维护员"),

    SECRETMGR("secretMgr","保密主管"),

    BUSINESSMGR("businessMgr","业务主管"),

    OPERATIONMGR("operationMgr","运维主管")
    ;


    private String roleCode;

    private String roleName;

    RoleEnum(String roleCode, String roleName) {
        this.roleCode = roleCode;
        this.roleName = roleName;
    }

}

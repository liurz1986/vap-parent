package com.vrv.vap.admin.vo;


import lombok.Data;

@Data
public class WorkbenchVo {

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 组件code
     */
    private  String codes;


    /**
     * 个性化配置
     */
    private String workbenchConfig;

}

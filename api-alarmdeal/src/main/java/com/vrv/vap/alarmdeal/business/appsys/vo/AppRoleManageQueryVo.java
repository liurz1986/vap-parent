package com.vrv.vap.alarmdeal.business.appsys.vo;

import com.vrv.vap.jpa.web.page.PageReqVap;
import lombok.Data;

/**
 * @author lps 2021/8/10
 */

@Data
public class AppRoleManageQueryVo extends PageReqVap {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 应用名称
     */
    private String appName;


}

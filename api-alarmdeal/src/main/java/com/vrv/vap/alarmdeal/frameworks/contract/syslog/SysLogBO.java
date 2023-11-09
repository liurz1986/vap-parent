package com.vrv.vap.alarmdeal.frameworks.contract.syslog;

import lombok.Data;

import java.util.Date;

/**
 * @author huipei.x
 * @data 创建时间 2019/10/10
 * @description 类说明 :
 */
@Data
public class SysLogBO {

    /**
     *  终端标识 操作IP
     */
    private String requestIp;
    /**
     *  操作类型 0 登录 1查询 2 新增 3 修改 4 删除
     */

    private int type;
    /**
     * 操作人ID
     */
    private String userId;
    /**
     * 操作人
     */
    private String userName;

    /**
     * 组织机构名称
     */
    private String organizationName;
    /**
     * 操作描述
     */

    private String description;
    /**
     * 请求路径
     */
    private String requestUrl;
    /**
     * 操作时间
     */
    private Date requestTime;
    /**
     * 请求方式
     */
    private String requestMethod;
    /**
     * 请求方法名
     */
    private String methodName;
    /**
     * 请求beanm名称
     */
    private String beanName;
    /**
     * 请求参数
     */
    private String paramsValue;
    /**
     * 操作结果
     */
    private int responseResult;

    /**
     * "用户登录类型 0：普通登录 1：证书登录 2：虹膜登录"
     */
    private int loginType;
    /**
     * 角色名称
     */
    private String roleName;
}

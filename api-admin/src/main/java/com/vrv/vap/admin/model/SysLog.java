package com.vrv.vap.admin.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author huipei.x
 * @data 创建时间 2019/6/14
 * @description 类说明 :
 */
@Data
@Entity
@Table(name = "sys_log")
public class SysLog {

    @Id
    private String id;
    /**
     *  终端标识 操作IP
     */
    @Column(name = "request_ip")
    private String requestIp;
    /**
     *  操作类型 0 登录 1查询 2 新增 3 修改 4 删除
     */

    private Integer type;
    /**
     * 操作人ID
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 操作人
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 组织机构名称
     */
    @Column(name = "organization_name")
    private String organizationName;
    /**
     * 操作描述
     */

    private String description;
    /**
     * 请求路径
     */
    @Column(name = "request_url")
    private String requestUrl;
    /**
     * 操作时间
     */
    @Column(name = "request_time")
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date requestTime;
    /**
     * 请求方式
     */
    @Column(name = "request_method")
    private String requestMethod;
    /**
     * 请求方法名
     */
    @Column(name = "method_name")
    private String methodName;
    /**
     * 请求beanm名称
     */
    @Column(name = "bean_name")
    private String beanName;
    /**
     * 请求参数
     */
    @Column(name = "params_value")
    private String paramsValue;
    /**
     * 操作结果
     */
    @Column(name = "response_result")
    private Integer responseResult;

    /**
     * "用户登录类型 0：普通登录 1：证书登录 2：虹膜登录"
     */
    @Column(name = "login_type")
    private Integer loginType;
    /**
     * 角色名称
     */
    @Column(name = "role_name")
    private String roleName;
}

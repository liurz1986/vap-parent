package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;

import javax.persistence.Column;
import java.util.Date;

public class SysLogQuery extends Query {
    private String id;
    /**
     *  终端标识 操作IP
     */
    @QueryLike
    private String requestIp;
    /**
     *  操作类型 0 登录 1查询 2 新增 3 修改 4 删除
     */
    private Integer type;
    /**
     * 操作人ID
     */
    private String userId;
    /**
     * 操作人
     */
    @QueryLike
    private String userName;

    /**
     * 组织机构名称
     */
    private String organizationName;
    /**
     * 操作描述
     */
    @QueryLike
    private String description;
    /**
     * 请求路径
     */
    private String requestUrl;
    /**
     * 操作时间
     */
    @Column(name = "requestTime")
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss")
    @QueryMoreThan
    private Date requestStartTime;

    @Column(name = "requestTime")
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss")
    @QueryLessThan
    private Date requestEndTime;
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
    private Integer responseResult;

    /**
     * "用户登录类型 0：普通登录 1：证书登录 2：虹膜登录"
     */
    private Integer loginType;
    /**
     * 角色名称
     */
    private String roleName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Date getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(Date requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public Date getRequestEndTime() {
        return requestEndTime;
    }

    public void setRequestEndTime(Date requestEndTime) {
        this.requestEndTime = requestEndTime;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getParamsValue() {
        return paramsValue;
    }

    public void setParamsValue(String paramsValue) {
        this.paramsValue = paramsValue;
    }

    public Integer getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(Integer responseResult) {
        this.responseResult = responseResult;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

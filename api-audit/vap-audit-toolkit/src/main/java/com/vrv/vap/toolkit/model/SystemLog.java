package com.vrv.vap.toolkit.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统审计日志
 * Created by lizj on 2019/8/8.
 */
public class SystemLog implements Serializable {

    private String id;
    /**
     * 终端标识 操作IP
     */

    private String requestIp;
    /**
     * 操作类型 0 登录 1查询 2 新增 3 修改 4 删除
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
    /**
     * 扩展字段
     */
    private String extendFields;


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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public int getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(int responseResult) {
        this.responseResult = responseResult;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getExtendFields() {
        return extendFields;
    }

    public void setExtendFields(String extendFields) {
        this.extendFields = extendFields;
    }

    @Override
    public String toString() {
        return "SystemLog{" +
                "id='" + id + '\'' +
                ", requestIp='" + requestIp + '\'' +
                ", type=" + type +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", description='" + description + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", requestTime=" + requestTime +
                ", requestMethod='" + requestMethod + '\'' +
                ", methodName='" + methodName + '\'' +
                ", beanName='" + beanName + '\'' +
                ", paramsValue='" + paramsValue + '\'' +
                ", responseResult=" + responseResult +
                ", loginType=" + loginType +
                ", roleName='" + roleName + '\'' +
                ", extendField='" + extendFields + '\'' +
                '}';
    }
}

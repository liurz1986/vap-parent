package com.vrv.vap.syslog.model;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.common.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author liujinhui
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

    /**
     * 是否是手动构建, true 代表手动发送， false代表自动发送
     */
    private boolean manually = true;

    /**
     * 操作对象
     */
    private String operationObject;

    /**
     * 扩展字段1
     */
    private String bz1;

    /**
     * token信息获取
     */
    private String token;

    /**
     * taskCode
     */
    private String taskCode;

    /**
     * 获取request请求中的header信息中的referer信息
     */
    private String referer;

    /**
     * 获取request请求中的header信息中的uri信息
     */
    private String requestPageUri;

    /**
     * 获取request请求中的header信息中的Title信息
     */
    private String requestPageTitle;

    public String getLogContext() {
        String context = "";
        if (StringUtils.isNotEmpty(paramsValue)) {
            List<ExtendFiledDTO> list = JSON.parseArray(paramsValue, ExtendFiledDTO.class);
            String ctx = StringUtils.isNotEmpty(description) ? description : "";
            StringBuffer sb = new StringBuffer(ctx.concat(":【"));
            for (ExtendFiledDTO extendFiledDTO : list) {
                String fieldDescription = extendFiledDTO.getFieldDescription();
                String fieldName = extendFiledDTO.getFieldName();
                Object fieldValue = extendFiledDTO.getFieldValue();
                sb.append(fieldDescription).append(":").append(fieldValue);
                sb.append(",");
            }
            context = StringUtils.substringBeforeLast(sb.toString(), ",").concat(("】"));
        }
        return context;
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
                ", manually='" + manually + '\'' +
                ", operationObject='" + operationObject + '\'' +
                ", bz1='" + bz1 + '\'' +
                ", token='" + token + '\'' +
                ", taskCode='" + taskCode + '\'' +
                ", referer='" + referer + '\'' +
                ", requestPageUri='" + requestPageUri + '\'' +
                ", requestPageTitle='" + requestPageTitle + '\'' +
                '}';
    }

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

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
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

    public boolean getManually() {
        return manually;
    }

    public void setManually(boolean manually) {
        this.manually = manually;
    }

    public String getOperationObject() {
        return operationObject;
    }

    public void setOperationObject(String operationObject) {
        this.operationObject = operationObject;
    }

    public String getBz1() {
        return bz1;
    }

    public void setBz1(String bz1) {
        this.bz1 = bz1;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getReferer() {
        return referer;
    }

    public String getRequestPageUri() {
        return requestPageUri;
    }

    public void setRequestPageUri(String requestPageUri) {
        this.requestPageUri = requestPageUri;
    }

    public String getRequestPageTitle() {
        return requestPageTitle;
    }

    public void setRequestPageTitle(String requestPageTitle) {
        this.requestPageTitle = requestPageTitle;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }
}
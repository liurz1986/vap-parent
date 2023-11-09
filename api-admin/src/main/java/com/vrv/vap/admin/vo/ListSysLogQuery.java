package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;


/**
 * @author huipei.x
 * @data 创建时间 2018/11/12
 * @description 类说明 : 查询系统日志条件VO
 */
public class ListSysLogQuery extends Query {

    /**
     * 操作IP
     */
    @ApiModelProperty(value="操作IP")
    @Column(name = "request_ip")
    @QueryLike
    private String requestIp;


    /**
     *  操作类型 0 登录 1查询 2 新增 3 修改 4 删除
     */
    @ApiModelProperty(value="操作类型")
    private Integer type;

    /**
     * 操作人ID
     */
    @ApiModelProperty(value="操作人ID")
    @Column(name = "user_id")
    private String userId;
    /**
     * 操作人
     */
    @ApiModelProperty(value="操作人")
    @Column(name = "user_name")
    private String userName;
    /**
     * 操作描述
     */
    @ApiModelProperty(value="操作描述")
    private String description;

    /**
     * 组织机构名称
     */
    @ApiModelProperty(value="组织机构名称")
    @Column(name = "organization_name")
    private String organizationName;
    /**
     * 操作时间开始时间
     */
    @ApiModelProperty(value="操作时间开始时间")
    private String requestStartTime;
    /**
     * 操作时间结束时间
     */
    @ApiModelProperty(value="操作时间结束时间")
    private String requestEndTime;

    /**
     * 请求方式
     */
    @ApiModelProperty(value="请求方式")
    @Column(name = "request_method")
    private String requestMethod;

    @ApiModelProperty(value="方法名称")
    @Column(name = "method_name")
    private String methodName;
    /**
     * "用户登录类型 0：普通登录 1：证书登录 2：虹膜登录"
     */
    @ApiModelProperty(value="用户登录类型")
    @Column(name = "login_type")
    private Integer loginType;

    @ApiModelProperty(value="响应结果")
    @Column(name = "response_result")
    private Integer responseResult;


    /**
     * 系统事件,业务事件
     */
    @ApiModelProperty(value="业务事件类型")
    private  String  eventType;

    /**
     *   事件等级：严重的基本都是越权行为，非法登录行为，其他都属于一般
     */
    @ApiModelProperty(value="事件等级")
    private  String  eventLevel;

    /**
     *  三权
     */
    private boolean threePowers;



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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(String requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public String getRequestEndTime() {
        return requestEndTime;
    }

    public void setRequestEndTime(String requestEndTime) {
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

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public Integer getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(Integer responseResult) {
        this.responseResult = responseResult;
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel(String eventLevel) {
        this.eventLevel = eventLevel;
    }

    public boolean isThreePowers() {
        return threePowers;
    }

    public void setThreePowers(boolean threePowers) {
        this.threePowers = threePowers;
    }
}
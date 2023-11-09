package com.vrv.vap.admin.model;

import com.vrv.vap.common.annotation.LogColumn;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

@Table(name = "cascade_platform")
public class CascadePlatform {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 平台区域名称
     */
    @Column(name = "platform_name")
    @ApiModelProperty("平台区域名称")
    private String platformName;

    /**
     * 平台id
     */
    @Column(name = "platform_id")
    @ApiModelProperty("平台id")
    private String platformId;

    /**
     * 平台ip
     */
    @ApiModelProperty("平台ip地址")
    private String ip;

    /**
     * 平台端口
     */
    @ApiModelProperty("平台端口")
    private Integer port;

    /**
     * 校验token
     */
    @Ignore
    private String token;

    /**
     * 注册状态 0:未注册 1:已注册
     */
    @ApiModelProperty("注册状态")
    private Integer status;

    /**
     * 是否本机 0:下级平台 1:本机平台
     */
    @ApiModelProperty("是否本机")
    @LogColumn(mapping = "{\"0\":\"下级平台\",\"1\":\"本机平台\"}")
    private Integer local;

    /**
     * 是否主审下级 0 是，1 否
     */
    @Column(name = "product_type")
    @ApiModelProperty("是否主审下级")
    @LogColumn(mapping = "{\"0\":\"是\",\"1\":\"否\"}")
    private Integer productType;

    /**
     * 安全级别
     */
    @Column(name = "security_classification")
    @ApiModelProperty("安全级别")
    private String securityClassification;

    /**
     * 已注册状态 0:失败 1:成功
     */
    @Column(name = "reg_status")
    @ApiModelProperty("已注册状态")
    @LogColumn(mapping = "{\"0\":\"失败\",\"1\":\"成功\"}")
    private Integer regStatus;

    /**
     * 注册返回消息
     */
    @Column(name = "reg_msg")
    @ApiModelProperty("注册返回消息")
    private String regMsg;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取平台区域名称
     *
     * @return platform_name - 平台区域名称
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * 设置平台区域名称
     *
     * @param platformName 平台区域名称
     */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    /**
     * 获取平台id
     *
     * @return platform_id - 平台id
     */
    public String getPlatformId() {
        return platformId;
    }

    /**
     * 设置平台id
     *
     * @param platformId 平台id
     */
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    /**
     * 获取平台ip
     *
     * @return ip - 平台ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置平台ip
     *
     * @param ip 平台ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取平台端口
     *
     * @return port - 平台端口
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 设置平台端口
     *
     * @param port 平台端口
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取校验token
     *
     * @return token - 校验token
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置校验token
     *
     * @param token 校验token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取注册状态 0:未注册 1:已注册
     *
     * @return status - 注册状态 0:未注册 1:已注册
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置注册状态 0:未注册 1:已注册
     *
     * @param status 注册状态 0:未注册 1:已注册
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取是否本机 0:下级平台 1:本机平台
     *
     * @return local - 是否本机 0:下级平台 1:本机平台
     */
    public Integer getLocal() {
        return local;
    }

    /**
     * 设置是否本机 0:下级平台 1:本机平台
     *
     * @param local 是否本机 0:下级平台 1:本机平台
     */
    public void setLocal(Integer local) {
        this.local = local;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    /**
     * 获取安全级别
     *
     * @return security_classification - 安全级别
     */
    public String getSecurityClassification() {
        return securityClassification;
    }

    /**
     * 设置安全级别
     *
     * @param securityClassification 安全级别
     */
    public void setSecurityClassification(String securityClassification) {
        this.securityClassification = securityClassification;
    }

    public Integer getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(Integer regStatus) {
        this.regStatus = regStatus;
    }

    public String getRegMsg() {
        return regMsg;
    }

    public void setRegMsg(String regMsg) {
        this.regMsg = regMsg;
    }
}
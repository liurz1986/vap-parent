package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;

@ApiModel("")
@Table(name = "self_concern_asset")
public class SelfConcernAsset {
    @Id
    @ApiModelProperty("")
    private String guid;

    @Column(name = "user_id")
    @ApiModelProperty("")
    private String userId;

    @ApiModelProperty("")
    private String ip;

    /**
     * 0资产ip 1应用系统id 2网络边界id
     */
    @ApiModelProperty("0资产ip 1应用系统id 2网络边界id")
    private Integer type;

    /**
     * @return guid
     */
    public String getGuid() {
        return guid;
    }

    /**
     * @param guid
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取0资产ip 1应用系统id 2网络边界id
     *
     * @return type - 0资产ip 1应用系统id 2网络边界id
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置0资产ip 1应用系统id 2网络边界id
     *
     * @param type 0资产ip 1应用系统id 2网络边界id
     */
    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", guid=").append(guid);
        sb.append(", userId=").append(userId);
        sb.append(", ip=").append(ip);
        sb.append(", type=").append(type);
        sb.append("]");
        return sb.toString();
    }
}
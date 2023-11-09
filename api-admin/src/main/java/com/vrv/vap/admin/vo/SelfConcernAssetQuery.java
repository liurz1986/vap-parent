package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

public class SelfConcernAssetQuery extends Query {
    @ApiModelProperty("")
    private String guid;

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
}
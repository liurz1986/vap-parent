package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table conf_domain_sysid
 *
 * @mbg.generated do_not_delete_during_merge 2019-08-26 16:05:43
 */
@ApiModel
@SuppressWarnings("unused")
public class ConfDomainSysidQuery extends Query {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * url域名
     */
    @ApiModelProperty("url域名")
    @NotNull
    private String domain;

    /**
     * 应用系统id
     */
    @ApiModelProperty("应用系统id")
    private String sysId;

    /**
     *
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("")
    @NotNull
    private Date updateTime;

    /**
     * 是否同步 1已同步 0未同步
     */
    @ApiModelProperty("是否同步 1已同步 0未同步")
    private Integer isSync;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsSync() {
        return isSync;
    }

    public void setIsSync(Integer isSync) {
        this.isSync = isSync;
    }
}
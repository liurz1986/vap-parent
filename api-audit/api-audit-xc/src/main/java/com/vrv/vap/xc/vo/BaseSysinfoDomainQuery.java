package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table base_sysinfo_domain
 *
 * @mbg.generated do_not_delete_during_merge 2019-11-08 15:37:41
 */
@ApiModel
@SuppressWarnings("unused")
public class BaseSysinfoDomainQuery extends Query {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 系统id
     */
    @ApiModelProperty("系统id")
    private Integer sysId;

    /**
     * 域名
     */
    @ApiModelProperty("域名")
    private String domain;

    /**
     * ip地址
     */
    @ApiModelProperty("ip地址")
    private String ip;

    /**
     * 端口
     */
    @ApiModelProperty("端口")
    private String port;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
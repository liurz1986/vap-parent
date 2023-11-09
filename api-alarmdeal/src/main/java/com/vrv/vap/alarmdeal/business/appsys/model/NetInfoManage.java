package com.vrv.vap.alarmdeal.business.appsys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lps 2021/8/10
 */

@Data
@Entity
@Table(name = "net_info_manage")
@ApiModel(value = "网络信息管理")
public class NetInfoManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "网络名称")
    @Column(name = "net_name")
    private String netName;

    /**
     * 局域网，广域网
     */
    @ApiModelProperty(value = "网络类型")
    @Column(name = "net_type")
    private String netType;

    @ApiModelProperty(value = "涉密等级")
    @Column(name = "secret_level")
    private String secretLevel;

    /**
     * 1-一级，2-二级，3-三级
     */
    @ApiModelProperty(value = "防护等级")
    @Column(name = "protect_level")
    private String protectLevel;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "安全域")
    @Column(name = "domain")
    private String domain;


}

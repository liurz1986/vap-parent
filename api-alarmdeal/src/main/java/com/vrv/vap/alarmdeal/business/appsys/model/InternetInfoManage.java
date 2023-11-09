package com.vrv.vap.alarmdeal.business.appsys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "internet_info_manage")
public class InternetInfoManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "互联网单位名称")
    @Column(name = "internet_name")
    private String internetName;

    /**
     * 远程登录  网络接入
     */
    @ApiModelProperty(value = "接入方式")
    @Column(name = "internet_type")
    private String internetType;


    @ApiModelProperty(value = "涉密等级")
    @Column(name = "secret_level")
    private String secretLevel;

    /**
     * 秘密 1； 机密；2 机密(增强)3；绝密 4
     */
    @ApiModelProperty(value = "防护等级")
    @Column(name = "protect_level")
    private String protectLevel;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

}

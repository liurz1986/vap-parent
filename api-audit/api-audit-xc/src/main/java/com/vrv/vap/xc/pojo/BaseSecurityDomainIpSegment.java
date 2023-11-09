package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@TableName("base_security_domain_ip_segment")
public class BaseSecurityDomainIpSegment implements Serializable {
    private Integer id;

    /**
     * 关联使用guid
     */
    @ApiModelProperty(value = "安全域编码")
    private String code;

    /**
     * 开始IP地址段
     */
    @ApiModelProperty(value = "开始IP地址段")
    private String startIp;

    /**
     * 结束IP地址段
     */
    @ApiModelProperty(value = "结束IP地址段")
    private String endIp;

    /**
     * 开始IP地址段转换成整型
     */
    @ApiModelProperty(value = "开始IP地址段转换成整型")
    private Long startIpNum;

    /**
     * 结束IP地址段转换成整型
     */
    @ApiModelProperty(value = "结束IP地址段转换成整型")
    private Long endIpNum;

    @ApiModelProperty("安全域名称")
    private String name;
}

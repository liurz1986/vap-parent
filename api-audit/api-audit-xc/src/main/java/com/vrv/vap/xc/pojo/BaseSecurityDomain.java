package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("base_security_domain")
public class BaseSecurityDomain implements Serializable {
    private Integer id;
    /**
     * 关联使用guid
     */
    @ApiModelProperty(value = "安全域编码")
    @Ignore
    private String code;

    /**
     * 区域名称
     */
    @ApiModelProperty(value = "安全域名称")
    private String domainName;

    /**
     * 上级编号
     */
    @ApiModelProperty(value = "上级安全域")
    private String parentCode;

    /**
     * 上级编号
     */
    @ApiModelProperty(value = "层级维护编码")
    private String subCode;

    private Integer sort;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级")
    private Integer secretLevel;

    /**
     * 0为一级单位，1为二级单位，2为三级单位
     */
    @ApiModelProperty("单位级别")
    private Byte orgHierarchy;

    @ApiModelProperty("责任人名称")
    private String responsibleName;

    @ApiModelProperty("责任人code(用户编号）")
    private String responsibleCode;
    @ApiModelProperty("组织结构名称")
    private String orgName;
    @ApiModelProperty("组织结构code")
    private String orgCode;
}

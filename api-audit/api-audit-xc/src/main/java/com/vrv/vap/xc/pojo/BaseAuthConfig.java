package com.vrv.vap.xc.pojo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 审批信息配置表
 */
@Data
@ApiModel(value = "审批信息配置表")
public class BaseAuthConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;

    private String srcObj;//源对象标识

    private String dstObj; //目的对象标识

    private String dstObjLabel; //目标对象名称

    private Integer opt; //操作类型

    private int typeId; //审批类型ID

    private String extendLabel; //扩展对象名称

    private String extendObj; //扩展对象标识

    private Date createTime; //创建时间
}

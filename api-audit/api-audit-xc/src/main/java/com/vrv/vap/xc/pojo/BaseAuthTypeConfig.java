package com.vrv.vap.xc.pojo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 审批类型配置表
 */
@Data
@ApiModel(value = "审批类型配置表")
public class BaseAuthTypeConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;

    private String label; //审批类型名称

    private String srcObjType; //源对象类型标识

    private String srcObjLabel; //源对象类型名称

    private String dstObjType; //目的对象类型标识

    private String dstObjLabel; //目标对象类型名称

    private Integer opt; //动作   1 :打印 2：刻录 3： 访问  4：运维

}

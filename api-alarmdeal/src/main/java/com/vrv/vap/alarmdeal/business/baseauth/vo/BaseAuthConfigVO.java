package com.vrv.vap.alarmdeal.business.baseauth.vo;

import lombok.Data;

import java.util.Date;

/**
 * 审批信息VO
 * 2023-08
 * @author liurz
 */
@Data
public class BaseAuthConfigVO {
    private int id; //id


    private String srcObj ;//源对象标识


    private String srcObjLabel; //源对象名称


    private String dstObj; //目的对象标识


    private String dstObjLabel; //目标对象名称


    private Integer opt; //操作类型<审批信息配置中的opt的值>

    private Integer typeId; //审批类型ID


    private String extendLable; //扩展对象名称

    private String extendObj; //扩展对象标识

    private Date createTime; //创建时间
}

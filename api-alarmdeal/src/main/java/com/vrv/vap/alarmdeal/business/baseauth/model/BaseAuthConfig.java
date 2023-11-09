package com.vrv.vap.alarmdeal.business.baseauth.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 审批信息配置表
 *
 * @author liurz
 * @date 202308
 */
@Data
@Table(name="base_auth_config")
@Entity
@ApiModel(value = "审批信息配置表")
public class BaseAuthConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键生成策略为：主键自增长，适用于MySQL等部分数据库
    private int id; //id

    @Column(name="src_obj")
    private String srcObj ;//源对象标识

    @Column(name="src_obj_label")
    private String srcObjLabel; //源对象名称

    @Column(name="dst_obj")
    private String dstObj; //目的对象标识

    @Column(name="dst_obj_label")
    private String dstObjLabel; //目标对象名称

    @Column(name="opt")
    private Integer opt; //操作类型

    @Column(name="type_id")
    private int typeId; //审批类型ID

    @Column(name="extend_label")
    private String extendLable; //扩展对象名称

    @Column(name="extend_obj")
    private String extendObj; //扩展对象标识

    @Column(name="create_time")
    private Date createTime; //创建时间



}

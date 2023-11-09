package com.vrv.vap.alarmdeal.business.baseauth.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
/**
 * 审批类型配置表
 *
 * @author liurz
 * @date 202308
 */
@Data
@Table(name="base_auth_type_config")
@Entity
@ApiModel(value = "审批类型配置表")
public class BaseAuthTypeConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键生成策略为：主键自增长，适用于MySQL等部分数据库
    private int id; //id

    @Column(name="label")
    private String label; //审批类型名称

    @Column(name="src_obj_type")
    private String srcObjtype; //源对象类型标识

    @Column(name="src_obj_label")
    private String srcObjLabel; //源对象类型名称

    @Column(name="dst_obj_type")
    private String dstObjtype; //目的对象类型标识

    @Column(name="dst_obj_label")
    private String dstObjLabel; //目标对象类型名称

    @Column(name="opt")
    private Integer opt; //动作   1 :打印 2：刻录 3： 访问  4：运维

}

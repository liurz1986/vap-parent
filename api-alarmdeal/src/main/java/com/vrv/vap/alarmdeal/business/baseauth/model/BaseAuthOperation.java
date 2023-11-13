package com.vrv.vap.alarmdeal.business.baseauth.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Table(name="base_auth_operation")
@Entity
@ApiModel(value = "运维权限审批表")
public class BaseAuthOperation implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键生成策略为：主键自增长，适用于MySQL等部分数据库
    private Integer id; //id

    @Column(name="ip")
    private String ip ;//ip
    @Column(name="dst_ip")
    private String  dstIp; //appid
    @Column(name="create_time")
    private Date createTime; //创建时间
    @Column(name="type")
    private Integer type; //0资产类型 1应用系统
}

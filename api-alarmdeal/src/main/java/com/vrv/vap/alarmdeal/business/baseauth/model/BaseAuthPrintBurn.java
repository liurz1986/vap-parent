package com.vrv.vap.alarmdeal.business.baseauth.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Table(name="base_auth_print_burn")
@Entity
@ApiModel(value = "打印刻录审批表")
public class BaseAuthPrintBurn implements Serializable {

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

    @Column(name="type")
    private Integer type; //1打印 2刻录

    @Column(name="decide")
    private Integer decide; //0允许 1不允许

    @Column(name="create_time")
    private Date createTime; //创建时间
}

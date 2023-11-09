package com.vrv.vap.alarmdeal.business.analysis.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "object_resource")
@Data
public class ObjectResource {

    @Id
    @Column(name="guid")
    private String guid;

    @Column(name="name")
    private  String name;

    @Column(name="content")
    private  String content;

    @Column(name="remark")
    private  String remark;

    @Column(name="object_resource_type")
    private  String objectResourceType;

    @Column(name="create_time")
    private  Date createTime;

    @Column(name="update_time")
    private Date updateTime;

    @Column(name="version")
    private  Integer version;

    @Column(name="delete_flag")
    private  Integer deleteFlag;
    
    @Column(name="object_resource_source")
    private  Integer source;

    @Column(name="multi_version")
    private  String multiVersion;   //综合版本
    
    @Column(name="code")
    private  String code;   //唯一编码





}

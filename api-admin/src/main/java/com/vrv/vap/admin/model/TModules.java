package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Table(name = "t_modules")
@Getter
@Setter
public class TModules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="module_name")
    private String moduleName;

    @Column(name="module_type")
    private String moduleType;

    @Column(name="module_version")
    private String moduleVersion;

    @Column(name="module_desc")
    private String moduleDesc;

    @Column(name="module_instances_number")
    private Integer moduleInstancesNumber;

    @Column(name="create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    private Date createTime;

    @Column(name="module_original")
    private String moduleOriginal;

    @Column(name = "current_big_version")
    private String currentBigVersion;
}

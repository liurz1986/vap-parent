package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;


@Table(name = "user_module")
@Data
public class UserModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @Column(name = "module")
    @ApiModelProperty(value = "模块标识")
    private String module;

    @Column(name = "module_key")
    @ApiModelProperty(value = "键")
    private String key;

    @Column(name = "module_value")
    @ApiModelProperty(value = "值")
    private String value;

}
package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import java.util.Date;


@ApiModel("证书信息")
@Table(name = "license")
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product")
    private Integer product;

    @Column(name = "module")
    private String module ;

    @Column(name="updatetime")
    @JsonFormat( pattern ="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    private Date updatetime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}

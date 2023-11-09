package com.vrv.vap.admin.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Mapregion entity
 */

@Data
@Table(name="mapregion")
@Entity
public class Mapregion implements java.io.Serializable {

    // Fields

    private static final long serialVersionUID = 1L;
    @Id
    private String guid;
    @Column(name="is_main_Server")
    private Boolean mainServer;  //是否为主服务器
    @Column(name="name")
    private String name;   //级联名称
    @Column(name="ip")
    private String ip;  //级联服务器IP
    @Column(name="up_id")
    private String upId; //上级guid
    @Column(name="up_ip")
    private String upIp; //上级IP
    @Column(name="status")
    private Integer status; //级联状态 (0:正常；1：不正常；2：待确认绑定；3：待确认解绑)
    @Column(name="run_status")
    private Integer runStatus; //级联运行状态
    @Column(name="code")
    private String code; //编码
    @Column(name="charge_man")
    private String chargeMan;   //负责人
    @Column(name="phone")
    private String phone; //电话
    @Column(name="lng")
    private BigDecimal lng;// 经度
    @Column(name="lat")
    private BigDecimal lat;// 纬度
    @Column(name="region_code")
    private String regionCode; //区域编号
    @Column(name="url")
    private String url; //登录路径
    @Column(name="login_name")
    private String loginName;


}
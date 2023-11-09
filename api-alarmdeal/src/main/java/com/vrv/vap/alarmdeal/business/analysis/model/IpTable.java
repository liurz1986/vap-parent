package com.vrv.vap.alarmdeal.business.analysis.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="iptable")
public class IpTable {

    @Id
    @Column(name = "Guid")
    private String guid;

    private String startip;

    private String endip;

    @Column(name = "startip_num")
    private String startIpNum;

    @Column(name = "endip_num")
    private String endIpNum;

    private String country;

    private String repartition;

    private  String area;

    private  String location;


}

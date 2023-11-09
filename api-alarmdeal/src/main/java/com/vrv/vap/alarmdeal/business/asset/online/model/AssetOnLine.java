package com.vrv.vap.alarmdeal.business.asset.online.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name="asset_online")
@Entity
@ApiModel(value = "资产在线表")
public class AssetOnLine {
    @Id
    @Column(name="guid")
    private String guid ; //主键
    @Column(name="name")
    private String name;//资产名称
    @Column(name="ip")
    private String ip;// 资产ip
    @Column(name="mac")
    private String mac;// 资产mac
    @Column(name="group_name")
    private String groupName;// 一级资产名称
    @Column(name="group_guid")
    private String groupGuid;// 一级资产guid
    @Column(name="type_name")
    private String typeName;// 二级资产名称
    @Column(name="type_guid")
    private String typeGuid;// 二级资产guid
    @Column(name="scan_type")
    private String scanType;//发现方式
    @Column(name="os")
    private String os;// 操作系统
    @Column(name="status")
    private  String status;// 状态："0"表示在线，“1”表示离线
    @Column(name="first_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstTime;// 首次发现时间
    @Column(name="cur_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date curTime;// 最近发现时间
    @Column(name="org_name")
    private String orgName;//归属单位名称
    @Column(name="org_code")
    private String orgCode;// 归属单位Code
    @Column(name="person_name")
    private String responsibleName ;// 责任人名称
    @Column(name="person_code")
    private String responsibleCode;// 责任人code
    @Column(name="create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;// 同步时间

    @Column(name="is_delete")
    private String isDelete; // 是否删除 0表示正常，-1表示删除
    @Column(name="is_import")
    private String isImport; // 是否导入台账 0表示没有导入，1表示导入
}

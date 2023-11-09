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
@Table(name="asset_change")
@Entity
@ApiModel(value = "资产变更表")
public class AssetChange {
    @Id
    @Column(name="guid")
    private String guid ; //主键
    @Column(name="ip")
    private String ip;// 资产ip
    @Column(name="asset_type_name")
    private String assetTypeName;// 台账资产类型(资产表中二级资产类型名称)
    @Column(name="scan_type_name")
    private String scanTypeName;// 发现资产类型(发现的资产小类名称)
    @Column(name="status")
    private String status; //状态："0"表示在线，“1”表示离线
    @Column(name="handle_status")
    private String handleStatus; //处理状态  处理状态："0"表示已经处理
    @Column(name="handle_user_name")
    private String handleUserName; //处理人名称
    @Column(name="handle_user_id")
    private String handleUserId; //处理人id
    @Column(name="handle_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime; //处理时间
    @Column(name="opinion")
    private String opinion;//处理意见
    @Column(name="create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; //时间
}

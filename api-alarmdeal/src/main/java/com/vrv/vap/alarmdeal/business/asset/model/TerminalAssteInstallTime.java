package com.vrv.vap.alarmdeal.business.asset.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 终端设备操作系统安装时间统计
 * 2021-08-24
 */
@Data
@Entity
@Table(name = "asset_terminal_install_time")
public class TerminalAssteInstallTime {
    @Id
    @Column(name="guid")
    private String guid;

    @Column(name="asset_id")
    private String assetGuid;
    // 记录资产上一次系统安装时间：新增和导入时间记录的是当前系统安装时间、编辑时记录的上一次系统安装时间
    @Column(name="last_install_time")
    private Date lastInstallTime;
    // 记录当前系统安装时间(定时任务跑的时候记录)
    @Column(name="current_install_time")
    private Date currentInstallTime;

}

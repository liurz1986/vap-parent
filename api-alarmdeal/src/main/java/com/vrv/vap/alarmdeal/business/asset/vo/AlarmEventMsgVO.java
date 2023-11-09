package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

import java.util.Date;
@Data
public class AlarmEventMsgVO {

    private String ip; //资产ip地址

    private String osListOld;  //操作系统旧的

    private Date osSetuptimeOld; // 安装时间旧的

    private String osList; //操作系统

    private Date osSetuptime; // 安装时间

    private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs

    private String osType; // 操作类型 1 新增 ，2 编辑

    private String typeTreeCode; // 资产类型treeCode
}

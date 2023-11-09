package com.vrv.vap.alarmdeal.business.asset.online.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 资产在线VO
 */
@Data
public class AssetOnLineVO {

    private String guid ; //主键

    private String name;//资产名称

    private String ip;// 资产ip

    private String mac ;//mac地址

    private String groupName;// 一级资产名称

    private String groupGuid;// 一级资产guid

    private String typeName;// 二级资产名称

    private String typeGuid;// 二级资产guid

    private String scanType;//发现方式

    private String os;// 操作系统

    private String status;// 状态："0"表示在线，“1”表示离线
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstTime;// 首次发现时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date curTime;// 最近发现时间

    private String orgName;//归属单位名称

    private String orgCode;// 归属单位Code

    private String responsibleName ;// 责任人名称

    private String responsibleCode;// 责任人code
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;// 同步时间

    private String isImport; //是否导入台账:"0表示"没有导入 “1”表示导入

    private String isDelete; // 是否删除 0表示正常，-1表示删除


}

package com.vrv.vap.alarmdeal.business.asset.online.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;
import lombok.Data;

import java.util.Date;

/**
 * 资产在线导出VO
 */
@Data
public class AssetOnLineExportVO {
    @ExcelField(title = "标记", order = 1)
    private String flage; // 标记

    @ExcelField(title = "资产名称", order = 2)
    private String name;//资产名称

    @ExcelField(title = "ip地址", order = 3)
    private String ip;// 资产ip

    @ExcelField(title = "资产大类", order = 4)
    private String groupName;// 一级资产名称

    @ExcelField(title = "资产小类", order = 5)
    private String typeName;// 二级资产名称

    @ExcelField(title = "操作系统", order = 6)
    private String os;// 操作系统

    @ExcelField(title = "发现方式", order = 7)
    private String scanType;//发现方式

    @ExcelField(title = "状态", order = 8)
    private String status;// 状态："0"表示在线，“1”表示离线

    @ExcelField(title = "最近发现时间", order = 9)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date curTime;// 最近发现时间

    @ExcelField(title = "首次发现时间", order = 10)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstTime;// 首次发现时间

    @ExcelField(title = "归属单位", order = 11)
    private String orgName;//归属单位名称

    @ExcelField(title = "责任用户", order = 12)
    private String responsibleName ;// 责任人名称



}

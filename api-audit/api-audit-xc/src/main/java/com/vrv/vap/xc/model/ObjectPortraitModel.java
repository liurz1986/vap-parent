package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ApiModel("ObjectPortraitModel")
@Data
public class ObjectPortraitModel extends PageModel {
    @ApiModelProperty("设备唯一标识")
    private String devId;
    @ApiModelProperty("设备ip")
    private String devIp;
    @ApiModelProperty("客户端IP/服务器IP地址")
    private String ip;
    @ApiModelProperty("文件密级")
    private String fileLevel;
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("文件MD5")
    private String fileMd5;
    @ApiModelProperty("文件信息")
    private String fileInfo;
    @ApiModelProperty("文件大小")
    private String fileSize;
    @ApiModelProperty("时间跨度 1小时 2天 3月")
    private String interval;
    @ApiModelProperty("登录结果 0-成功、1-失败")
    private String loginResult;
    @ApiModelProperty("打印刻录类型 0打印 1刻录")
    private String opType;
    @ApiModelProperty("设备类型0用户终端、1服务器、2bm安全产品、3应用、4网络设备、5其它设备（通用办公设备）、6运维终端")
    private String devTypeGroup;
    @ApiModelProperty("设备类型0用户终端、1服务器、2bm安全产品、3应用、4网络设备、5其它设备（通用办公设备）、6运维终端")
    private String dstStdDevTypeGroup;
    @ApiModelProperty("访问系统名称")
    private String dstStdSysName;
    @ApiModelProperty("客户端IP")
    private String clientIp;
    @ApiModelProperty("资源账号")
    private String resourceAccount;
    @ApiModelProperty("资源IP")
    private String resourceIp;
    @ApiModelProperty("资源设备一级类型")
    private String resourceTypeGroup;
    @ApiModelProperty("客户端设备一级类型")
    private String stdDevTypeGroup;
    @ApiModelProperty("源ip")
    private String sip;
    @ApiModelProperty("源端口")
    private String sport;
    @ApiModelProperty("目的ip")
    private String dip;
    @ApiModelProperty("目的端口")
    private String dport;
    @ApiModelProperty("协议")
    private String protocol;
    @ApiModelProperty("文件传输方向 1上传 2下载")
    private String fileDir;
    @ApiModelProperty("流量传输方向 1发送 2接收")
    private String transfer;
    @ApiModelProperty("链接主机协议")
    private String connType;
    @ApiModelProperty("链接主机端口")
    private String connPort;
    @ApiModelProperty("操作指令")
    private String instruct;
    @ApiModelProperty("是否专用介质 专用介质:true 非专用介质:false")
    private boolean dedicatedMedia;
    @ApiModelProperty("是否工作时间 工作时间:true 非工作时间:false")
    private boolean work = true;
    @ApiModelProperty("是否秘密 秘密:true 非密:false")
    private boolean secret;
    @ApiModelProperty("端口")
    private String port;
    @ApiModelProperty("应用访问地址")
    private String url;
    @ApiModelProperty("介质产品号、截止厂商编码")
    private String vidPidInfo;
    @ApiModelProperty("ip范围段")
    private String rangIp;
    @ApiModelProperty("安全域对应ip范围段")
    private List<CommunicationModel> ipRangeList;
    @ApiModelProperty("导出文件名")
    private String exportName;
    @ApiModelProperty("1流量大小 2总字节数 3文件大小")
    private String flow = "3";
    @ApiModelProperty("应用系统编号")
    private String appNo;
    @ApiModelProperty("应用系统绑定服务器ip列表")
    private String ips;
}

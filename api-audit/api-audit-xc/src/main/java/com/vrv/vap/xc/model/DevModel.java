package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("DevModel")
@EqualsAndHashCode(callSuper = true)
@Data
public class DevModel extends PageModel{

    @ApiModelProperty("设备唯一标识")
    private String devId;
    @ApiModelProperty("设备ip")
    private String devIp;
    @ApiModelProperty("文件密级")
    private String fileLevel;
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("文件信息")
    private String fileInfo;
    @ApiModelProperty("时间跨度 1小时 2天 3月")
    private String interval;
    @ApiModelProperty("登录结果 0-成功、1-失败")
    private String loginResult;
    @ApiModelProperty("介质类型 1红盘 2通用U盘")
    private String opCode;
    @ApiModelProperty("打印刻录类型 0打印 1刻录")
    private String opType;
    @ApiModelProperty("设备类型0终端、1服务器、2bm安全产品、3应用、4网络设备、5其它设备（通用办公设备）")
    private String devTypeGroup;

    @ApiModelProperty("目的ip")
    private String dip;
    @ApiModelProperty("目的端口")
    private String dport;
    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("文件传输方向 1上传 2下载")
    private String fileDir;
}

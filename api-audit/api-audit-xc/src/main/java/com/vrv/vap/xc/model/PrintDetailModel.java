package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel("printDetailModel")
@Data
public class PrintDetailModel extends PageModel{
    @ApiModelProperty("文件名称")
    private String fileName;
    @ApiModelProperty("文件密级")
    private String fileLevel;
    @ApiModelProperty("文件类型：pdf word...")
    private String fileType;
    @ApiModelProperty("文件大小：最小值,最大值")
    private String fileSzie;
    @ApiModelProperty("业务类别")
    private String business;
    @ApiModelProperty("打印数量: 最小值,最大值")
    private String fileNum;
    @ApiModelProperty("设备标识")
    private String terminalType;
    @ApiModelProperty("用户编码")
    private String userNo;
    @ApiModelProperty("部门")
    private String orgCode;
    @ApiModelProperty("操作结果 0成功 1失败")
    private String opResult;
    @ApiModelProperty("设备名称")
    private String devName;
    @ApiModelProperty("设备ip")
    private String devIp;
    @ApiModelProperty("用户名")
    private String username;
    /**
     * 导出文件名
     */
    private String modalTitle;

    private String opType;
}
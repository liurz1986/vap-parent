package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 导出资产Cvs文件实体
 * 2021-09-06(写入字段减少)
 * dev_ip,std_dev_type_group,std_dev_type,std_dev_safety_marign,std_terminal_type,std_user_no
 */
@Data
public class AssetCsvVO {
    @ApiModelProperty("ip")
    private String devIp; // dev_ip 对应设备ip
    @ApiModelProperty("设备一级类型")
    private String stdDevTypeGroup; // std_dev_type_group  一级资产类型treeCode
    @ApiModelProperty("设备二级类型")
    private String stdDevType; // std_dev_type  二级资产类型名称
    @ApiModelProperty("所属安全域名称")
    private String stdDevSafeMarign; // 	对应 std_dev_safety_marign 对应安全域名称
    @ApiModelProperty("设备标识")
    private String stdTerminalType; // 	对应 std_terminal_type 对应终端类型terminalType
    @ApiModelProperty("人员编号")
    private String stdUserNo; // 	对应 std_user_no 对应responsibleCode
}

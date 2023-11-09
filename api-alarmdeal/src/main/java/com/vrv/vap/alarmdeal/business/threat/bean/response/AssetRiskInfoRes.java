package com.vrv.vap.alarmdeal.business.threat.bean.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/9/7 14:53
 * @description:
 */
@Data
public class AssetRiskInfoRes {
    @ApiModelProperty(value="ip")
    private String ip;

    @ApiModelProperty(value="权重")
    private String weight;

    @ApiModelProperty(value="责任人")
    private String userName;

    @ApiModelProperty(value="部门")
    private String deptName;
}

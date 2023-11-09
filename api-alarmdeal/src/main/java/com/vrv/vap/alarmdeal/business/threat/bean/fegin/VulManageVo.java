package com.vrv.vap.alarmdeal.business.threat.bean.fegin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Administrator
 * @since: 2022/8/29 15:46
 * @description:
 */
@Data
public class VulManageVo {
    @ApiModelProperty("脆弱性id")
    private String vulGuid;

    @ApiModelProperty("脆弱性名称")
    private String vulName;

    @ApiModelProperty("脆弱性值")
    private int vulValue;
}

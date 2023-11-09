package com.vrv.vap.alarmdeal.business.asset.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="资产查询接口")
public class AssetImpoertLogSearchVO {

    @ApiModelProperty(value="排序顺序")
    private String by_;   // 排序顺序

    @ApiModelProperty(value="起始页")
    private Integer start_;//起始页

    @ApiModelProperty(value="每页行数")
    private Integer count_; //每页行数
}

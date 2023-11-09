package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeleteIndexParamVo {
    @ApiModelProperty("索引" )
    private String index;
    @ApiModelProperty("类型" )
    private String type;
    @ApiModelProperty("索引ID" )
    private String id;
    @ApiModelProperty("记录" )
    private String _source;
}

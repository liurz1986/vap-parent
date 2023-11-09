package com.vrv.vap.alarmdeal.business.analysis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value="工作流表单数据")
public class SoarDataVO {
    @ApiModelProperty(value="业务信息表单信息")
    private Map<String, Object> forms;
    @ApiModelProperty(value="剧本guid")
    private String scriptGuid;
    @ApiModelProperty(value="子剧本任务guid")
    private String childScriptTaskGuid;
}
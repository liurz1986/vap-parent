package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 三合一的有关信息
 * 新增的扩展字段extend中有用到
 */
@Data
public class ThreeinOneInfo {
    @EsField("三合一版本号")
    @ApiModelProperty(value = "文件名称")
    private String threeinOneVersion;
    @EsField("三合一生产厂家编号")
    @ApiModelProperty(value = "三合一生产厂家编号")
    private String threeinOneNum;


}

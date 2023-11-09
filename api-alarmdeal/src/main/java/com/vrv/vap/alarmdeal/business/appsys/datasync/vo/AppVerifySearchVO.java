package com.vrv.vap.alarmdeal.business.appsys.datasync.vo;

import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppVerifySearchVO extends AppSysManagerVerify {
    @ApiModelProperty(value="排序字段")
    private String order_;    // 排序字段
    @ApiModelProperty(value="排序顺序")
    private String by_;   // 排序顺序
    @ApiModelProperty(value="起始页")
    private Integer start_;//起始页
    @ApiModelProperty(value="每页行数")
    private Integer count_; //每页行数
}

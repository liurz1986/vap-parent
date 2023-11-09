package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class AssetBookDiffSearchVO extends AssetBookDiffColumnsVO{
    @ApiModelProperty(value="排序字段")
    private String order_;    // 排序字段
    @ApiModelProperty(value="排序顺序")
    private String by_;   // 排序顺序
    @ApiModelProperty(value="起始页")
    private Integer start_;//起始页
    @ApiModelProperty(value="每页行数")
    private Integer count_; //每页行数
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date registerTimeStart;// 启用时间开始时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date registerTimeEnd;// 启用时间结束时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date osSetupTimeStart;// 系统安装时间开始时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date osSetupTimeEnd;// 系统安装时间结束时间

}

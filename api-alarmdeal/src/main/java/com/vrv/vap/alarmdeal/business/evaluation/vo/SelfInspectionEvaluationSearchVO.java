package com.vrv.vap.alarmdeal.business.evaluation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class SelfInspectionEvaluationSearchVO extends SelfInspectionEvaluationVO{
    @ApiModelProperty(value="排序字段")
    private String order_;    // 排序字段
    @ApiModelProperty(value="排序顺序")
    private String by_;   // 排序顺序
    @ApiModelProperty(value="起始页")
    private Integer start_;//起始页
    @ApiModelProperty(value="每页行数")
    private Integer count_; //每页行数
    @ApiModelProperty(value="自查自评项产生时间-开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;
    @ApiModelProperty(value="自查自评项产生时间-结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;
    @ApiModelProperty(value="自查自评开展时间-开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date evTimeStart;
    @ApiModelProperty(value="自查自评开展时间-结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date evTimeEnd;


}

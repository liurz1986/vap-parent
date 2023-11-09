package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VisualReportCycleQuery extends Query {
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("报表ID")
    private Integer reportId;

    @ApiModelProperty("报表名称")
    @QueryLike
    private String title;

    @ApiModelProperty("状态")
    private Integer status;
}

package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class VisualReportCycleFileQuery extends Query {
    @ApiModelProperty("主键")
    private Integer id;

    @QueryLike
    @ApiModelProperty("周期ID")
    private Integer cycleId;

    @ApiModelProperty("报表ID")
    private Integer reportId;


    @ApiModelProperty("创建时间-起始")
    @Column(name = "createTime")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    @QueryMoreThan
    private Date startTime;


    @ApiModelProperty("创建时间-截至")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    @Column(name = "createTime")
    @QueryLessThan
    private Date endTime;



}

package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-19
 */
@ApiModel(value="ReportSecurityFileHistory对象", description="")
public class ReportSecurityFileHistoryQuery extends Query {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "报告类型：1-汇报/2-通报")
    private String reportType;

    @ApiModelProperty(value = "报告地区")
    private String areaCode;

    @ApiModelProperty(value = "报告年月 yyyyMM")
    private String reportMonth;

    @ApiModelProperty(value = "生成类型：1-自动/2-手动")
    private String generationType;

    @ApiModelProperty(value = "生成时间")
    private Date generationTime;

    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @ApiModelProperty(value = "导出文件名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String fileName;

    @ApiModelProperty(value = "文件生成状态：0-正在生成/1-完成/2-生成失败")
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(String reportMonth) {
        this.reportMonth = reportMonth;
    }
    public String getGenerationType() {
        return generationType;
    }

    public void setGenerationType(String generationType) {
        this.generationType = generationType;
    }
    public Date getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(Date generationTime) {
        this.generationTime = generationTime;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReportSecurityFileHistory{" +
            "id=" + id +
            ", reportType=" + reportType +
            ", areaCode=" + areaCode +
            ", reportMonth=" + reportMonth +
            ", generationType=" + generationType +
            ", generationTime=" + generationTime +
            ", filePath=" + filePath +
            ", fileName=" + fileName +
            ", status=" + status +
        "}";
    }
}

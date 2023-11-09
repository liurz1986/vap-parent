package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.admin.model.BaseReportInterface;
import com.vrv.vap.admin.model.BaseReportModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ReportConfig {
    @ApiModelProperty("报表配置")
    private List<BaseReport> baseReports;
    @ApiModelProperty("模型")
    private List<BaseReportModel> models;
    @ApiModelProperty("接口")
    private List<BaseReportInterface> interfaces;

    public List<BaseReportInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<BaseReportInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public List<BaseReport> getBaseReports() {
        return baseReports;
    }

    public void setBaseReports(List<BaseReport> baseReports) {
        this.baseReports = baseReports;
    }

    public List<BaseReportModel> getModels() {
        return models;
    }

    public void setModels(List<BaseReportModel> models) {
        this.models = models;
    }

    public ReportConfig(List<BaseReport> baseReports, List<BaseReportModel> models) {
        this.baseReports = baseReports;
        this.models = models;
    }

    public ReportConfig(List<BaseReport> baseReports, List<BaseReportModel> models,List<BaseReportInterface> interfaces) {
        this.baseReports = baseReports;
        this.models = models;
        this.interfaces = interfaces;
    }

    public ReportConfig() {
    }
}

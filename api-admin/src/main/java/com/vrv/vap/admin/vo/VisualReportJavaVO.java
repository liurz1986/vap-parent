package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.VisualReportJava;


public class VisualReportJavaVO extends VisualReportJava {

    /**
     * 导出类型 1-pdf 2-word 3-html
     */
    private Integer exportType;

    /**
     * 文件名称
     */
    private String fileName;



    public Integer getExportType() {
        return exportType;
    }

    public void setExportType(Integer exportType) {
        this.exportType = exportType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
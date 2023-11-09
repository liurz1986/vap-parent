package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.VisualReportCatalog;
import com.vrv.vap.admin.model.VisualReportJava;
import lombok.Data;

import java.util.List;

@Data
public class VisualReportCatalogJavaVo extends VisualReportCatalog {
    private List<VisualReportJava> children;
}

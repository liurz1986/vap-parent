package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.Report;
import com.vrv.vap.admin.model.VisualReportCatalog;
import lombok.Data;

import java.util.List;


@Data
public class VisualReportCatalogVo extends VisualReportCatalog {
    private List<Report> children;
}

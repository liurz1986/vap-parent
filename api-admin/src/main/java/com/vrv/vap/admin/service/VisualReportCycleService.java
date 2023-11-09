package com.vrv.vap.admin.service;

import com.vrv.vap.admin.common.pdf.PdfExport;
import com.vrv.vap.admin.model.VisualReportCycle;
import com.vrv.vap.admin.vo.VisualReportJavaVO;
import com.vrv.vap.base.BaseService;

import java.util.Map;

/**
 * Created by CodeGenerator on 2020/09/10.
 */
public interface VisualReportCycleService extends BaseService<VisualReportCycle> {

    String exportReport(Map<String, Object> params);

    String getReportFilePath();

    boolean checkExistFile(String fileId);

    boolean checkExistFileWithSuffix(String fileId, String suffix);

    PdfExport.PdfProgress pdfExport(Map<String, String> record);

    PdfExport.PdfProgress pdfJavaExport(VisualReportJavaVO visualReportJavaVO);

    PdfExport.PdfProgress getProgress(String workId);



}

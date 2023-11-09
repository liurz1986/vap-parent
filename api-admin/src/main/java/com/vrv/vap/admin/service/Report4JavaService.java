package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.VisualReportJava;
import com.vrv.vap.admin.vo.VisualReportJavaVO;

/**
 * Created by lizj on 2020/12/12
 */
public interface Report4JavaService {

    String preview(VisualReportJava param);

    String export(VisualReportJavaVO param);
}

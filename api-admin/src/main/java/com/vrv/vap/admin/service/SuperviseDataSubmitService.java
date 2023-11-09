package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.SuperviseDataSubmit;
import com.vrv.vap.base.BaseService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by CodeGenerator on 2021/08/05.
 */
public interface SuperviseDataSubmitService extends BaseService<SuperviseDataSubmit> {
    void offLineExport(String ids, HttpServletResponse response);
}

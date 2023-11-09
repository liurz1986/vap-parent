package com.vrv.vap.admin.service;

import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.model.VisualWidgetModel;
import com.vrv.vap.admin.vo.ExportWidgetVO;
import com.vrv.vap.base.BaseService;

/**
 * Created by CodeGenerator on 2018/03/26.
 */
public interface VisualWidgetService extends BaseService<VisualWidgetModel> {

    Export.Progress exportList(ExportWidgetVO exportWidgetVO);
}

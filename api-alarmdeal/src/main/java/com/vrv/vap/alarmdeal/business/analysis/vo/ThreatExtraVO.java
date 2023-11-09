package com.vrv.vap.alarmdeal.business.analysis.vo;

import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;
import lombok.Data;

@Data
public class ThreatExtraVO {

    private String threat_library_id;
    @ExcelField(title = "详细信息", order = 1)
    private String detail_info;
    @ExcelField(title = "风险危害", order = 2)
    private String threat_harm;
    @ExcelField(title = "处理意见", order = 3)
    private String deal_advice;
    @ExcelField(title = "处理意见", order = 4)
    private String safe_advice;
}

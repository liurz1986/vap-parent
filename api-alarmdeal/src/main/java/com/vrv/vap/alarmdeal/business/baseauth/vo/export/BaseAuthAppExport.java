package com.vrv.vap.alarmdeal.business.baseauth.vo.export;

import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelField;
import lombok.Data;

/**
 * 审批类型基础配置表
 *
 * @author liurz
 * @date 202308
 */
@Data
public class BaseAuthAppExport {
    @ExportExcelField(title = "应用名称", order = 1)
    private String appName;
    @ExportExcelField(title = "内部授权IP", order = 2)
    private String insideIp;
    @ExportExcelField(title = "外部部授权IP", order = 3)
    private String outIp;
}

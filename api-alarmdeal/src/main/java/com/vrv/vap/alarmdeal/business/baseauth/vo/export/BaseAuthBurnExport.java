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
public class BaseAuthBurnExport {
    @ExportExcelField(title = "设备ip", order = 1)
    private String ip ;//ip
    @ExportExcelField(title = "资产类型", order = 2)
    private String assetType;
    @ExportExcelField(title = "人员姓名", order = 3)
    private String responsibleName;
    @ExportExcelField(title = "部门名称", order = 4)
    private String orgName;
    @ExportExcelField(title = "是否允许刻录", order = 5)
    private String decideCN;//0允许 1不允许
}

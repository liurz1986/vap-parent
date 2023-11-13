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
public class BaseAuthOperationExport {
    @ExportExcelField(title = "运维终端IP", order = 1)
    private String ip ;//ip
    @ExportExcelField(title = "运维对象资产类型", order = 2)
    private String assetType;
    @ExportExcelField(title = "运维对象IP", order = 3)
    private String dstIp;
    @ExportExcelField(title = "运维对象MAC地址", order = 4)
    private String mac;
    @ExportExcelField(title = "运维对象管理入口地址", order = 5)
    private String operationUrl;
    @ExportExcelField(title = "管理员姓名", order = 6)
    private String responsibleName;
    @ExportExcelField(title = "管理员所属部门", order = 7)
    private String orgName;
}

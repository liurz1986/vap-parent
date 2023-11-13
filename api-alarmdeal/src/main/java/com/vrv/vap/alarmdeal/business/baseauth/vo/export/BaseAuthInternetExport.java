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
public class BaseAuthInternetExport {
    @ExportExcelField(title = "互联单位名称", order = 1)
    private String internetName;
    @ExportExcelField(title = "互联网络名称", order = 2)
    private String name;
    @ExportExcelField(title = "互联网络密级", order = 3)
    private String secretLevel;
    @ExportExcelField(title = "互联边界IP", order = 4)
    private String ip ;//ip
    @ExportExcelField(title = "允许接入设备Ip", order = 5)
    private String ips;
}

package com.vrv.vap.alarmdeal.business.appsys.vo;

import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author lps 2021/8/9
 */

@Data
@ApiModel(value = "应用系统到处VO")
public class AppSysManagerExportVo {

    /**
     * 应用系统名称
     */
    @ExportExcelField(title = "应用系统名称", order = 1)
    private String appName;

    /**
     * 单位名称
     */
    @ExportExcelField(title = "单位名称", order = 4)
    private String departmentName;


    /**
     * 域名
     */
    @ExportExcelField(title = "管理入口url", order = 2)
    private String operationUrl;   //管理入口

    /**
     * 涉密等级
     */
    @ExportExcelField(title = "密级", order = 5)
    private String secretLevel;

    @ExportExcelField(title = "涉密厂商", order = 8)
    private String secretCompany;

    /**
     * 责任人
     */
    @ExportExcelField(title = "责任人", order = 3)
    private String personName;

    /**
     * 事件数量
     */
    @ExportExcelField(title = "异常事件数量", order = 7)
    private Integer countEvent;
    /**
     * 泄密
     */
    @ApiModelProperty(value = "泄密")
    @ExportExcelField(title = "窃泄密值", order = 6)
    private Integer stealLeakValue;
    @ExportExcelField(title = "应用类型", order = 9)
    @ApiModelProperty(value = "应用类型")
    private String appType;
}

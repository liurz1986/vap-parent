package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;
import lombok.Data;

import java.util.Date;

@Data
public class ProcessExportExcelVO {
    @ExcelField(title = "工单名称", order = 1)
    private String name; // 工单名称

    @ExcelField(title = "所属流程", order = 2)
    private String processDefName;//所属流程

    @ExcelField(title = "工单状态", order = 3)
    private String status;// 工单状态

    @ExcelField(title = "创建人", order = 4)
    private String createuserName;// 创建人

    @ExcelField(title = "创建时间", order = 5)
    private Date createTime;// 创建时间

    @ExcelField(title = "逾期时间", order = 6)
    private Date deadlineDate;// 逾期时间

    @ExcelField(title = "待办人员", order = 7)
    private String candidatePerson;// 待办人员

}

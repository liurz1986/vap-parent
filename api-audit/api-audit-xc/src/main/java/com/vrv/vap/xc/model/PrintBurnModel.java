package com.vrv.vap.xc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 打印刻录request
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PrintBurnModel extends PageModel{
    /**
     * 时间跨度 1：小时 2：天 3：月
     */
    private String interval;
    /**
     * 操作类型（打印：0，刻录：1）
     */
    private String opType;
    /**
     * 文件密级
     */
    private String level;
    /**
     * 结果状态，1成功 0失败
     */
    private String result;
}

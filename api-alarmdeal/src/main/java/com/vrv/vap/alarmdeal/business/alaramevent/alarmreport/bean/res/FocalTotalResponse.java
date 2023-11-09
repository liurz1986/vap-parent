package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 11:08
 */
@Data
public class FocalTotalResponse {
    // 重点监管事件总数
    private Long total;

    // 重点监管事件违规人员
    private Long focalViolations;

    // 重点监管事件违规资产
    private Long focalViolationAssets;
}

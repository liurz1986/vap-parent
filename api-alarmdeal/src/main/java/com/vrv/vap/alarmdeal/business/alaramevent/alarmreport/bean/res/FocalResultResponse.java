package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年02月15日 14:05
 */
@Data
public class FocalResultResponse {
    // 重点监管事件总数
    private Long totalResultTotal;

    // 重点监管事件违规人员
    private Long distinctStaffNoValue;

    // 重点监管事件违规资产
    private Long distinctDeviceIpValue;

    // 监管平台监测发现新增监管事件
    private Long totalAddResultTotal;

    // 特别重大事件
    private Long high5CountResultTotal;

    // 重大事件
    private Long high4CountResultTotal;

    // 较大事件
    private Long high3CountResultTotal;

    // 已处置事件
    private Long processedCountResultTotal;

    // 待处置事件
    private Long untreatedCountResultTotal;

    // 督促事件
    private Long urgeCountResultTotal;

    // 督办事件
    private Long superviseCountResultTotal;
}

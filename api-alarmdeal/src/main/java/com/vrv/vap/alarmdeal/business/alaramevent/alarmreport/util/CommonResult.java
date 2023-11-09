package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.util;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 15:47
 */
@Data
public class CommonResult<T> {
    private T list;
    private T map;

}

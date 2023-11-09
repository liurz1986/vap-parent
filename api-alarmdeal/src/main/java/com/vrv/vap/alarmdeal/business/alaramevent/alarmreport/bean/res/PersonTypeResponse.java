package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月13日 9:26
 */
@Data
public class PersonTypeResponse {

    //总计
    private Integer total;
    //涉密
    private Integer secret;
    //非涉密
    private Integer nosecret;
}

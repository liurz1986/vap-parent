package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req;

import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/10/12 11:21
 * @description:
 */
@Data
public class FieldConditionBean {
    // 字段名称
    private String fieldName;

    // 字段值
    private Object fieldValue;

    // 判断方式
    private String judgeLogic;
}

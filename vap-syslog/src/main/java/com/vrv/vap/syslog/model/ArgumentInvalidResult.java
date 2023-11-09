package com.vrv.vap.syslog.model;

import lombok.Data;

/**
 * @Author: liujinhui
 * @Date: 2020/1/15 17:18
 */
@Data
public class ArgumentInvalidResult {
    private String field;
    private Object rejectedValue;
    private String defaultMessage;

}

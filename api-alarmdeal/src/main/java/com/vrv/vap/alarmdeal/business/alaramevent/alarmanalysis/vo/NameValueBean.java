package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo;

import com.vrv.vap.jpa.web.NameValue;
import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/12/21 11:26
 * @description:
 */
@Data
public class NameValueBean {
    private String name;
    private Object value;

    public NameValueBean() {
    }

    public NameValueBean(Object value, String name) {
        this.value = value;
        this.name = name;
    }

}

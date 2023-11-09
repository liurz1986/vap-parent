package com.vrv.vap.alarmdeal.business.appsys.vo.query;

import lombok.Data;

@Data
public class AppInfoVO {
    private String appName; //涉密用于名称
    private String secretLevel; // 涉密等级
    private String company; // 涉密应用厂商
}

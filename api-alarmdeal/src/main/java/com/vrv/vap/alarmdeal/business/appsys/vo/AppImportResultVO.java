package com.vrv.vap.alarmdeal.business.appsys.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppImportResultVO {
    // 校验成功的数据：应用系统
    private List<Map<String,Object>>  trueList;
    // 校验失败的数据：应用系统
    private List<Map<String,Object>>  falseList;

    // 校验成功的关联数据：账户、角色、资源
    private Map<String,Map<String,List<Map<String,Object>>>> relations;
}

package com.vrv.vap.alarmdeal.business.appsys.vo.query;

import lombok.Data;

@Data
public class AppInfoNewVO {
    private int id; //序号

    private String  appNo; // 应用编号

    private String  appName; // 应用名称

    private String  secret; // 密级

    private int  serverNumber; // 部署服务器数量
}

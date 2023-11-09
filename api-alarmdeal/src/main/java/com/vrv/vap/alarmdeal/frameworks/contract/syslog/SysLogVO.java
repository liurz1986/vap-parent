package com.vrv.vap.alarmdeal.frameworks.contract.syslog;


import lombok.Data;

@Data
public class SysLogVO {

    private String requestIp; //接收ip

    private  String description;  //响应内容
}

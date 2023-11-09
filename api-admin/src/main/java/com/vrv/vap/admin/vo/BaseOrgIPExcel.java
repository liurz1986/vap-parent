package com.vrv.vap.admin.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class BaseOrgIPExcel {

    //上级机构编码	机构编码	机构名称	结构缩写	 其他名称	机构类型	机构级别


    @Excel(name = "起始IP" )
    private String startIpSegment;


    @Excel(name = "截止IP" )
    private String endIpSegment;


}
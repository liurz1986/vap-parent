package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Data
public class DealCommandVO {

    public   static final  String EMAIL="email";
    public   static final  String SMS="sms";

    private  String responseType;  //响应类型

    private List<T> alarmContent;   //响应类型
}

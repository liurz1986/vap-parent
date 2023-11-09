package com.vrv.vap.alarmdeal.business.appsys.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Data
@ApiModel(value = "服务器Vo")
public class AppServerVo {
    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("设备IP", "MAC", "设备名称","操作系统","涉密等级","责任人姓名","所属单位"));
    public static final String[] KEYS= new String[]{"ip","mac","name","extendSystem","secretLevel","responsibleName","orgName"};
    public static final String APP_RESOURCE_MANAGE="服务器";


    private String ip;
    private String mac;
    private String name;
    private String extendSystem;
    private String secretLevel;
    private String responsibleName;
    private String orgName;


}

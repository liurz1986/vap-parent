package com.vrv.vap.alarmdeal.business.baseauth.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Data
@ApiModel(value = "网络互联权限审批表")
public class BaseAuthInternetVo implements Serializable {
    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("互联单位名称", "允许接入设备ip"));
    public static final String[] KEYS= new String[]{"internetName","ips"};
    public static final String INFO_MANAGE="网络互联权限审批信息";
    private String internetName;
    private String name;
    private String secretLevel;
    private Integer id; //id

    private String ip ;//ip
    private String ips;
    private Integer  internetId;
    private Date createTime; //创建时间
}

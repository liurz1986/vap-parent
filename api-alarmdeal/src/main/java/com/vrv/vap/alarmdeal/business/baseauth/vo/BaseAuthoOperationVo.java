package com.vrv.vap.alarmdeal.business.baseauth.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Data
@ApiModel(value = "运维权限审批表")
public class BaseAuthoOperationVo implements Serializable {
    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("运维终端ip", "运维对象资产类型", "运维对象ip"));
    public static final String[] KEYS= new String[]{"ip","assetType","dstIp"};
    public static final String INFO_MANAGE="运维权限审批信息";
    private Integer id; //id
    private String ip ;//ip
    private String dstIp;
    private String assetType;
    private String mac;
    private String operationUrl;
    private String responsibleName;
    private String orgName;
    private String treeCode;



}

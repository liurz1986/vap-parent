package com.vrv.vap.alarmdeal.business.baseauth.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 审批类型基础配置表
 *
 * @author liurz
 * @date 202308
 */
@Data
@ApiModel(value = "打印刻录审批表")
public class BaseAuthPrintBurnVo implements Serializable {
    public static final List<String> HEADERS =  new ArrayList<String>(Arrays.asList("设备ip", "资产类型", "是否允许打印"));
    public static final String[] KEYS= new String[]{"ip","assetType","decideCN"};
    public static final String PRINT_INFO_MANAGE="打印权限审批信息";
    public static final String BURN_INFO_MANAGE="刻录权限审批信息";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer id; //id
    private String ip ;//ip
    private Integer type; //1打印 2刻录
    private Integer decide; //0允许 1不允许
    private String responsibleName;
    private String assetType;
    private String orgName;
    private String decideCN;
    private Date createTime; //创建时间
    private String treeCode;
}

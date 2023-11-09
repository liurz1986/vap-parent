package com.vrv.vap.alarmdeal.business.baseauth.vo;

import lombok.Data;


/**
 * 审批类型基础配置VO
 *
 * @author liurz
 * @date 202308
 */
@Data
public class BaseAuthCommonConfigVO  {

    private String confId; //id

    private String confValue ;//value的值

    private String confTime; //时间

    private String remark; //备注
}

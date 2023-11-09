package com.vrv.vap.alarmdeal.business.baseauth.vo;

import lombok.Data;


/**
 * 审批类型配置VO
 *
 * @author liurz
 * @date 202308
 */
@Data
public class BaseAuthTypeConfigVO  {


    private int id; //id


    private String label; //审批类型名称


    private String srcObjtype; //源对象类型


    private String srcObjLabel; //源对象类型名称


    private String dstObjtype; //目的对象类型


    private String dstObjLabel; //目标对象类型名称


    private Integer opt; //动作  1 :打印 2：刻录 3： 访问  4：运维

}

package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;

/**
 * 未履行审批手续、人员误操作、人员故意违规、人员不熟悉相关规定、违反保密法律法规行为查处
 */
@Data
public class NoInforOrgResultVO {
    // 部门名称
    private String orgName;

    //未履行审批手续
    private int num1 =0;

    //人员误操作
    private int num2 =0;

    //人员故意违规
    private int num3 =0;

    //人员不熟悉相关规定
    private int num4 =0;

    //违反保密法律法规行为查处
    private int num5 =0;
}

package com.vrv.vap.alarmdeal.business.appsys.vo.query;

import lombok.Data;

@Data
public class SecretlevelTypeVO {

    private int secretNum;  // 涉密数量

    private int noSecretNum; // 非涉密数量
}

package com.vrv.vap.alarmdeal.business.asset.contract;

import lombok.Data;

/**
 * 点数接受参数实体类
 */
@Data
public class Licensen {
    private int id;
    private int product;
    private String module;
    private String updatetime;
    private int terminalCount;
}

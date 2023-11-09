package com.vrv.vap.alarmdeal.business.buinesssystem.vo;

import lombok.Data;

@Data
public class BusinessNodeVO {
    private String name;  // 业务系统名称

    private String code;  // 业务系统id

    private String parentId; // 父业务系统id

}

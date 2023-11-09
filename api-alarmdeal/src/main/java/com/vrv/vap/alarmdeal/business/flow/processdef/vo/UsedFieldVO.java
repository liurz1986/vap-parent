package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import lombok.Data;

import java.util.Map;
@Data
public class UsedFieldVO {
    private String type;

    private String id;

    private String itemType;

    private String name;

    private String icon;

    private Map<String,Object> option;
}

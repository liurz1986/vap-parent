package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表单数据结构2022-12-12
 */
@Data
public class FormInfosVO {

    private List<UsedFieldVO> usedFields;

    private String templateName;

    private Map<String,Object> flowData;
}

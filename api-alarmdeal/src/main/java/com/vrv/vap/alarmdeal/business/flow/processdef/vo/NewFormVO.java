package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewFormVO {

    private List<NewFormStructure> usedFields;

    private Object flowData;

    private  String templateName;




}

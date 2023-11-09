package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventField implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String logField;

    private String outputField;


    private String valueType;


}

package com.vrv.rule.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventTypeObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content; //事件内容
    private String indexName; //索引名称


}

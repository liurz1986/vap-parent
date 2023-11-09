package com.vrv.rule.source.datasourceparam.impl;

import com.vrv.rule.source.datasourceparam.DataStreamRunnerParamsAbs;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESRunnerParams extends DataStreamRunnerParamsAbs implements Serializable{

    private static final long serialVersionUID = 1L;

    private String indexName; //索引

    private String startConfig; //启动配置


}

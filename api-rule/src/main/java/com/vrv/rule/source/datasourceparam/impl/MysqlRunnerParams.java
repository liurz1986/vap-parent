package com.vrv.rule.source.datasourceparam.impl;

import com.vrv.rule.source.datasourceparam.DataStreamRunnerParamsAbs;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class MysqlRunnerParams extends DataStreamRunnerParamsAbs implements Serializable {

    private static final long serialVersionUID = 1L;

    private String startConfig;

    private String tableName; //表名



}

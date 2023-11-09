package com.vrv.rule.source.datasourceparam.impl;

import com.vrv.rule.source.datasourceparam.DataStreamRunnerParamsAbs;
import com.vrv.rule.vo.FieldInfoVO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 启动相关类型
 *
 */
@Builder
@Data
public class KafkaRunnerParams extends DataStreamRunnerParamsAbs implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topicName;

    private String groupId;




}

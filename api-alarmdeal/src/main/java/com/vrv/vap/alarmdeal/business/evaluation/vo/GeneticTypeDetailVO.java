package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;
import java.util.List;

/**
 * 成因类型监管事件详情
 */
@Data
public class GeneticTypeDetailVO {
    // 成因类型名称
    private String geneticType;
    // 成因类型下的自查自评结果数据
    private List<EvaluationResultVO> evaluations;
    // 监管事件详情
    private List<EventDetailVO> eventDetails;
}

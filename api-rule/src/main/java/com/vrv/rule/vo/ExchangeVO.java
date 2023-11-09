package com.vrv.rule.vo;

import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.util.List;


/**
 * 算子输入属性
 */
@Builder
@Data
public class ExchangeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private StreamExecutionEnvironment env;

    private List<DataStreamSourceVO> dataStreamSourceVOs;

    private FilterConfigObject filterConfigObject;

    private Exchanges exchanges;

    private String roomType;   //盒子类型

    private String groupId; //kafka分组Id

    private String tag; //标识符

    private String ruleCode; //规则编码

    private String startConfig; //启动参数






}

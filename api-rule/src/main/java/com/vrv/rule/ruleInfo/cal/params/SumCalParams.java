package com.vrv.rule.ruleInfo.cal.params;

import com.vrv.rule.vo.FieldInfoVO;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * @author wudi
 * @date 2023/6/28 11:17
 */
@Builder
@Data
public class SumCalParams {

 private Row inputRow;
 private Object outRowSumValue;
 private String ouputField;
 private List<FieldInfoVO> inputFieldInfoVOs;
}

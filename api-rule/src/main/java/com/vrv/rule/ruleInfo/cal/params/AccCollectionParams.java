package com.vrv.rule.ruleInfo.cal.params;

import com.vrv.rule.vo.FieldInfoVO;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.types.Row;

import java.util.List;

@Builder
@Data
public class AccCollectionParams {
    private Object result; //返回结果
    private Row inputRow; //输入列
    private List<FieldInfoVO> inputFieldInfoVOs; //输入字段信息
    private String expressField; //求取集合的字段（采用逗号分割）


}

package com.vrv.rule.ruleInfo.cal.params;

import com.vrv.rule.vo.FieldInfoVO;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.types.Row;

import java.util.List;

/**
 * distinctCount计算输入参数
 * @author wudi
 * @date 2023/6/27 16:33
 */
@Builder
@Data
public class DistinctCountCalParams {
    private Object result; //返回结果
    private Row inputRow; //输入列
    private List<FieldInfoVO> inputFieldInfoVOs; //输入字段信息
    private String expressField; //去重字段

}

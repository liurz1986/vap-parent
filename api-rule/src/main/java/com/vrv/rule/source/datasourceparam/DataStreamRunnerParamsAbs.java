package com.vrv.rule.source.datasourceparam;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.vo.FieldInfoVO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *输入层参数
 */
@Data
public class DataStreamRunnerParamsAbs  implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<FieldInfoVO> fieldInfoVOs;

}

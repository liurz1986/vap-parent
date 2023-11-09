package com.vrv.rule.source.datasourceparam;

import com.vrv.rule.vo.FieldInfoVO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataSourceParamsAbs implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<FieldInfoVO> fieldInfoVOs;


}

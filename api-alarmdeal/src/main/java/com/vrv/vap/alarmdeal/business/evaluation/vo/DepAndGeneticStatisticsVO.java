package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;
import java.util.List;
@Data
public class DepAndGeneticStatisticsVO {
    private String geneticType; // 成因类型

    private List<KeyValueVO> datas ; // 待查部门及数据
}

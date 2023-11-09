package com.vrv.vap.alarmdeal.business.baseauth.vo;

import lombok.Data;

import java.util.List;
@Data
public class TrendResultVO {
    private String name;

    private List<CoordinateVO> coords;

}

package com.vrv.vap.toolkit.excel.in;

import com.vrv.vap.toolkit.excel.ExcelInfo;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ImportHandler {
    List<Map<String, Object>> toListMap(ExcelInfo excel);
}

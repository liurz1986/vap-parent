package com.vrv.vap.admin.common.excel.in;

import com.vrv.vap.admin.common.excel.ExcelInfo;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface ImportHandler {
	List<Map<String, Object>> toListMap(ExcelInfo excel);
}

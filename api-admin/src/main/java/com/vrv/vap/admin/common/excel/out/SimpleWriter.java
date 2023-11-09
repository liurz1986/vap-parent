package com.vrv.vap.admin.common.excel.out;

import com.vrv.vap.admin.common.excel.out.Export.Progress;

/**
 * 实现如何写入数据
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
@FunctionalInterface
public interface SimpleWriter {
	void write(Progress progress);
}

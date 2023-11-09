package com.vrv.vap.toolkit.excel.out;

import com.vrv.vap.toolkit.excel.ExcelInfo;

import java.util.List;

public class ExcelData {
    private ExcelInfo excelInfo;
    private long total;
    private List<String[]> data;

    public ExcelData(ExcelInfo excelInfo, long total, List<String[]> data) {
        this.excelInfo = excelInfo;
        this.total = total;
        this.data = data;
    }

    public ExcelInfo getExcelInfo() {
        return excelInfo;
    }

    public void setExcelInfo(ExcelInfo excelInfo) {
        this.excelInfo = excelInfo;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<String[]> getData() {
        return data;
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }
}

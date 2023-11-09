package com.vrv.vap.data.common.excel.out;


import com.vrv.vap.data.common.excel.ExcelInfo;

import java.io.Serializable;

/**
 * @author lilang
 * @date 2022/3/11
 * @description
 */
public class ProgressInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long startTime;

    /**
     * 为每个导入随机生成id
     */
    private String id;

    /**
     * 总条数
     */
    private float total;

    /**
     * 写入ExcelData条数
     */
    private Integer writeCount;

    /**
     * 已读取的数据条数
     */
    private Integer readCount;

    private boolean finish;

    private float process;

    private ExcelInfo firstExcelInfo;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Integer getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(Integer writeCount) {
        this.writeCount = writeCount;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public float getProcess() {
        return process;
    }

    public void setProcess(float process) {
        this.process = process;
    }

    public ExcelInfo getFirstExcelInfo() {
        return firstExcelInfo;
    }

    public void setFirstExcelInfo(ExcelInfo firstExcelInfo) {
        this.firstExcelInfo = firstExcelInfo;
    }
}

package com.vrv.vap.xc.tools.zip;

import java.util.List;

public class ZipData {
    private List<ZipInfo> zipInfo;
    private long total;
    private String zipFileName;
    private String zipFilePath;
    private List<String[]> data;

    public ZipData(List<ZipInfo> zipInfo, long total, List<String[]> data) {
        this.zipInfo = zipInfo;
        this.total = total;
        this.data = data;
    }

    public ZipData(List<ZipInfo> zipInfo, long total, String zipFilePath) {
        this.zipInfo = zipInfo;
        this.total = total;
        this.zipFilePath = zipFilePath;
    }

    public ZipData(List<ZipInfo> zipInfo, long total, String zipFileName, String zipFilePath) {
        this.zipInfo = zipInfo;
        this.total = total;
        this.zipFileName = zipFileName;
        this.zipFilePath = zipFilePath;
    }

    public List<ZipInfo> getZipInfo() {
        return zipInfo;
    }

    public void setZipInfo(List<ZipInfo> zipInfo) {
        this.zipInfo = zipInfo;
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

    public String getZipFilePath() {
        return zipFilePath;
    }

    public void setZipFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }
}

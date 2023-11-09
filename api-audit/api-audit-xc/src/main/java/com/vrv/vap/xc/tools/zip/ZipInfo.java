package com.vrv.vap.xc.tools.zip;

/**
 * Created by Administrator on 2018/4/24.
 */
public class ZipInfo {
    private String name;
    private String fileType;
    private String filePath;
    private byte[] data;

    public ZipInfo(String name, String fileType, byte[] data, String filePath) {
        this.name = name;
        this.fileType = fileType;
        this.data = data;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

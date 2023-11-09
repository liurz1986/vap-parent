package com.vrv.vap.admin.vo;

public class BackupOutVO {
    private String filePath;
    private String fileName;
    private String fileMD5;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }
}

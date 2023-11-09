package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "file_md5")
public class FileMd5 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_id")
    @ApiModelProperty("文件ID")
    private  String fileId;

    @Column(name = "file_type")
    @ApiModelProperty("文件类型")
    private  String fileType;

    @ApiModelProperty("文件MD5")
    private  String md5;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}

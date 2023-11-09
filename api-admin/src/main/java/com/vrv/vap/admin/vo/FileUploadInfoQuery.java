package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


public class FileUploadInfoQuery extends Query {

    @ApiModelProperty(value="上传文件标识")
    private String fileId;

    @ApiModelProperty(value="上传文件名称")
    private  String fileName;

    @ApiModelProperty(value="上传文件类型")
    private  String fileType;

    @ApiModelProperty(value="上传文件地址")
    private  String filePath;

    @ApiModelProperty(value="命名空间")
    private String namespace;

    @ApiModelProperty(value="上传文件信息")
    private  String msg;

    @ApiModelProperty(value="缩略图id")
    private  String thumbMediaId;

    @ApiModelProperty(value="用户id")
    private  Integer userId;

    @ApiModelProperty(value="用户名称")
    private String userName;

    @ApiModelProperty(value="文件上传类型   0是本地上传  1是Fastdfs上传")
    private Integer uploadType;

    @ApiModelProperty("开始时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "start_time", access = Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date myStartTime;

    @ApiModelProperty("结束时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "end_time", access = Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date myEndTime;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUploadType() {
        return uploadType;
    }

    public void setUploadType(Integer uploadType) {
        this.uploadType = uploadType;
    }

    public Date getMyStartTime() {
        return myStartTime;
    }

    public void setMyStartTime(Date myStartTime) {
        this.myStartTime = myStartTime;
    }

    public Date getMyEndTime() {
        return myEndTime;
    }

    public void setMyEndTime(Date myEndTime) {
        this.myEndTime = myEndTime;
    }
}

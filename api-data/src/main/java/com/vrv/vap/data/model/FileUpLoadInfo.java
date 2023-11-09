package com.vrv.vap.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.syslog.common.annotation.LogField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUpLoadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_id")
    @ApiModelProperty(value = "文件ID")
    private String fileId;

    @Column(name = "file_name")
    @ApiModelProperty(value = "文件名称")
    private  String fileName;

    @Column(name = "file_type")
    @ApiModelProperty(value = "文件类型")
    private  String fileType;

    @Column(name = "file_path")
    @ApiModelProperty(value = "文件路径")
    private  String filePath;

    private String namespace;

    private  String msg;

    @Column(name = "thumb_media_id")
    private  String thumbMediaId;

    @Column(name = "user_id")
    @ApiModelProperty(value = "用户id")
    private  Integer userId;

    @Column(name = "user_name")
    @ApiModelProperty(value = "用户名称")
    private String userName;

    @Column(name = "upload_type")
    @ApiModelProperty(value = "上传类型")
    @LogField(name = "uploadType", description = "上传类型")
    private Integer uploadType;

    @Column(name="create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    private Date createTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.common.annotation.LogColumn;
import com.vrv.vap.syslog.common.annotation.LogField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Builder
@Data
@Table(name = "file_upload_info")
@NoArgsConstructor
@AllArgsConstructor
public class FileUpLoadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_id")
    @ApiModelProperty("文件ID")
    private String fileId;

    @Column(name = "file_name")
    @ApiModelProperty("文件名称")
    private  String fileName;

    @Column(name = "file_type")
    @ApiModelProperty("文件类型")
    private  String fileType;

    @Column(name = "file_path")
    @ApiModelProperty("文件路径")
    private  String filePath;

    @ApiModelProperty("命名空间")
    private String namespace;

    @ApiModelProperty("上传文件信息")
    private  String msg;

    @Column(name = "thumb_media_id")
    @ApiModelProperty("缩略图ID")
    private  String thumbMediaId;

    @Column(name = "user_id")
    @ApiModelProperty("用户ID")
    private  Integer userId;

    @Column(name = "user_name")
    @ApiModelProperty("用户名")
    private String userName;

    @Column(name = "upload_type")
    @ApiModelProperty("上传类型")
    @LogField(name = "uploadType", description = "上传类型")
    @LogColumn(mapping = "{\"0\":\"本地地址\",\"1\":\"Fastdfs地址\"}")
    private Integer uploadType;

    @Column(name="create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    @ApiModelProperty("创建时间")
    private Date createTime;
}

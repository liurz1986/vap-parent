package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 点对点任务表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-24
 */
@ApiModel(value="ClientPointTask对象", description="点对点任务表")
public class ClientPointTaskQuery extends Query {


    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "设备id")
    private String devId;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "pki")
    private String pki;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "内容")
    private String content;

    private String fileCrc;

    @ApiModelProperty(value = "处理状态")
    private String status;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "fastdfs中保存的地址")
    private String filePath;

    @ApiModelProperty(value = "点对点指令类型:01截屏,02录屏,03进程,04网络,05软件,06插入pki,07拔出PKI,08开机,09关机,10打印,11日志,12锁屏/休眠")
    private String type;

    private Long fileLength;

    private String logType;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String errorInfo;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String contentDesc;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private List<String> types;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private List<String> limitTypes;

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getLimitTypes() {
        return limitTypes;
    }

    public void setLimitTypes(List<String> limitTypes) {
        this.limitTypes = limitTypes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getPki() {
        return pki;
    }

    public void setPki(String pki) {
        this.pki = pki;
    }
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getFileCrc() {
        return fileCrc;
    }

    public void setFileCrc(String fileCrc) {
        this.fileCrc = fileCrc;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }
    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    @Override
    public String toString() {
        return "ClientPointTask{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", devId=" + devId +
            ", ip=" + ip +
            ", pki=" + pki +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", content=" + content +
            ", fileCrc=" + fileCrc +
            ", status=" + status +
            ", fileName=" + fileName +
            ", filePath=" + filePath +
            ", type=" + type +
            ", fileLength=" + fileLength +
            ", logType=" + logType +
        "}";
    }
}

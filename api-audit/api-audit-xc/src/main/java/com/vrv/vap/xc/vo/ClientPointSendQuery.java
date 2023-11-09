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
 * 点对点发送任务表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="ClientPointSend对象", description="点对点发送任务表")
public class ClientPointSendQuery extends Query {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    private String devId;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "pki")
    private String pki;

    @ApiModelProperty(value = "开始时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.MORE_THAN)
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LESS_THAN)
    private Date endTime;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "状态0：成功 1：运行中，2：指令重复，3：接收超时，4：发送超时")
    private Integer status;

    @ApiModelProperty(value = "错误信息描述")
    private String errorInfo;

    @ApiModelProperty(value = "指令类型 01：抓屏，02：录屏，03：进程，04：网络状态，05：软件列表")
    private String type;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "发送状态 0 未发送，1 已发送")
    private Integer sendState;

    @ApiModelProperty(value = "审批状态 0 待审批；1审批成功；2审批失败；")
    private String approvalStatus;

    @ApiModelProperty(value = "是否同步：0未同步1已同步")
    private Integer synchronize;

    @ApiModelProperty(value = "中间件id")
    private String middleId;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String contentDesc;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String contentDescDetail;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private Integer percentProccess;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private Integer compeleteProccess;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private Integer totalProccess;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private List<String> limitTypes;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String notApprovalStatus;

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public String getContentDescDetail() {
        return contentDescDetail;
    }

    public void setContentDescDetail(String contentDescDetail) {
        this.contentDescDetail = contentDescDetail;
    }

    public Integer getPercentProccess() {
        return percentProccess;
    }

    public void setPercentProccess(Integer percentProccess) {
        this.percentProccess = percentProccess;
    }

    public Integer getCompeleteProccess() {
        return compeleteProccess;
    }

    public void setCompeleteProccess(Integer compeleteProccess) {
        this.compeleteProccess = compeleteProccess;
    }

    public Integer getTotalProccess() {
        return totalProccess;
    }

    public void setTotalProccess(Integer totalProccess) {
        this.totalProccess = totalProccess;
    }

    public List<String> getLimitTypes() {
        return limitTypes;
    }

    public void setLimitTypes(List<String> limitTypes) {
        this.limitTypes = limitTypes;
    }

    public String getNotApprovalStatus() {
        return notApprovalStatus;
    }

    public void setNotApprovalStatus(String notApprovalStatus) {
        this.notApprovalStatus = notApprovalStatus;
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
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    public Integer getSendState() {
        return sendState;
    }

    public void setSendState(Integer sendState) {
        this.sendState = sendState;
    }
    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    public Integer getSynchronize() {
        return synchronize;
    }

    public void setSynchronize(Integer synchronize) {
        this.synchronize = synchronize;
    }
    public String getMiddleId() {
        return middleId;
    }

    public void setMiddleId(String middleId) {
        this.middleId = middleId;
    }

    @Override
    public String toString() {
        return "ClientPointSend{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", devId=" + devId +
            ", ip=" + ip +
            ", pki=" + pki +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", content=" + content +
            ", status=" + status +
            ", errorInfo=" + errorInfo +
            ", type=" + type +
            ", createTime=" + createTime +
            ", creator=" + creator +
            ", sendState=" + sendState +
            ", approvalStatus=" + approvalStatus +
            ", synchronize=" + synchronize +
            ", middleId=" + middleId +
        "}";
    }
}

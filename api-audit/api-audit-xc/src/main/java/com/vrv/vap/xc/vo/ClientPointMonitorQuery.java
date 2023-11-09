package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-24
 */
@ApiModel(value="ClientPointMonitor对象", description="")
public class ClientPointMonitorQuery extends Query {

    private String id;

    @ApiModelProperty(value = "IP或PKI")
    private String objId;

    @ApiModelProperty(value = "类型  01 监控机器 ；02  监控人员；")
    private String type;

    @ApiModelProperty(value = "在线状态 0 离线；1 在线；")
    private String onlineState;

    @ApiModelProperty(value = "采集状态 0 未开始；1 正在取证；2 取证完成；")
    private String collectState;

    @ApiModelProperty(value = "监控状态 0 历史关注；1 当前关注；")
    private String monitorState;

    @ApiModelProperty(value = "开始关注时间")
    private Date createTime;

    @ApiModelProperty(value = "取消关注时间")
    private Date endTime;

    @ApiModelProperty(value = "采集配置项")
    private String content;

    @ApiModelProperty(value = "操作人")
    private String operator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(String onlineState) {
        this.onlineState = onlineState;
    }
    public String getCollectState() {
        return collectState;
    }

    public void setCollectState(String collectState) {
        this.collectState = collectState;
    }
    public String getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(String monitorState) {
        this.monitorState = monitorState;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "ClientPointMonitor{" +
            "id=" + id +
            ", objId=" + objId +
            ", type=" + type +
            ", onlineState=" + onlineState +
            ", collectState=" + collectState +
            ", monitorState=" + monitorState +
            ", createTime=" + createTime +
            ", endTime=" + endTime +
            ", content=" + content +
            ", operator=" + operator +
        "}";
    }
}

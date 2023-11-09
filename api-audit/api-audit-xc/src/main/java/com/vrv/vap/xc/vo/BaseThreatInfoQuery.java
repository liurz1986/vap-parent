package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-05-27
 */
@ApiModel(value="BaseThreatInfo对象", description="")
public class BaseThreatInfoQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer sample;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "来源")
    private String source;

    @ApiModelProperty(value = "威胁的二进制值")
    private byte[] uuid;

    @ApiModelProperty(value = "威胁的发现（入库）时间")
    private Date date;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "状态 0 启动  1 停止 ")
    private Integer status;

    @ApiModelProperty(value = "类型 0 手动添加 1 导入")
    private Integer othertype;

    private Long fearturestype;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getSample() {
        return sample;
    }

    public void setSample(Integer sample) {
        this.sample = sample;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getOthertype() {
        return othertype;
    }

    public void setOthertype(Integer othertype) {
        this.othertype = othertype;
    }
    public Long getFearturestype() {
        return fearturestype;
    }

    public void setFearturestype(Long fearturestype) {
        this.fearturestype = fearturestype;
    }

    @Override
    public String toString() {
        return "BaseThreatInfoQuery{" +
            "id=" + id +
            ", sample=" + sample +
            ", description=" + description +
            ", source=" + source +
            ", uuid=" + uuid +
            ", date=" + date +
            ", remarks=" + remarks +
            ", status=" + status +
            ", othertype=" + othertype +
            ", fearturestype=" + fearturestype +
        "}";
    }
}

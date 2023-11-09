package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 恶意行为详情
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="MaliciousBehaviorDetectionContent对象", description="恶意行为详情")
public class MaliciousBehaviorDetectionContentQuery extends Query {

@ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标识同一个content")
    private String uuid;

    @ApiModelProperty(value = "4663文件相关操作、4657注册表相关操作、4688进程相关操作")
    private Integer eventId;

    @ApiModelProperty(value = "文件操作类型：1写操作、2、执行操作3、删除操作")
    private Integer fileOptType;

    @ApiModelProperty(value = "新的进程名称，创建进程操作时该字段才有值")
    private String newProcName;

    @ApiModelProperty(value = "操作对象")
    private String objectName;

    @ApiModelProperty(value = "操作对象的类型")
    private String objectType;

    @ApiModelProperty(value = "操作对象类型")
    private String objectValueName;

    @ApiModelProperty(value = "当前进程全路径")
    private String pName;

    @ApiModelProperty(value = "当前进程ID")
    private Integer pId;

    @ApiModelProperty(value = "注册表操作类型，1创建、2修改、3删除")
    private Integer regOptType;

    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    public Integer getFileOptType() {
        return fileOptType;
    }

    public void setFileOptType(Integer fileOptType) {
        this.fileOptType = fileOptType;
    }
    public String getNewProcName() {
        return newProcName;
    }

    public void setNewProcName(String newProcName) {
        this.newProcName = newProcName;
    }
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    public String getObjectValueName() {
        return objectValueName;
    }

    public void setObjectValueName(String objectValueName) {
        this.objectValueName = objectValueName;
    }
    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }
    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }
    public Integer getRegOptType() {
        return regOptType;
    }

    public void setRegOptType(Integer regOptType) {
        this.regOptType = regOptType;
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MaliciousBehaviorDetectionContent{" +
            "id=" + id +
            ", uuid=" + uuid +
            ", eventId=" + eventId +
            ", fileOptType=" + fileOptType +
            ", newProcName=" + newProcName +
            ", objectName=" + objectName +
            ", objectType=" + objectType +
            ", objectValueName=" + objectValueName +
            ", pName=" + pName +
            ", pId=" + pId +
            ", regOptType=" + regOptType +
            ", time=" + time +
        "}";
    }
}

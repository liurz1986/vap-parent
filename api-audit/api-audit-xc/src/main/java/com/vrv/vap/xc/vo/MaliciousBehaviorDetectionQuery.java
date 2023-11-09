package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 恶意行为检测
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="MaliciousBehaviorDetection对象", description="恶意行为检测")
public class MaliciousBehaviorDetectionQuery extends Query {

@ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "计算机名")
    private String computerName;

    @ApiModelProperty(value = "计算机IP")
    private String computerIp;

    @ApiModelProperty(value = "日志用途类型，1用于恶意行为检测、2用于僵尸程序检测")
    private Integer usedType;

    @ApiModelProperty(value = "标识同一个content")
    private String uuid;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "判断结果，0正常、1存在恶意行为")
    private Integer result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }
    public String getComputerIp() {
        return computerIp;
    }

    public void setComputerIp(String computerIp) {
        this.computerIp = computerIp;
    }
    public Integer getUsedType() {
        return usedType;
    }

    public void setUsedType(Integer usedType) {
        this.usedType = usedType;
    }
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "MaliciousBehaviorDetection{" +
            "id=" + id +
            ", computerName=" + computerName +
            ", computerIp=" + computerIp +
            ", usedType=" + usedType +
            ", uuid=" + uuid +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", result=" + result +
        "}";
    }
}

package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.vrv.vap.toolkit.vo.Query;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
@ApiModel(value="RptUserLoginHis对象", description="")
public class RptUserLoginHis extends Query {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "keyid")
    private String keyId;

    @ApiModelProperty(value = "终端ip")
    private String devIp;

    @ApiModelProperty(value = "时间段，小时数")
    @TableField(value = "login_hour")
    private Integer hour;

    @ApiModelProperty(value = "登录次数")
    @TableField(value = "login_count")
    private Integer count;

    @ApiModelProperty(value = "数据日期")
    private String dataTime;

    @ApiModelProperty(value = "入库时间")
    private LocalDateTime insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }
    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "RptUserLoginHis{" +
            "id=" + id +
            ", keyId=" + keyId +
            ", devIp=" + devIp +
            ", hour=" + hour +
            ", count=" + count +
            ", dataTime=" + dataTime +
            ", insertTime=" + insertTime +
        "}";
    }
}

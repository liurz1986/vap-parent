package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
@ApiModel(value="RptUserLogin对象", description="")
public class RptUserLogin extends Query {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdateTime;

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

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "RptUserLogin{" +
            "keyId=" + keyId +
            ", devIp=" + devIp +
            ", hour=" + hour +
            ", count=" + count +
        "}";
    }
}

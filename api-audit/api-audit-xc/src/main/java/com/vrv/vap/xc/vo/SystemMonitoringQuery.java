package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 系统监控信息
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-18
 */
@ApiModel(value="SystemMonitoring对象", description="系统监控信息")
public class SystemMonitoringQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "系统名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String sysName;

    @ApiModelProperty(value = "地址")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String sysUrl;

    @ApiModelProperty(value = "状态:0 ping不通 1 ping通")
    private Integer sysState;

    @ApiModelProperty(value = "添加时间")
    private Date insertTime;

    @ApiModelProperty(value = "最近一次ping的时间")
    private Date lastPingTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }
    public String getSysUrl() {
        return sysUrl;
    }

    public void setSysUrl(String sysUrl) {
        this.sysUrl = sysUrl;
    }
    public Integer getSysState() {
        return sysState;
    }

    public void setSysState(Integer sysState) {
        this.sysState = sysState;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    public Date getLastPingTime() {
        return lastPingTime;
    }

    public void setLastPingTime(Date lastPingTime) {
        this.lastPingTime = lastPingTime;
    }

    @Override
    public String toString() {
        return "SystemMonitoring{" +
            "id=" + id +
            ", sysName=" + sysName +
            ", sysUrl=" + sysUrl +
            ", sysState=" + sysState +
            ", insertTime=" + insertTime +
            ", lastPingTime=" + lastPingTime +
        "}";
    }
}

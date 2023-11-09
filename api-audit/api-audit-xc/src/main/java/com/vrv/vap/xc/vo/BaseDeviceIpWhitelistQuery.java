package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2021-05-25
 */
@ApiModel(value="BaseDeviceIpWhitelist对象", description="")
public class BaseDeviceIpWhitelistQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "基础设备ID")
    @TableField("deviceId")
    private Integer deviceid;

    private String ip;

    @ApiModelProperty(value = "1 未注册设备预警白名单; 2 网络会话分析-源TOP-源IP;3 数据泄露设备白名单;4 网络会话分析-目标TOP-目标IP;5 网络会话分析-端口TOP-源IP;6 网络会话分析-端口TOP-端口;7 网络攻击分析-源IP;8 网络攻击分析-目标IP;9 网络会话预警-源IP;10 网络会话预警-目标IP;11 设备风险排行白名单")
    private String type;

    @TableField("lastUpdateTime")
    private Date lastupdatetime;

    @ApiModelProperty(value = "备注")
    private String descrption;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(Integer deviceid) {
        this.deviceid = deviceid;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Date getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(Date lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }
    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption;
    }

    @Override
    public String toString() {
        return "BaseDeviceIpWhitelist{" +
            "id=" + id +
            ", deviceid=" + deviceid +
            ", ip=" + ip +
            ", type=" + type +
            ", lastupdatetime=" + lastupdatetime +
            ", descrption=" + descrption +
        "}";
    }
}

package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 僵尸程序详情
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="BotDetectionContent对象", description="僵尸程序详情")
public class BotDetectionContentQuery extends Query {

@ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标识同一个content")
    private String uuid;

    @ApiModelProperty(value = "当前进程ID")
    private Integer pId;

    @ApiModelProperty(value = "当前进程全路径")
    private String pName;

    @ApiModelProperty(value = "访问的IP地址")
    private String ip;

    @ApiModelProperty(value = "访问的URL地址")
    private String url;

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
    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }
    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "BotDetectionContent{" +
            "id=" + id +
            ", uuid=" + uuid +
            ", pId=" + pId +
            ", pName=" + pName +
            ", ip=" + ip +
            ", url=" + url +
            ", time=" + time +
        "}";
    }
}

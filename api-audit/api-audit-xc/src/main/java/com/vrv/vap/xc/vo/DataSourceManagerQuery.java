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
 * 数据源管理
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="DataSourceManager对象", description="数据源管理")
public class DataSourceManagerQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "数据源名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String sourceName;

    @ApiModelProperty(value = "设备地址")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String ip;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "采集协议")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String protocol;

    @ApiModelProperty(value = "描述")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "DataSourceManager{" +
            "id=" + id +
            ", sourceName=" + sourceName +
            ", ip=" + ip +
            ", port=" + port +
            ", protocol=" + protocol +
            ", description=" + description +
            ", createTime=" + createTime +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}

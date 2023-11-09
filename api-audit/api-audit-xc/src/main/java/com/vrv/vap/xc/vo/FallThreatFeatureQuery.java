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
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="FallThreatFeature对象", description="")
public class FallThreatFeatureQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "威胁名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String threatName;

    @ApiModelProperty(value = "端口")
    private String port;

    @ApiModelProperty(value = "进程")
    private String process;

    @ApiModelProperty(value = "url")
    private String url;

    @ApiModelProperty(value = "满足条件个数")
    private String count;

    @ApiModelProperty(value = "满足级别1,2,3,4")
    private String portLeve;

    @ApiModelProperty(value = "满足级别1,2,3,4")
    private String processLeve;

    @ApiModelProperty(value = "满足级别1,2,3,4")
    private String urlLeve;

    @ApiModelProperty(value = "满足级别1,2,3,4")
    private String patchLeve;

    @ApiModelProperty(value = "关联补丁")
    private String relatePatch;

    @ApiModelProperty(value = "序列号")
    private String code;

    private Date greatTime;

    @ApiModelProperty(value = "级别 高0，中1，低2")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getThreatName() {
        return threatName;
    }

    public void setThreatName(String threatName) {
        this.threatName = threatName;
    }
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
    public String getPortLeve() {
        return portLeve;
    }

    public void setPortLeve(String portLeve) {
        this.portLeve = portLeve;
    }
    public String getProcessLeve() {
        return processLeve;
    }

    public void setProcessLeve(String processLeve) {
        this.processLeve = processLeve;
    }
    public String getUrlLeve() {
        return urlLeve;
    }

    public void setUrlLeve(String urlLeve) {
        this.urlLeve = urlLeve;
    }
    public String getPatchLeve() {
        return patchLeve;
    }

    public void setPatchLeve(String patchLeve) {
        this.patchLeve = patchLeve;
    }
    public String getRelatePatch() {
        return relatePatch;
    }

    public void setRelatePatch(String relatePatch) {
        this.relatePatch = relatePatch;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public Date getGreatTime() {
        return greatTime;
    }

    public void setGreatTime(Date greatTime) {
        this.greatTime = greatTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "FallThreatFeature{" +
            "id=" + id +
            ", threatName=" + threatName +
            ", port=" + port +
            ", process=" + process +
            ", url=" + url +
            ", count=" + count +
            ", portLeve=" + portLeve +
            ", processLeve=" + processLeve +
            ", urlLeve=" + urlLeve +
            ", patchLeve=" + patchLeve +
            ", relatePatch=" + relatePatch +
            ", code=" + code +
            ", greatTime=" + greatTime +
            ", updateTime=" + updateTime +
        "}";
    }
}

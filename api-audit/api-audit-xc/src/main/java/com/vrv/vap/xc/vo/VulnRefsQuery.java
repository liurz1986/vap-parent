package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.vrv.vap.toolkit.vo.Query;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-18
 */
@ApiModel(value="VulnRefs对象", description="")
public class VulnRefsQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "漏洞唯一标识，与vuln_info中同字段关联")
    private String vulnIdentity;

    @ApiModelProperty(value = "参考来源")
    private String refSource;

    @ApiModelProperty(value = "名称")
    private String refName;

    @ApiModelProperty(value = "参考链接")
    private String refUrl;

    private Date insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getVulnIdentity() {
        return vulnIdentity;
    }

    public void setVulnIdentity(String vulnIdentity) {
        this.vulnIdentity = vulnIdentity;
    }
    public String getRefSource() {
        return refSource;
    }

    public void setRefSource(String refSource) {
        this.refSource = refSource;
    }
    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }
    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "VulnRefsQuery{" +
            "id=" + id +
            ", vulnIdentity=" + vulnIdentity +
            ", refSource=" + refSource +
            ", refName=" + refName +
            ", refUrl=" + refUrl +
            ", insertTime=" + insertTime +
        "}";
    }
}

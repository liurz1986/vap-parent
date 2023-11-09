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
@ApiModel(value="VulnerableConfiguration对象", description="")
public class VulnerableConfigurationQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "漏洞唯一标识，与vuln_info表同字段关联")
    private String vulnIdentity;

    @ApiModelProperty(value = "厂商")
    private String corporation;

    @ApiModelProperty(value = "产品名称")
    private String product;

    @ApiModelProperty(value = "版本")
    private String version;

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
    public String getCorporation() {
        return corporation;
    }

    public void setCorporation(String corporation) {
        this.corporation = corporation;
    }
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "VulnerableConfigurationQuery{" +
            "id=" + id +
            ", vulnIdentity=" + vulnIdentity +
            ", corporation=" + corporation +
            ", product=" + product +
            ", version=" + version +
            ", insertTime=" + insertTime +
        "}";
    }
}

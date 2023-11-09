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
 * @since 2021-05-19
 */
@ApiModel(value="ReportSecurityPersonWhitelist对象", description="")
public class ReportSecurityPersonWhitelistQuery extends Query {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String userName;

    @ApiModelProperty(value = "身份证号")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String idCard;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "机构编码")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String orgCode;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "区域编码")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String areaCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "ReportSecurityPersonWhitelist{" +
            "id=" + id +
            ", userName=" + userName +
            ", idCard=" + idCard +
            ", orgName=" + orgName +
            ", orgCode=" + orgCode +
            ", createTime=" + createTime +
            ", areaCode=" + areaCode +
        "}";
    }
}

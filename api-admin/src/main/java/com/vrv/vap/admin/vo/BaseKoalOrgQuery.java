package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import java.util.Date;

@ApiModel("BaseKoalOrgQuery组织机构分页查询参数")
public class BaseKoalOrgQuery extends Query {


    /**
     * 机构编码
     */
    @ApiModelProperty("机构编码")
    private String code;

    /**
     * 上级机构编码
     * */
    @ApiModelProperty("上级机构编码")
    private String parentCode;

    /**
     * 机构类型
     */
    @ApiModelProperty("机构类型")
    private String type;

    /**
     * 机构名称
     */
    @QueryLike
    @ApiModelProperty("机构名称")
    private String name;

    /**
     * 机构简短名称
     */
    @ApiModelProperty("机构简短名称")
    private String shortName;

    /**
     * 机构别名
     */
    @ApiModelProperty("机构别名")
    private String otherName;

    /**
     * 机构状态
     */
    @ApiModelProperty("机构状态")
    private String status;

    /**
     * 旧机构代码
     */
    @ApiModelProperty("旧机构代码")
    private String oldCode;

    /**
     * 启用日期
     */
    @ApiModelProperty("启用日期")
    private String startDate;

    /**
     * 停用日期
     */
    @ApiModelProperty("停用日期")
    private String endDate;

    /**
     * 原机构代码停用日期
     */
    @ApiModelProperty("原机构代码停用日期")
    private String oldCodeEnd;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    private Date updatetime;

    /**
     * 排序，默认为0
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 0： 部 1：省 2：市 3：区，默认为3
     */
    @ApiModelProperty("0： 部 1：省 2：市 3：区，默认为3")
    private Byte orghierarchy;



    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级 0绝密，1机密，2秘密，3内部")
    private Integer secretLevel;

    /**
     * 保密资格-
     */
    @ApiModelProperty("保密资格：1-JG一级；2-JG二级 ")
    private Integer secretQualifications;

    /**
     *  单位类别 1-行政机关、2-事业单位；3-国有企业；4-中央企业
     */
    @ApiModelProperty("单位类别 1-行政机关、2-事业单位；3-国有企业；4-中央企业 ")
    private Integer orgType;


    /**
     *  防护等级
     */
    @ApiModelProperty("防护等级 0绝密，1机密增强，2机密一般，3秘密")
    private Integer protectionLevel;



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getOldCodeEnd() {
        return oldCodeEnd;
    }

    public void setOldCodeEnd(String oldCodeEnd) {
        this.oldCodeEnd = oldCodeEnd;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Byte getOrghierarchy() {
        return orghierarchy;
    }

    public void setOrghierarchy(Byte orghierarchy) {
        this.orghierarchy = orghierarchy;
    }

    public Integer getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }

    public Integer getSecretQualifications() {
        return secretQualifications;
    }

    public void setSecretQualifications(Integer secretQualifications) {
        this.secretQualifications = secretQualifications;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public Integer getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(Integer protectionLevel) {
        this.protectionLevel = protectionLevel;
    }
}

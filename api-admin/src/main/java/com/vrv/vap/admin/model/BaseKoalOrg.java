package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.common.annotation.LogColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("组织机构信息")
@Table(name = "base_koal_org")
public class BaseKoalOrg implements Serializable {

    @Id
    @Column(name = "uu_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uuId;

    /**
     * 机构编码
     */
    @ApiModelProperty("机构编码")
    @Ignore
    private String code;

    @ApiModelProperty("上级机构")
    @Column(name = "parent_code")
    private String parentCode;

    /**
     * 机构类型
     */
    @ApiModelProperty("机构类型")
    @Ignore
    private String type;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String name;

    @Column(name = "short_name")
    @ApiModelProperty("机构缩写")
    private String shortName;

    @Column(name = "other_name")
    @ApiModelProperty("其它机构名称")
    private String otherName;

    @ApiModelProperty("状态")
    @Ignore
    private String status;

    /**
     * 原机构代码
     */
    @ApiModelProperty("原机构代码")
    @Column(name = "old_code")
    @Ignore
    private String oldCode;

    /**
     * 启用日期
     */
    @ApiModelProperty("启用日期")
    @Column(name = "start_date")
    @Ignore
    private String startDate;

    /**
     * 停用日期
     */
    @ApiModelProperty("停用日期")
    @Column(name = "end_date")
    @Ignore
    private String endDate;

    /**
     * 原机构代码停用日期
     */
    @ApiModelProperty("原机构代码停用日期")
    @Column(name = "old_code_end")
    @Ignore
    private String oldCodeEnd;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    @Column(name = "update_time")
    @Ignore
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;

    /**
     * 排序，默认为0
     */
    @ApiModelProperty("排序")
    @Ignore
    private Integer sort;

    /**
     * 0： 部 1：省 2：市 3：区，默认为3
     */
    @ApiModelProperty("0： 部 1：省 2：市 3：区，默认为3")
    @Column(name = "org_hierarchy")
    @Ignore
    private Byte orghierarchy;

    /**
     * 业务线
     */
    @ApiModelProperty("机构所属业务线")
    @Column(name = "business_line")
    @Ignore
    private String businessline;

    /**
     * 层级关系维护码
     */
    @ApiModelProperty("层级关系维护码")
    @Column(name = "sub_code")
    private String subCode;


    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级")
    @Column(name = "secret_level")
    @LogColumn(mapping = "{\"0\":\"绝密\",\"1\":\"机密\",\"2\":\"秘密\",\"3\":\"内部\",\"4\":\"非密\"}")
    private Integer secretLevel;

    /**
     * 保密资格-
     */
    @ApiModelProperty("保密资格")
    @Column(name = "secret_qualifications")
    @LogColumn(mapping = "{\"1\":\"军工一级\",\"2\":\"军工二级\",\"3\":\"非军工\"}")
    private Integer secretQualifications;

    /**
     *  单位类别 1-行政机关、2-事业单位；3-国有企业；4-中央企业
     */
    @ApiModelProperty("单位类别")
    @Column(name = "org_type")
    @LogColumn(mapping = "{\"1\":\"党政机关\",\"2\":\"事业单位\",\"3\":\"国有企业\",\"4\":\"其他单位\"}")
    private Integer orgType;


    /**
     *  防护等级
     */
    @ApiModelProperty("防护等级")
    @Column(name = "protection_level")
    @LogColumn(mapping = "{\"0\":\"绝密\",\"1\":\"机密增强\",\"2\":\"机密一般\",\"3\":\"秘密\"}")
    private Integer protectionLevel;

    /**
     * 外部系统人员表主键
     */
    @ApiModelProperty("外部系统人员表主键")
    @Ignore
    private String uid;

    /**
     * 数据来源
     */
    @ApiModelProperty("数据来源")
    @Ignore
    private String source;

    /**
     * 数据来源类型
     */
    @ApiModelProperty("数据来源类型")
    @Column(name = "data_source_type")
    @Ignore
    private Integer dataSourceType;

    /**
     * @return uu_id
     */
    public Integer getUuId() {
        return uuId;
    }

    /**
     * @param uuId
     */
    public void setUuId(Integer uuId) {
        this.uuId = uuId;
    }

    /**
     * 获取机构编码
     *
     * @return code - 机构编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置机构编码
     *
     * @param code 机构编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return parent_code
     */
    public String getParentCode() {
        return parentCode;
    }

    /**
     * @param parentCode
     */
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    /**
     * 获取机构类型
     *
     * @return type - 机构类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置机构类型
     *
     * @param type 机构类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取机构名称
     *
     * @return name - 机构名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置机构名称
     *
     * @param name 机构名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return short_name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return other_name
     */
    public String getOtherName() {
        return otherName;
    }

    /**
     * @param otherName
     */
    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取原机构代码
     *
     * @return old_code - 原机构代码
     */
    public String getOldCode() {
        return oldCode;
    }

    /**
     * 设置原机构代码
     *
     * @param oldCode 原机构代码
     */
    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    /**
     * 获取启用日期
     *
     * @return start_date - 启用日期
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * 设置启用日期
     *
     * @param startDate 启用日期
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取停用日期
     *
     * @return end_date - 停用日期
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * 设置停用日期
     *
     * @param endDate 停用日期
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * 获取原机构代码停用日期
     *
     * @return old_code_end - 原机构代码停用日期
     */
    public String getOldCodeEnd() {
        return oldCodeEnd;
    }

    /**
     * 设置原机构代码停用日期
     *
     * @param oldCodeEnd 原机构代码停用日期
     */
    public void setOldCodeEnd(String oldCodeEnd) {
        this.oldCodeEnd = oldCodeEnd;
    }

    /**
     * 获取最后更新时间
     *
     * @return updateTime - 最后更新时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置最后更新时间
     *
     * @param updatetime 最后更新时间
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * 获取排序，默认为0
     *
     * @return sort - 排序，默认为0
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序，默认为0
     *
     * @param sort 排序，默认为0
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取0： 部 1：省 2：市 3：区，默认为3
     *
     * @return orgHierarchy - 0： 部 1：省 2：市 3：区，默认为3
     */
    public Byte getOrghierarchy() {
        return orghierarchy;
    }

    /**
     * 设置0： 部 1：省 2：市 3：区，默认为3
     *
     * @param orghierarchy 0： 部 1：省 2：市 3：区，默认为3
     */
    public void setOrghierarchy(Byte orghierarchy) {
        this.orghierarchy = orghierarchy;
    }

    public String getBusinessline() {
        return businessline;
    }

    public void setBusinessline(String businessline) {
        this.businessline = businessline;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
}
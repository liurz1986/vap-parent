package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="ConfRedList对象", description="")
public class ConfRedListQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "民族")
    private String nation;

    @ApiModelProperty(value = "出生年月")
    private LocalDate birthdate;

    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    @ApiModelProperty(value = "籍贯")
    private String navtivePlace;

    @ApiModelProperty(value = "职务")
    private String position;

    @ApiModelProperty(value = "入党时间")
    private String joinPartyTime;

    @ApiModelProperty(value = "毕业院校")
    private String graduateInstitution;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "区域码")
    private String areacode;

    @ApiModelProperty(value = "状态 0-禁用，1-启用")
    private Integer state;

    @ApiModelProperty(value = "类别 1：领导人 2：富豪")
    private Integer classification;

    @ApiModelProperty(value = "描述 ")
    private String description;

    @ApiModelProperty(value = "最后更新时间")
    private LocalDate lastUpdateTime;

    @ApiModelProperty(value = "公司名称")
    private String company;

    @ApiModelProperty(value = "行业")
    private String industry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    public String getNavtivePlace() {
        return navtivePlace;
    }

    public void setNavtivePlace(String navtivePlace) {
        this.navtivePlace = navtivePlace;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public String getJoinPartyTime() {
        return joinPartyTime;
    }

    public void setJoinPartyTime(String joinPartyTime) {
        this.joinPartyTime = joinPartyTime;
    }
    public String getGraduateInstitution() {
        return graduateInstitution;
    }

    public void setGraduateInstitution(String graduateInstitution) {
        this.graduateInstitution = graduateInstitution;
    }
    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public Integer getClassification() {
        return classification;
    }

    public void setClassification(Integer classification) {
        this.classification = classification;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDate getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDate lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    @Override
    public String toString() {
        return "ConfRedList{" +
            "id=" + id +
            ", name=" + name +
            ", gender=" + gender +
            ", nation=" + nation +
            ", birthdate=" + birthdate +
            ", idNumber=" + idNumber +
            ", navtivePlace=" + navtivePlace +
            ", position=" + position +
            ", joinPartyTime=" + joinPartyTime +
            ", graduateInstitution=" + graduateInstitution +
            ", education=" + education +
            ", province=" + province +
            ", areacode=" + areacode +
            ", state=" + state +
            ", classification=" + classification +
            ", description=" + description +
            ", lastUpdateTime=" + lastUpdateTime +
            ", company=" + company +
            ", industry=" + industry +
        "}";
    }
}

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
 * 企业项目人员
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyPerson对象", description="企业项目人员")
public class CompanyPersonQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "关联公司id")
    private Integer companyId;

    @ApiModelProperty(value = "厂家类型")
    private String companyType;

    @ApiModelProperty(value = "姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "身份证")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String idCard;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "毕业院校")
    private String graduatedCollege;

    @ApiModelProperty(value = "专业")
    private String profession;

    @ApiModelProperty(value = "最高学历")
    private String education;

    @ApiModelProperty(value = "政治面貌")
    private String politicsStatus;

    @ApiModelProperty(value = "民族")
    private String nation;

    @ApiModelProperty(value = "联系方式")
    private String tel;

    @ApiModelProperty(value = "户籍")
    private String register;

    @ApiModelProperty(value = "居住地址")
    private String residentialAddress;

    @ApiModelProperty(value = "驾照备案号码(身份证)")
    private String driverNum;

    @ApiModelProperty(value = "电子邮箱")
    private String email;

    @ApiModelProperty(value = "qq")
    private String qq;

    @ApiModelProperty(value = "微信")
    private String weixin;

    @ApiModelProperty(value = "钉钉")
    private String dingding;

    @ApiModelProperty(value = "短视频")
    private String shortVideo;

    @ApiModelProperty(value = "其他工具")
    private String otherChat;

    @ApiModelProperty(value = "状态:在职/离职")
    private String status;

    @ApiModelProperty(value = "是否驻场")
    private String resident;

    @ApiModelProperty(value = "工作地点")
    private String workSite;

    @ApiModelProperty(value = "工作时间")
    private String workTime;

    @ApiModelProperty(value = "是否经过保密安全教育培训")
    private String securityTrain;

    @ApiModelProperty(value = "近三年违法犯罪情况")
    private String illegalCase;

    @ApiModelProperty(value = "是否签订保密协议")
    private String signedSecurity;

    @ApiModelProperty(value = "保密协议扫描件")
    private String securityFile;

    @ApiModelProperty(value = "工作内容")
    private String duty;

    @ApiModelProperty(value = "服务警种")
    private String servicePolice;

    @ApiModelProperty(value = "工作领域")
    private String workDomain;

    @ApiModelProperty(value = "工作性质")
    private String workNature;

    @ApiModelProperty(value = "工作类型")
    private String workType;

    @ApiModelProperty(value = "头像")
    private String pic;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作人所在机构")
    private String operatorOrg;

    @ApiModelProperty(value = "录入人员所属角色")
    private String operatorRole;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public String getGraduatedCollege() {
        return graduatedCollege;
    }

    public void setGraduatedCollege(String graduatedCollege) {
        this.graduatedCollege = graduatedCollege;
    }
    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
    public String getPoliticsStatus() {
        return politicsStatus;
    }

    public void setPoliticsStatus(String politicsStatus) {
        this.politicsStatus = politicsStatus;
    }
    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }
    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }
    public String getDriverNum() {
        return driverNum;
    }

    public void setDriverNum(String driverNum) {
        this.driverNum = driverNum;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }
    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }
    public String getDingding() {
        return dingding;
    }

    public void setDingding(String dingding) {
        this.dingding = dingding;
    }
    public String getShortVideo() {
        return shortVideo;
    }

    public void setShortVideo(String shortVideo) {
        this.shortVideo = shortVideo;
    }
    public String getOtherChat() {
        return otherChat;
    }

    public void setOtherChat(String otherChat) {
        this.otherChat = otherChat;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getResident() {
        return resident;
    }

    public void setResident(String resident) {
        this.resident = resident;
    }
    public String getWorkSite() {
        return workSite;
    }

    public void setWorkSite(String workSite) {
        this.workSite = workSite;
    }
    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }
    public String getSecurityTrain() {
        return securityTrain;
    }

    public void setSecurityTrain(String securityTrain) {
        this.securityTrain = securityTrain;
    }
    public String getIllegalCase() {
        return illegalCase;
    }

    public void setIllegalCase(String illegalCase) {
        this.illegalCase = illegalCase;
    }
    public String getSignedSecurity() {
        return signedSecurity;
    }

    public void setSignedSecurity(String signedSecurity) {
        this.signedSecurity = signedSecurity;
    }
    public String getSecurityFile() {
        return securityFile;
    }

    public void setSecurityFile(String securityFile) {
        this.securityFile = securityFile;
    }
    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
    public String getServicePolice() {
        return servicePolice;
    }

    public void setServicePolice(String servicePolice) {
        this.servicePolice = servicePolice;
    }
    public String getWorkDomain() {
        return workDomain;
    }

    public void setWorkDomain(String workDomain) {
        this.workDomain = workDomain;
    }
    public String getWorkNature() {
        return workNature;
    }

    public void setWorkNature(String workNature) {
        this.workNature = workNature;
    }
    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }
    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getOperatorOrg() {
        return operatorOrg;
    }

    public void setOperatorOrg(String operatorOrg) {
        this.operatorOrg = operatorOrg;
    }
    public String getOperatorRole() {
        return operatorRole;
    }

    public void setOperatorRole(String operatorRole) {
        this.operatorRole = operatorRole;
    }
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "CompanyPerson{" +
            "id=" + id +
            ", projectId=" + projectId +
            ", companyId=" + companyId +
            ", companyType=" + companyType +
            ", name=" + name +
            ", idCard=" + idCard +
            ", sex=" + sex +
            ", position=" + position +
            ", graduatedCollege=" + graduatedCollege +
            ", profession=" + profession +
            ", education=" + education +
            ", politicsStatus=" + politicsStatus +
            ", nation=" + nation +
            ", tel=" + tel +
            ", register=" + register +
            ", residentialAddress=" + residentialAddress +
            ", driverNum=" + driverNum +
            ", email=" + email +
            ", qq=" + qq +
            ", weixin=" + weixin +
            ", dingding=" + dingding +
            ", shortVideo=" + shortVideo +
            ", otherChat=" + otherChat +
            ", status=" + status +
            ", resident=" + resident +
            ", workSite=" + workSite +
            ", workTime=" + workTime +
            ", securityTrain=" + securityTrain +
            ", illegalCase=" + illegalCase +
            ", signedSecurity=" + signedSecurity +
            ", securityFile=" + securityFile +
            ", duty=" + duty +
            ", servicePolice=" + servicePolice +
            ", workDomain=" + workDomain +
            ", workNature=" + workNature +
            ", workType=" + workType +
            ", pic=" + pic +
            ", operator=" + operator +
            ", operatorOrg=" + operatorOrg +
            ", operatorRole=" + operatorRole +
            ", operateTime=" + operateTime +
        "}";
    }
}

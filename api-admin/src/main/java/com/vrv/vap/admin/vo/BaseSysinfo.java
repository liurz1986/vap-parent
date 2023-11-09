package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class BaseSysinfo {

    /**
     *   主键 主键/NOT NULL/自增长
     */
    @ApiModelProperty("主键 主键/NOT NULL/自增长")
    private Integer id;

    /**
     *   系统名称
     */
    @ApiModelProperty("系统名称")
    private String name;

    /**
     *   系统域名
     */
    @ApiModelProperty("系统域名")
    private String domain;

    /**
     *   所属部门
     */
    @ApiModelProperty("所属部门")
    private String orgCode;

    /**
     *   所属区域
     */
    @ApiModelProperty("所属区域")
    private String areaCode;

    /**
     *   网站站点类别
     */
    @ApiModelProperty("网站站点类别")
    private Integer siteTypeId;

    /**
     *   登陆安全级别
     */
    @ApiModelProperty("登陆安全级别")
    private Integer securityLevelId;

    /**
     *   业务分类
     */
    @ApiModelProperty("业务分类")
    private String appTypeId;

    /**
     *   网站负责人PKI证书号
     */
    @ApiModelProperty("网站负责人PKI证书号")
    private String certsn;

    /**
     *   是否启用分析
     */
    @ApiModelProperty("是否启用分析")
    private Integer isAnalysis;

    /**
     *   标签
     */
    @ApiModelProperty("标签")
    private String tags;

    /**
     *   重要等级
     */
    @ApiModelProperty("重要等级")
    private Integer importantTypeId;

    /**
     *   警种
     */
    @ApiModelProperty("警种")
    private String policeTypeId;

    /**
     *   科室名称
     */
    @ApiModelProperty("科室名称")
    private String office;

    /**
     *   网站负责人
     */
    @ApiModelProperty("网站负责人")
    private String userName;

    /**
     *   联系电话
     */
    @ApiModelProperty("联系电话")
    private String tel;

    /**
     *   邮箱地址
     */
    @ApiModelProperty("邮箱地址")
    private String email;

    /**
     *   接入方式为其他的信息
     */
    @ApiModelProperty("接入方式为其他的信息")
    private String other;

    /**
     *   边界设备IP
     */
    @ApiModelProperty("边界设备IP")
    private String boundaryIp;

    /**
     *   边界设备信息ID
     */
    @ApiModelProperty("边界设备信息ID")
    private Integer boundaryId;

    /**
     *   接入方式 0-其他 1-边界设备
     */
    @ApiModelProperty("接入方式 0-其他 1-边界设备")
    private Integer accessType;

    /**
     *   是否与其他网络交换数据 0-否  1-是
     */
    @ApiModelProperty("是否与其他网络交换数据 0-否  1-是")
    private Integer isOtherTrans;

    /**
     *   归类时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("归类时间")
    private Date addTime;

    /**
     *   修改时间 数据更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("修改时间 数据更新时间")
    private Date updateTime;

    /**
     *   系统编号 对应巨龙应用系统编号
     */
    @ApiModelProperty("系统编号 对应巨龙应用系统编号")
    private String systemId;

    /**
     *   系统等级
     */
    @ApiModelProperty("系统等级")
    private String sysLevel;

    /**
     *   系统状态
     */
    @ApiModelProperty("系统状态")
    private String sysState;

    /**
     *   系统类别
     */
    @ApiModelProperty("系统类别")
    private String sysType;

    /**
     *   信息应用域
     */
    @ApiModelProperty("信息应用域")
    private String infoScope;

    /**
     *   开发语言
     */
    @ApiModelProperty("开发语言")
    private String developLanguage;

    /**
     *   系统架构
     */
    @ApiModelProperty("系统架构")
    private String sysFramework;

    /**
     *   所使用的第三方插件
     */
    @ApiModelProperty("所使用的第三方插件")
    private String otherPlugin;

    /**
     *   所需要的外接设备
     */
    @ApiModelProperty("所需要的外接设备")
    private String externalDevice;

    /**
     *   外接设备型号（厂家、版本）
     */
    @ApiModelProperty("外接设备型号（厂家、版本）")
    private String extDeviceInfo;

    /**
     *   浏览器及版本号
     */
    @ApiModelProperty("浏览器及版本号")
    private String browserInfo;

    /**
     *   账户类型
     */
    @ApiModelProperty("账户类型")
    private String accountType;

    /**
     *   个人用户登录方式
     */
    @ApiModelProperty("个人用户登录方式")
    private String userLoginType;

    /**
     *   是否有日志审计模块
     */
    @ApiModelProperty("是否有日志审计模块")
    private String isAuditModel;

    /**
     *   是否已对接安审平台
     */
    @ApiModelProperty("是否已对接安审平台")
    private String isSecurityPlatform;

    /**
     *   ip地址
     */
    @ApiModelProperty("ip地址")
    private String ip;

    /**
     *   服务器类型
     */
    @ApiModelProperty("服务器类型")
    private String serverType;

    /**
     *   网络
     */
    @ApiModelProperty("网络")
    private String network;

    /**
     *   数据库类型
     */
    @ApiModelProperty("数据库类型")
    private String databaseType;

    /**
     *   数据库实例名
     */
    @ApiModelProperty("数据库实例名")
    private String databaseName;

    /**
     *   信息化室对口民警
     */
    @ApiModelProperty("信息化室对口民警")
    private String infoOfficePolice;

    /**
     *   承建公司
     */
    @ApiModelProperty("承建公司")
    private String buildCompany;

    /**
     *   承建公司联系人
     */
    @ApiModelProperty("承建公司联系人")
    private String buildContact;

    /**
     *   承建公司联系人电话
     */
    @ApiModelProperty("承建公司联系人电话")
    private String buildTel;

    /**
     *   数据库管理员
     */
    @ApiModelProperty("数据库管理员")
    private String dba;

    /**
     *   开发完成时间
     */
    @ApiModelProperty("开发完成时间")
    private String completeTime;

    /**
     *   是否使用文字处理软件
     */
    @ApiModelProperty("是否使用文字处理软件")
    private String isFontSoftware;

    /**
     *   与国产文字处理软件是否兼容
     */
    @ApiModelProperty("与国产文字处理软件是否兼容")
    private String isFontCompatible;

    /**
     *   上级系统ID
     */
    @ApiModelProperty("上级系统ID")
    private String upSysId;

    /**
     *   用户ID
     */
    @ApiModelProperty("用户ID")
    private String userId;

    /**
     *   应用系统等级: 1App,2模块
     */
    @ApiModelProperty("应用系统等级: 1App,2模块")
    private String isAppOrModel;

    /**
     *   建库模式
     */
    @ApiModelProperty("建库模式")
    private String createModel;

    /**
     *   是否有查询模块
     */
    @ApiModelProperty("是否有查询模块")
    private String isQueryModel;

    /**
     *   系统来源: 巨龙/第三方/移动警务等
     */
    @ApiModelProperty("系统来源: 巨龙/第三方/移动警务等")
    private Integer sourceType;

    /**
     *   系统标识:1.系统 2.网站
     */
    @ApiModelProperty("系统标识:1.系统 2.网站")
    private Integer webType;

    /**
     *   是否完成PK/PMI改造 1是,0否
     */
    @ApiModelProperty("是否完成PK/PMI改造 1是,0否")
    private String isOverPkiOrPmi;

    /**
     *   是否定级备案 1是,0否
     */
    @ApiModelProperty("是否定级备案 1是,0否")
    private String isRankRecord;

    /**
     *   定级情况 一级、二级、三级、四级
     */
    @ApiModelProperty("定级情况 一级、二级、三级、四级")
    private String rankLevel;

    /**
     *   地市区域代码
     */
    @ApiModelProperty("地市区域代码")
    private String cityAreaCode;

    /**
     */
    @ApiModelProperty("")
    private String cityAreaName;

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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getSiteTypeId() {
        return siteTypeId;
    }

    public void setSiteTypeId(Integer siteTypeId) {
        this.siteTypeId = siteTypeId;
    }

    public Integer getSecurityLevelId() {
        return securityLevelId;
    }

    public void setSecurityLevelId(Integer securityLevelId) {
        this.securityLevelId = securityLevelId;
    }

    public String getAppTypeId() {
        return appTypeId;
    }

    public void setAppTypeId(String appTypeId) {
        this.appTypeId = appTypeId;
    }

    public String getCertsn() {
        return certsn;
    }

    public void setCertsn(String certsn) {
        this.certsn = certsn;
    }

    public Integer getIsAnalysis() {
        return isAnalysis;
    }

    public void setIsAnalysis(Integer isAnalysis) {
        this.isAnalysis = isAnalysis;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getImportantTypeId() {
        return importantTypeId;
    }

    public void setImportantTypeId(Integer importantTypeId) {
        this.importantTypeId = importantTypeId;
    }

    public String getPoliceTypeId() {
        return policeTypeId;
    }

    public void setPoliceTypeId(String policeTypeId) {
        this.policeTypeId = policeTypeId;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getBoundaryIp() {
        return boundaryIp;
    }

    public void setBoundaryIp(String boundaryIp) {
        this.boundaryIp = boundaryIp;
    }

    public Integer getBoundaryId() {
        return boundaryId;
    }

    public void setBoundaryId(Integer boundaryId) {
        this.boundaryId = boundaryId;
    }

    public Integer getAccessType() {
        return accessType;
    }

    public void setAccessType(Integer accessType) {
        this.accessType = accessType;
    }

    public Integer getIsOtherTrans() {
        return isOtherTrans;
    }

    public void setIsOtherTrans(Integer isOtherTrans) {
        this.isOtherTrans = isOtherTrans;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSysLevel() {
        return sysLevel;
    }

    public void setSysLevel(String sysLevel) {
        this.sysLevel = sysLevel;
    }

    public String getSysState() {
        return sysState;
    }

    public void setSysState(String sysState) {
        this.sysState = sysState;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getInfoScope() {
        return infoScope;
    }

    public void setInfoScope(String infoScope) {
        this.infoScope = infoScope;
    }

    public String getDevelopLanguage() {
        return developLanguage;
    }

    public void setDevelopLanguage(String developLanguage) {
        this.developLanguage = developLanguage;
    }

    public String getSysFramework() {
        return sysFramework;
    }

    public void setSysFramework(String sysFramework) {
        this.sysFramework = sysFramework;
    }

    public String getOtherPlugin() {
        return otherPlugin;
    }

    public void setOtherPlugin(String otherPlugin) {
        this.otherPlugin = otherPlugin;
    }

    public String getExternalDevice() {
        return externalDevice;
    }

    public void setExternalDevice(String externalDevice) {
        this.externalDevice = externalDevice;
    }

    public String getExtDeviceInfo() {
        return extDeviceInfo;
    }

    public void setExtDeviceInfo(String extDeviceInfo) {
        this.extDeviceInfo = extDeviceInfo;
    }

    public String getBrowserInfo() {
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserLoginType() {
        return userLoginType;
    }

    public void setUserLoginType(String userLoginType) {
        this.userLoginType = userLoginType;
    }

    public String getIsAuditModel() {
        return isAuditModel;
    }

    public void setIsAuditModel(String isAuditModel) {
        this.isAuditModel = isAuditModel;
    }

    public String getIsSecurityPlatform() {
        return isSecurityPlatform;
    }

    public void setIsSecurityPlatform(String isSecurityPlatform) {
        this.isSecurityPlatform = isSecurityPlatform;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getInfoOfficePolice() {
        return infoOfficePolice;
    }

    public void setInfoOfficePolice(String infoOfficePolice) {
        this.infoOfficePolice = infoOfficePolice;
    }

    public String getBuildCompany() {
        return buildCompany;
    }

    public void setBuildCompany(String buildCompany) {
        this.buildCompany = buildCompany;
    }

    public String getBuildContact() {
        return buildContact;
    }

    public void setBuildContact(String buildContact) {
        this.buildContact = buildContact;
    }

    public String getBuildTel() {
        return buildTel;
    }

    public void setBuildTel(String buildTel) {
        this.buildTel = buildTel;
    }

    public String getDba() {
        return dba;
    }

    public void setDba(String dba) {
        this.dba = dba;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getIsFontSoftware() {
        return isFontSoftware;
    }

    public void setIsFontSoftware(String isFontSoftware) {
        this.isFontSoftware = isFontSoftware;
    }

    public String getIsFontCompatible() {
        return isFontCompatible;
    }

    public void setIsFontCompatible(String isFontCompatible) {
        this.isFontCompatible = isFontCompatible;
    }

    public String getUpSysId() {
        return upSysId;
    }

    public void setUpSysId(String upSysId) {
        this.upSysId = upSysId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsAppOrModel() {
        return isAppOrModel;
    }

    public void setIsAppOrModel(String isAppOrModel) {
        this.isAppOrModel = isAppOrModel;
    }

    public String getCreateModel() {
        return createModel;
    }

    public void setCreateModel(String createModel) {
        this.createModel = createModel;
    }

    public String getIsQueryModel() {
        return isQueryModel;
    }

    public void setIsQueryModel(String isQueryModel) {
        this.isQueryModel = isQueryModel;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getWebType() {
        return webType;
    }

    public void setWebType(Integer webType) {
        this.webType = webType;
    }

    public String getIsOverPkiOrPmi() {
        return isOverPkiOrPmi;
    }

    public void setIsOverPkiOrPmi(String isOverPkiOrPmi) {
        this.isOverPkiOrPmi = isOverPkiOrPmi;
    }

    public String getIsRankRecord() {
        return isRankRecord;
    }

    public void setIsRankRecord(String isRankRecord) {
        this.isRankRecord = isRankRecord;
    }

    public String getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(String rankLevel) {
        this.rankLevel = rankLevel;
    }

    public String getCityAreaCode() {
        return cityAreaCode;
    }

    public void setCityAreaCode(String cityAreaCode) {
        this.cityAreaCode = cityAreaCode;
    }

    public String getCityAreaName() {
        return cityAreaName;
    }

    public void setCityAreaName(String cityAreaName) {
        this.cityAreaName = cityAreaName;
    }

}

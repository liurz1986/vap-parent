package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table base_device_info
 *
 * @mbg.generated do_not_delete_during_merge 2020-04-16 14:09:00
 */
@ApiModel
@SuppressWarnings("unused")
public class BaseDeviceInfoQuery extends Query {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @NotNull
    private String id;

    /**
     * 设备号
     */
    @ApiModelProperty("设备号")
    private String deviceOnlyId;

    /**
     * 资产号
     */
    @ApiModelProperty("资产号")
    private String deviceCode;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称")
    private String deptName;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String officeName;

    /**
     * 科室名称
     */
    @ApiModelProperty("科室名称")
    private String subOffice;

    /**
     * 使用人
     */
    @ApiModelProperty("使用人")
    private String userName;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 操作系统类型
     */
    @ApiModelProperty("操作系统类型")
    private String osType;

    /**
     * 操作系统语言
     */
    @ApiModelProperty("操作系统语言")
    private String osLanguage;

    /**
     * Service pack号
     */
    @ApiModelProperty("Service pack号")
    private String spNumber;

    /**
     * CPU类型
     */
    @ApiModelProperty("CPU类型")
    private String cpuType;

    /**
     * CPU型号
     */
    @ApiModelProperty("CPU型号")
    private String cpuSerial;

    /**
     * CPU主频
     */
    @ApiModelProperty("CPU主频")
    private Integer cpuHz;

    /**
     * ip地址
     */
    @ApiModelProperty("ip地址")
    private String ip;

    /**
     * ip数值(排序用)
     */
    @ApiModelProperty("ip数值(排序用)")
    private String ipNumber;

    /**
     * mac地址
     */
    @ApiModelProperty("mac地址")
    private String mac;

    /**
     * 内存大小
     */
    @ApiModelProperty("内存大小")
    private Double memorySize;

    /**
     * 硬盘空间
     */
    @ApiModelProperty("硬盘空间")
    private Integer diskSize;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String tel;

    /**
     * 邮箱地址
     */
    @ApiModelProperty("邮箱地址")
    private String email;

    /**
     * 操作系统登陆名称
     */
    @ApiModelProperty("操作系统登陆名称")
    private String logonUserName;

    /**
     * 域名地址
     */
    @ApiModelProperty("域名地址")
    private String domainName;

    /**
     * 识别标识
     */
    @ApiModelProperty("识别标识")
    private Long identify;

    /**
     * 是否运行拨号上网
     */
    @ApiModelProperty("是否运行拨号上网")
    private Integer allowDail;

    /**
     * 注册时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("注册时间")
    private Date registerTime;

    /**
     * 最后使用时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后使用时间")
    private Date lastTime;

    /**
     * 攻击时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("攻击时间")
    private Date attackTime;

    /**
     * 报告时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("报告时间")
    private Date reportTime;

    /**
     * 卸载时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("卸载时间")
    private Date uninstallTime;

    /**
     * 空闲时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("空闲时间")
    private Date idleTime;

    /**
     * 开机时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开机时间")
    private Date bootTime;

    /**
     * 保护结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("保护结束时间")
    private Date protectEndTime;

    /**
     * 锁定结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("锁定结束时间")
    private Date lockedEndTime;

    /**
     * 设备类型ID
     */
    @ApiModelProperty("设备类型ID")
    private Integer deviceTypeId;

    /**
     * 在线时间
     */
    @ApiModelProperty("在线时间")
    private Integer onlineTime;

    /**
     * 合计时间
     */
    @ApiModelProperty("合计时间")
    private Long totalTime;

    /**
     * 当天在线时间
     */
    @ApiModelProperty("当天在线时间")
    private Integer dayTime;

    /**
     * 运行状态  1--开机  0--关机
     */
    @ApiModelProperty("运行状态  1--开机  0--关机")
    private Integer runStatus;

    /**
     * 是否注册  1--注册  0--未注册
     */
    @ApiModelProperty("是否注册  1--注册  0--未注册")
    private Integer registered;

    /**
     * 客户端注册程序版本号
     */
    @ApiModelProperty("客户端注册程序版本号")
    private String clientVersion;

    /**
     * 杀毒软件厂商
     */
    @ApiModelProperty("杀毒软件厂商")
    private String kvsCompany;

    /**
     * 杀毒软件版本
     */
    @ApiModelProperty("杀毒软件版本")
    private String kvsVersion;

    /**
     * 授权码（控制授权用，注册时候输入的）
     */
    @ApiModelProperty("授权码（控制授权用，注册时候输入的）")
    private String aclCtrl;

    /**
     * 是否保护  1--保护  0--不保护
     */
    @ApiModelProperty("是否保护  1--保护  0--不保护")
    private Integer isProtect;

    /**
     * 是否锁定 1--锁定/信任 0--不锁定/不信任
     */
    @ApiModelProperty("是否锁定 1--锁定/信任 0--不锁定/不信任")
    private Integer isLock;

    /**
     * 是否阻断 1--阻断  0--不阻断
     */
    @ApiModelProperty("是否阻断 1--阻断  0--不阻断")
    private Integer isForceOut;

    /**
     * 安全级别
     */
    @ApiModelProperty("安全级别")
    private Integer runLevel;

    /**
     * 扩展字段3  包含无效设备
     */
    @ApiModelProperty("扩展字段3  包含无效设备")
    private Integer reserved3;

    /**
     * 计算机制造商
     */
    @ApiModelProperty("计算机制造商")
    private String computerManufacturer;

    /**
     * 计算机型号
     */
    @ApiModelProperty("计算机型号")
    private String computerModel;

    /**
     * 描述(备注、说明等)
     */
    @ApiModelProperty("描述(备注、说明等)")
    private String description;

    /**
     * pkiId(关联PKI 表ID)
     */
    @ApiModelProperty("pkiId(关联PKI 表ID)")
    private String pkiId;

    /**
     * PKI用户
     */
    @ApiModelProperty("PKI用户")
    private String pkiUser;

    /**
     * PKI单位编码
     */
    @ApiModelProperty("PKI单位编码")
    private String pkiUnit;

    /**
     * 地市代码
     */
    @ApiModelProperty("地市代码")
    @NotNull
    private String areaCode;

    /**
     * 数据更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("数据更新时间")
    private Date lastUpdateTime;

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    @NotNull
    private String deviceId;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @NotNull
    private Date createTime;

    /**
     * 终端自定义组织机构编号
     */
    @ApiModelProperty("终端自定义组织机构编号")
    private Integer classId;

    /**
     * 数据状态: 0.正常 1.提示状态:手动维护过且与同步数据不一致 2.忽略状态:忽略手动维护
     */
    @ApiModelProperty("数据状态: 0.正常 1.提示状态:手动维护过且与同步数据不一致 2.忽略状态:忽略手动维护")
    private Integer updateStatus;

    /**
     * 一级设备类型名称
     */
    @ApiModelProperty("一级设备类型名称")
    private String devFirstTypeName;

    /**
     * 二级设备类型名称
     */
    @ApiModelProperty("二级设备类型名称")
    private String devSecondTypeName;

    /**
     * 开放端口
     */
    @ApiModelProperty("开放端口")
    private String openPort;

    /**
     * 服务
     */
    @ApiModelProperty("服务")
    private String service;

    /**
     * 应用
     */
    @ApiModelProperty("应用")
    private String application;

    /**
     * 额外信息
     */
    @ApiModelProperty("额外信息")
    private String extraInfo;

    /**
     * 标签
     */
    @ApiModelProperty("标签")
    private String label;

    /**
     * 主机名
     */
    @ApiModelProperty("主机名")
    private String hostName;

    /**
     * 路由可达
     */
    @ApiModelProperty("路由可达")
    private String returnRouter;

    /**
     * 数据来源,0 一机两用注册设备  1 360资产扫描  2 人工维护
     */
    @ApiModelProperty("数据来源,0 一机两用注册设备  1 360资产扫描  2 人工维护")
    private String dataSource;

    /**
     * 管理器IP
     */
    @ApiModelProperty("管理器IP ")
    private String regIp;

    /**
     * 等于 2 卸载，不等于2 未卸载
     */
    @ApiModelProperty("等于 2 卸载，不等于2 未卸载")
    private Integer reserved1;

    /**
     * 1=有杀毒软件,0=无杀毒软件
     */
    @ApiModelProperty("1=有杀毒软件,0=无杀毒软件")
    private Integer hasKvs;

    /**
     * 查询字段,ip地址
     */
    @ApiModelProperty("精确查询字段:ip地址")
    @JsonProperty(value = "_ip")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String exactIp;

    @ApiModelProperty("设备类型查询条件")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String devTypeName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceOnlyId() {
        return deviceOnlyId;
    }

    public void setDeviceOnlyId(String deviceOnlyId) {
        this.deviceOnlyId = deviceOnlyId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getSubOffice() {
        return subOffice;
    }

    public void setSubOffice(String subOffice) {
        this.subOffice = subOffice;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsLanguage() {
        return osLanguage;
    }

    public void setOsLanguage(String osLanguage) {
        this.osLanguage = osLanguage;
    }

    public String getSpNumber() {
        return spNumber;
    }

    public void setSpNumber(String spNumber) {
        this.spNumber = spNumber;
    }

    public String getCpuType() {
        return cpuType;
    }

    public void setCpuType(String cpuType) {
        this.cpuType = cpuType;
    }

    public String getCpuSerial() {
        return cpuSerial;
    }

    public void setCpuSerial(String cpuSerial) {
        this.cpuSerial = cpuSerial;
    }

    public Integer getCpuHz() {
        return cpuHz;
    }

    public void setCpuHz(Integer cpuHz) {
        this.cpuHz = cpuHz;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIpNumber() {
        return ipNumber;
    }

    public void setIpNumber(String ipNumber) {
        this.ipNumber = ipNumber;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Double getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Double memorySize) {
        this.memorySize = memorySize;
    }

    public Integer getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(Integer diskSize) {
        this.diskSize = diskSize;
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

    public String getLogonUserName() {
        return logonUserName;
    }

    public void setLogonUserName(String logonUserName) {
        this.logonUserName = logonUserName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getIdentify() {
        return identify;
    }

    public void setIdentify(Long identify) {
        this.identify = identify;
    }

    public Integer getAllowDail() {
        return allowDail;
    }

    public void setAllowDail(Integer allowDail) {
        this.allowDail = allowDail;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Date getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(Date attackTime) {
        this.attackTime = attackTime;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public Date getUninstallTime() {
        return uninstallTime;
    }

    public void setUninstallTime(Date uninstallTime) {
        this.uninstallTime = uninstallTime;
    }

    public Date getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(Date idleTime) {
        this.idleTime = idleTime;
    }

    public Date getBootTime() {
        return bootTime;
    }

    public void setBootTime(Date bootTime) {
        this.bootTime = bootTime;
    }

    public Date getProtectEndTime() {
        return protectEndTime;
    }

    public void setProtectEndTime(Date protectEndTime) {
        this.protectEndTime = protectEndTime;
    }

    public Date getLockedEndTime() {
        return lockedEndTime;
    }

    public void setLockedEndTime(Date lockedEndTime) {
        this.lockedEndTime = lockedEndTime;
    }

    public Integer getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(Integer deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public Integer getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Integer onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getDayTime() {
        return dayTime;
    }

    public void setDayTime(Integer dayTime) {
        this.dayTime = dayTime;
    }

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    public Integer getRegistered() {
        return registered;
    }

    public void setRegistered(Integer registered) {
        this.registered = registered;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getKvsCompany() {
        return kvsCompany;
    }

    public void setKvsCompany(String kvsCompany) {
        this.kvsCompany = kvsCompany;
    }

    public String getKvsVersion() {
        return kvsVersion;
    }

    public void setKvsVersion(String kvsVersion) {
        this.kvsVersion = kvsVersion;
    }

    public String getAclCtrl() {
        return aclCtrl;
    }

    public void setAclCtrl(String aclCtrl) {
        this.aclCtrl = aclCtrl;
    }

    public Integer getIsProtect() {
        return isProtect;
    }

    public void setIsProtect(Integer isProtect) {
        this.isProtect = isProtect;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public Integer getIsForceOut() {
        return isForceOut;
    }

    public void setIsForceOut(Integer isForceOut) {
        this.isForceOut = isForceOut;
    }

    public Integer getRunLevel() {
        return runLevel;
    }

    public void setRunLevel(Integer runLevel) {
        this.runLevel = runLevel;
    }

    public Integer getReserved3() {
        return reserved3;
    }

    public void setReserved3(Integer reserved3) {
        this.reserved3 = reserved3;
    }

    public String getComputerManufacturer() {
        return computerManufacturer;
    }

    public void setComputerManufacturer(String computerManufacturer) {
        this.computerManufacturer = computerManufacturer;
    }

    public String getComputerModel() {
        return computerModel;
    }

    public void setComputerModel(String computerModel) {
        this.computerModel = computerModel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPkiId() {
        return pkiId;
    }

    public void setPkiId(String pkiId) {
        this.pkiId = pkiId;
    }

    public String getPkiUser() {
        return pkiUser;
    }

    public void setPkiUser(String pkiUser) {
        this.pkiUser = pkiUser;
    }

    public String getPkiUnit() {
        return pkiUnit;
    }

    public void setPkiUnit(String pkiUnit) {
        this.pkiUnit = pkiUnit;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(Integer updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getDevFirstTypeName() {
        return devFirstTypeName;
    }

    public void setDevFirstTypeName(String devFirstTypeName) {
        this.devFirstTypeName = devFirstTypeName;
    }

    public String getDevSecondTypeName() {
        return devSecondTypeName;
    }

    public void setDevSecondTypeName(String devSecondTypeName) {
        this.devSecondTypeName = devSecondTypeName;
    }

    public String getOpenPort() {
        return openPort;
    }

    public void setOpenPort(String openPort) {
        this.openPort = openPort;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getReturnRouter() {
        return returnRouter;
    }

    public void setReturnRouter(String returnRouter) {
        this.returnRouter = returnRouter;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }

    public Integer getReserved1() {
        return reserved1;
    }

    public void setReserved1(Integer reserved1) {
        this.reserved1 = reserved1;
    }

    public Integer getHasKvs() {
        return hasKvs;
    }

    public void setHasKvs(Integer hasKvs) {
        this.hasKvs = hasKvs;
    }

    public String getExactIp() {
        return exactIp;
    }

    public void setExactIp(String exactIp) {
        this.exactIp = exactIp;
    }

    public String getDevTypeName() {
        return devTypeName;
    }

    public void setDevTypeName(String devTypeName) {
        this.devTypeName = devTypeName;
    }
}
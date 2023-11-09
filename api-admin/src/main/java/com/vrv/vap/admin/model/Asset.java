package com.vrv.vap.admin.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

public class Asset {
    @Id
    @Column(name = "Guid")
    private String guid;

    @Column(name = "Name")
    private String name;

    @Column(name = "Name_en")
    private String nameEn;

    @Column(name = "IP")
    private String ip;

    @Column(name = "securityGuid")
    private String securityguid;

    @Column(name = "ipNum")
    private Long ipnum;

    @Column(name = "Type_Guid")
    private String typeGuid;

    @Column(name = "Type_Sno_Guid")
    private String typeSnoGuid;

    @Column(name = "Version_info")
    private String versionInfo;

    /**
     * 资产标签，不同标签之间以,隔开
     */
    @Column(name = "Tags")
    private String tags;

    @Column(name = "CreateTime")
    private Date createtime;

    @Column(name = "typeUnicode")
    private String typeunicode;

    @Column(name = "snoUnicode")
    private String snounicode;

    private String mac;

    @Column(name = "employee_Code1")
    private String employeeCode1;

    @Column(name = "employee_Code2")
    private String employeeCode2;

    /**
     * 性能监控
     */
    private String monitor;

    /**
     * 采集事件是否开启
     */
    private String special;

    /**
     * 监控开启
     */
    @Column(name = "canMonitor")
    private String canmonitor;

    /**
     * 远程控制
     */
    @Column(name = "canRCtrl")
    private String canrctrl;

    /**
     * 资产价值
     */
    private String worth;

    /**
     * 机密性权值
     */
    private String secrecy;

    /**
     * 完整性权值
     */
    private String integrity;

    /**
     * 可用性权值
     */
    private String availability;

    /**
     * 监控协议
     */
    private String protocol;

    /**
     * 资产编号
     */
    @Column(name = "assetNum")
    private String assetnum;

    /**
     * 资产用途
     */
    @Column(name = "assetUse")
    private String assetuse;

    /**
     * 物理位置
     */
    private String location;

    /**
     * 备注
     */
    @Column(name = "AssetDescribe")
    private String assetdescribe;

    /**
     * 所属机柜guid
     */
    @Column(name = "cabinetGuid")
    private String cabinetguid;

    /**
     * 距离底部高度
     */
    @Column(name = "marginBottom")
    private Integer marginbottom;

    /**
     * 占U口个数
     */
    private Integer height;

    /**
     * 经度
     */
    private BigDecimal lng;

    /**
     * 纬度
     */
    private BigDecimal lat;

    /**
     * 网关名称
     */
    @Column(name = "gatewayName")
    private String gatewayname;

    /**
     * 网关序列号
     */
    @Column(name = "gatewayNum")
    private String gatewaynum;

    /**
     * 主管名称
     */
    @Column(name = "gatewayUser")
    private String gatewayuser;

    /**
     * 主管部门
     */
    @Column(name = "gatewayDepartment")
    private String gatewaydepartment;

    /**
     * 电话号码
     */
    @Column(name = "phoneNum")
    private String phonenum;

    /**
     * 说明
     */
    @Column(name = "remarkInfo")
    private String remarkinfo;

    /**
     * 机构
     */
    private String org;

    /**
     * 核心资产
     */
    private Boolean core;

    /**
     * 应用系统id
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 应用系统名称
     */
    @Column(name = "app_name")
    private String appName;

    private String labels;

    @Column(name = "employee_guid")
    private String employeeGuid;

    @Column(name = "domain_sub_code")
    private String domainSubCode;

    /**
     * 设备密集
     */
    @Column(name = "equipment_intensive")
    private String equipmentIntensive;

    /**
     * 序列号
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 1：表示国产 2：非国产
     */
    @Column(name = "term_type")
    private String termType;

    /**
     * 组织结构名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 组织结构code
     */
    @Column(name = "org_code")
    private String orgCode;

    /**
     * 责任人名称(比如普通用户、管理员)
     */
    @Column(name = "responsible_name")
    private String responsibleName;

    /**
     * 责任人code(用户账号code）
     */
    @Column(name = "responsible_code")
    private String responsibleCode;

    /**
     * 终端类型）1.已安装；2.未安装
     */
    @Column(name = "ismonitor_agent")
    private String ismonitorAgent;

    /**
     * 终端类型操作系统安装时间
     */
    @Column(name = "os_setup_time")
    private String osSetupTime;

    /**
     * 终端类型安装操作系统
     */
    @Column(name = "os_list")
    private String osList;

    /**
     * 终端类型 ：运维终端/用户终端
     */
    @Column(name = "terminal_type")
    private String terminalType;

    /**
     * @return Guid
     */
    public String getGuid() {
        return guid;
    }

    /**
     * @param guid
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Name_en
     */
    public String getNameEn() {
        return nameEn;
    }

    /**
     * @param nameEn
     */
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    /**
     * @return IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return securityGuid
     */
    public String getSecurityguid() {
        return securityguid;
    }

    /**
     * @param securityguid
     */
    public void setSecurityguid(String securityguid) {
        this.securityguid = securityguid;
    }

    /**
     * @return ipNum
     */
    public Long getIpnum() {
        return ipnum;
    }

    /**
     * @param ipnum
     */
    public void setIpnum(Long ipnum) {
        this.ipnum = ipnum;
    }

    /**
     * @return Type_Guid
     */
    public String getTypeGuid() {
        return typeGuid;
    }

    /**
     * @param typeGuid
     */
    public void setTypeGuid(String typeGuid) {
        this.typeGuid = typeGuid;
    }

    /**
     * @return Type_Sno_Guid
     */
    public String getTypeSnoGuid() {
        return typeSnoGuid;
    }

    /**
     * @param typeSnoGuid
     */
    public void setTypeSnoGuid(String typeSnoGuid) {
        this.typeSnoGuid = typeSnoGuid;
    }

    /**
     * @return Version_info
     */
    public String getVersionInfo() {
        return versionInfo;
    }

    /**
     * @param versionInfo
     */
    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    /**
     * 获取资产标签，不同标签之间以,隔开
     *
     * @return Tags - 资产标签，不同标签之间以,隔开
     */
    public String getTags() {
        return tags;
    }

    /**
     * 设置资产标签，不同标签之间以,隔开
     *
     * @param tags 资产标签，不同标签之间以,隔开
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * @return CreateTime
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * @return typeUnicode
     */
    public String getTypeunicode() {
        return typeunicode;
    }

    /**
     * @param typeunicode
     */
    public void setTypeunicode(String typeunicode) {
        this.typeunicode = typeunicode;
    }

    /**
     * @return snoUnicode
     */
    public String getSnounicode() {
        return snounicode;
    }

    /**
     * @param snounicode
     */
    public void setSnounicode(String snounicode) {
        this.snounicode = snounicode;
    }

    /**
     * @return mac
     */
    public String getMac() {
        return mac;
    }

    /**
     * @param mac
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * @return employee_Code1
     */
    public String getEmployeeCode1() {
        return employeeCode1;
    }

    /**
     * @param employeeCode1
     */
    public void setEmployeeCode1(String employeeCode1) {
        this.employeeCode1 = employeeCode1;
    }

    /**
     * @return employee_Code2
     */
    public String getEmployeeCode2() {
        return employeeCode2;
    }

    /**
     * @param employeeCode2
     */
    public void setEmployeeCode2(String employeeCode2) {
        this.employeeCode2 = employeeCode2;
    }

    /**
     * 获取性能监控
     *
     * @return monitor - 性能监控
     */
    public String getMonitor() {
        return monitor;
    }

    /**
     * 设置性能监控
     *
     * @param monitor 性能监控
     */
    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    /**
     * 获取采集事件是否开启
     *
     * @return special - 采集事件是否开启
     */
    public String getSpecial() {
        return special;
    }

    /**
     * 设置采集事件是否开启
     *
     * @param special 采集事件是否开启
     */
    public void setSpecial(String special) {
        this.special = special;
    }

    /**
     * 获取监控开启
     *
     * @return canMonitor - 监控开启
     */
    public String getCanmonitor() {
        return canmonitor;
    }

    /**
     * 设置监控开启
     *
     * @param canmonitor 监控开启
     */
    public void setCanmonitor(String canmonitor) {
        this.canmonitor = canmonitor;
    }

    /**
     * 获取远程控制
     *
     * @return canRCtrl - 远程控制
     */
    public String getCanrctrl() {
        return canrctrl;
    }

    /**
     * 设置远程控制
     *
     * @param canrctrl 远程控制
     */
    public void setCanrctrl(String canrctrl) {
        this.canrctrl = canrctrl;
    }

    /**
     * 获取资产价值
     *
     * @return worth - 资产价值
     */
    public String getWorth() {
        return worth;
    }

    /**
     * 设置资产价值
     *
     * @param worth 资产价值
     */
    public void setWorth(String worth) {
        this.worth = worth;
    }

    /**
     * 获取机密性权值
     *
     * @return secrecy - 机密性权值
     */
    public String getSecrecy() {
        return secrecy;
    }

    /**
     * 设置机密性权值
     *
     * @param secrecy 机密性权值
     */
    public void setSecrecy(String secrecy) {
        this.secrecy = secrecy;
    }

    /**
     * 获取完整性权值
     *
     * @return integrity - 完整性权值
     */
    public String getIntegrity() {
        return integrity;
    }

    /**
     * 设置完整性权值
     *
     * @param integrity 完整性权值
     */
    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    /**
     * 获取可用性权值
     *
     * @return availability - 可用性权值
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * 设置可用性权值
     *
     * @param availability 可用性权值
     */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /**
     * 获取监控协议
     *
     * @return protocol - 监控协议
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 设置监控协议
     *
     * @param protocol 监控协议
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * 获取资产编号
     *
     * @return assetNum - 资产编号
     */
    public String getAssetnum() {
        return assetnum;
    }

    /**
     * 设置资产编号
     *
     * @param assetnum 资产编号
     */
    public void setAssetnum(String assetnum) {
        this.assetnum = assetnum;
    }

    /**
     * 获取资产用途
     *
     * @return assetUse - 资产用途
     */
    public String getAssetuse() {
        return assetuse;
    }

    /**
     * 设置资产用途
     *
     * @param assetuse 资产用途
     */
    public void setAssetuse(String assetuse) {
        this.assetuse = assetuse;
    }

    /**
     * 获取物理位置
     *
     * @return location - 物理位置
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置物理位置
     *
     * @param location 物理位置
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 获取备注
     *
     * @return AssetDescribe - 备注
     */
    public String getAssetdescribe() {
        return assetdescribe;
    }

    /**
     * 设置备注
     *
     * @param assetdescribe 备注
     */
    public void setAssetdescribe(String assetdescribe) {
        this.assetdescribe = assetdescribe;
    }

    /**
     * 获取所属机柜guid
     *
     * @return cabinetGuid - 所属机柜guid
     */
    public String getCabinetguid() {
        return cabinetguid;
    }

    /**
     * 设置所属机柜guid
     *
     * @param cabinetguid 所属机柜guid
     */
    public void setCabinetguid(String cabinetguid) {
        this.cabinetguid = cabinetguid;
    }

    /**
     * 获取距离底部高度
     *
     * @return marginBottom - 距离底部高度
     */
    public Integer getMarginbottom() {
        return marginbottom;
    }

    /**
     * 设置距离底部高度
     *
     * @param marginbottom 距离底部高度
     */
    public void setMarginbottom(Integer marginbottom) {
        this.marginbottom = marginbottom;
    }

    /**
     * 获取占U口个数
     *
     * @return height - 占U口个数
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * 设置占U口个数
     *
     * @param height 占U口个数
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * 获取经度
     *
     * @return lng - 经度
     */
    public BigDecimal getLng() {
        return lng;
    }

    /**
     * 设置经度
     *
     * @param lng 经度
     */
    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    /**
     * 获取纬度
     *
     * @return lat - 纬度
     */
    public BigDecimal getLat() {
        return lat;
    }

    /**
     * 设置纬度
     *
     * @param lat 纬度
     */
    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    /**
     * 获取网关名称
     *
     * @return gatewayName - 网关名称
     */
    public String getGatewayname() {
        return gatewayname;
    }

    /**
     * 设置网关名称
     *
     * @param gatewayname 网关名称
     */
    public void setGatewayname(String gatewayname) {
        this.gatewayname = gatewayname;
    }

    /**
     * 获取网关序列号
     *
     * @return gatewayNum - 网关序列号
     */
    public String getGatewaynum() {
        return gatewaynum;
    }

    /**
     * 设置网关序列号
     *
     * @param gatewaynum 网关序列号
     */
    public void setGatewaynum(String gatewaynum) {
        this.gatewaynum = gatewaynum;
    }

    /**
     * 获取主管名称
     *
     * @return gatewayUser - 主管名称
     */
    public String getGatewayuser() {
        return gatewayuser;
    }

    /**
     * 设置主管名称
     *
     * @param gatewayuser 主管名称
     */
    public void setGatewayuser(String gatewayuser) {
        this.gatewayuser = gatewayuser;
    }

    /**
     * 获取主管部门
     *
     * @return gatewayDepartment - 主管部门
     */
    public String getGatewaydepartment() {
        return gatewaydepartment;
    }

    /**
     * 设置主管部门
     *
     * @param gatewaydepartment 主管部门
     */
    public void setGatewaydepartment(String gatewaydepartment) {
        this.gatewaydepartment = gatewaydepartment;
    }

    /**
     * 获取电话号码
     *
     * @return phoneNum - 电话号码
     */
    public String getPhonenum() {
        return phonenum;
    }

    /**
     * 设置电话号码
     *
     * @param phonenum 电话号码
     */
    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    /**
     * 获取说明
     *
     * @return remarkInfo - 说明
     */
    public String getRemarkinfo() {
        return remarkinfo;
    }

    /**
     * 设置说明
     *
     * @param remarkinfo 说明
     */
    public void setRemarkinfo(String remarkinfo) {
        this.remarkinfo = remarkinfo;
    }

    /**
     * 获取机构
     *
     * @return org - 机构
     */
    public String getOrg() {
        return org;
    }

    /**
     * 设置机构
     *
     * @param org 机构
     */
    public void setOrg(String org) {
        this.org = org;
    }

    /**
     * 获取核心资产
     *
     * @return core - 核心资产
     */
    public Boolean getCore() {
        return core;
    }

    /**
     * 设置核心资产
     *
     * @param core 核心资产
     */
    public void setCore(Boolean core) {
        this.core = core;
    }

    /**
     * 获取应用系统id
     *
     * @return app_id - 应用系统id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置应用系统id
     *
     * @param appId 应用系统id
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取应用系统名称
     *
     * @return app_name - 应用系统名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 设置应用系统名称
     *
     * @param appName 应用系统名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * @return labels
     */
    public String getLabels() {
        return labels;
    }

    /**
     * @param labels
     */
    public void setLabels(String labels) {
        this.labels = labels;
    }

    /**
     * @return employee_guid
     */
    public String getEmployeeGuid() {
        return employeeGuid;
    }

    /**
     * @param employeeGuid
     */
    public void setEmployeeGuid(String employeeGuid) {
        this.employeeGuid = employeeGuid;
    }

    /**
     * @return domain_sub_code
     */
    public String getDomainSubCode() {
        return domainSubCode;
    }

    /**
     * @param domainSubCode
     */
    public void setDomainSubCode(String domainSubCode) {
        this.domainSubCode = domainSubCode;
    }

    /**
     * 获取设备密集
     *
     * @return equipment_intensive - 设备密集
     */
    public String getEquipmentIntensive() {
        return equipmentIntensive;
    }

    /**
     * 设置设备密集
     *
     * @param equipmentIntensive 设备密集
     */
    public void setEquipmentIntensive(String equipmentIntensive) {
        this.equipmentIntensive = equipmentIntensive;
    }

    /**
     * 获取序列号
     *
     * @return serial_number - 序列号
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * 设置序列号
     *
     * @param serialNumber 序列号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 获取1：表示国产 2：非国产
     *
     * @return term_type - 1：表示国产 2：非国产
     */
    public String getTermType() {
        return termType;
    }

    /**
     * 设置1：表示国产 2：非国产
     *
     * @param termType 1：表示国产 2：非国产
     */
    public void setTermType(String termType) {
        this.termType = termType;
    }

    /**
     * 获取组织结构名称
     *
     * @return org_name - 组织结构名称
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 设置组织结构名称
     *
     * @param orgName 组织结构名称
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * 获取组织结构code
     *
     * @return org_code - 组织结构code
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * 设置组织结构code
     *
     * @param orgCode 组织结构code
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * 获取责任人名称(比如普通用户、管理员)
     *
     * @return responsible_name - 责任人名称(比如普通用户、管理员)
     */
    public String getResponsibleName() {
        return responsibleName;
    }

    /**
     * 设置责任人名称(比如普通用户、管理员)
     *
     * @param responsibleName 责任人名称(比如普通用户、管理员)
     */
    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    /**
     * 获取责任人code(用户账号code）
     *
     * @return responsible_code - 责任人code(用户账号code）
     */
    public String getResponsibleCode() {
        return responsibleCode;
    }

    /**
     * 设置责任人code(用户账号code）
     *
     * @param responsibleCode 责任人code(用户账号code）
     */
    public void setResponsibleCode(String responsibleCode) {
        this.responsibleCode = responsibleCode;
    }

    /**
     * 获取终端类型）1.已安装；2.未安装
     *
     * @return ismonitor_agent - 终端类型）1.已安装；2.未安装
     */
    public String getIsmonitorAgent() {
        return ismonitorAgent;
    }

    /**
     * 设置终端类型）1.已安装；2.未安装
     *
     * @param ismonitorAgent 终端类型）1.已安装；2.未安装
     */
    public void setIsmonitorAgent(String ismonitorAgent) {
        this.ismonitorAgent = ismonitorAgent;
    }

    /**
     * 获取终端类型操作系统安装时间
     *
     * @return os_setup_time - 终端类型操作系统安装时间
     */
    public String getOsSetupTime() {
        return osSetupTime;
    }

    /**
     * 设置终端类型操作系统安装时间
     *
     * @param osSetupTime 终端类型操作系统安装时间
     */
    public void setOsSetupTime(String osSetupTime) {
        this.osSetupTime = osSetupTime;
    }

    /**
     * 获取终端类型安装操作系统
     *
     * @return os_list - 终端类型安装操作系统
     */
    public String getOsList() {
        return osList;
    }

    /**
     * 设置终端类型安装操作系统
     *
     * @param osList 终端类型安装操作系统
     */
    public void setOsList(String osList) {
        this.osList = osList;
    }

    /**
     * 获取终端类型 ：运维终端/用户终端
     *
     * @return terminal_type - 终端类型 ：运维终端/用户终端
     */
    public String getTerminalType() {
        return terminalType;
    }

    /**
     * 设置终端类型 ：运维终端/用户终端
     *
     * @param terminalType 终端类型 ：运维终端/用户终端
     */
    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }
}
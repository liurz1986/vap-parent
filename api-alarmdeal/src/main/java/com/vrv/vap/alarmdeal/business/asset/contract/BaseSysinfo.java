package com.vrv.vap.alarmdeal.business.asset.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseSysinfo  implements Serializable {

 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    Integer	accessType ;//	接入方式 0-其他 1-边界设备	int32	
	String accountType;//	账户类型	string	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") 
	Date addTime;//	归类时间	date-time	
	String appTypeId;//	业务分类	string	
	String areaCode;//	所属区域	string	
	Integer	boundaryId;//	边界设备信息ID	int32	
	String boundaryIp;//	边界设备IP	string	
	String browserInfo;//	浏览器及版本号	string	
	String buildCompany;//	承建公司	string	
	String buildContact;//	承建公司联系人	string	
	String buildTel;//	承建公司联系人电话	string	
	String 	by_;//	排序:desc/asc	string	
	String 	certsn;//	网站负责人PKI证书号	string	
	String 	cityAreaCode;//	地市区域代码	string	
	String 	cityAreaName;//		string	
	String 	completeTime;//	开发完成时间	string	
Integer	count_	;//返回数量	int32	
	String 	createModel;//	建库模式	string	
	String databaseName;//	数据库实例名	string	
	String databaseType;//	数据库类型	string	
	String dba;//	数据库管理员	string	
	String developLanguage;//	开发语言	string	
	String 	domain;//	系统域名	string	
	String 	email;//	邮箱地址	string	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date end_time	;//结束时间,格式yyyy-MM-dd HH:mm:ss	date-time	
	String extDeviceInfo;//	外接设备型号（厂家、版本）	string	
	String externalDevice;//	所需要的外接设备	string	
	Integer	id;//	主键 主键/NOT NULL/自增长	int32	
	Integer	importantTypeId;//	重要等级	int32	
	String 	infoOfficePolice;//	信息化室对口民警	string	
	String infoScope;//	信息应用域	string	
	String ip;//	ip地址	string	
	String ips;//	ips地址	string	
	Integer	isAnalysis;//	是否启用分析	int32	
	String isAppOrModel;//	应用系统等级: 1App,2模块	string	
	String isAuditModel;//	是否有日志审计模块	string	
	String isFontCompatible;//	与国产文字处理软件是否兼容	string	
	String 	isFontSoftware;//	是否使用文字处理软件	string	
	Integer	isOtherTrans;//	是否与其他网络交换数据 0-否 1-是	int32	
	String isOverPkiOrPmi;//	是否完成PK/PMI改造 1是,0否	string	
	String isQueryModel;//	是否有查询模块	string	
	String isRankRecord;//	是否定级备案 1是,0否	string	
	String isSecurityPlatform;//	是否已对接安审平台	string	
	String name;//	系统名称	string	
	String network;//	网络	string	
	String office;//	科室名称	string	
	String order_;//	排序字段	string	
	String orgCode	;//所属部门	string	
	String other;//	接入方式为其他的信息	string	
	String otherPlugin;//	所使用的第三方插件	string	
	String policeTypeId	;//警种	string	
	String rankLevel	;//定级情况 一级、二级、三级、四级	string	
	Integer	securityLevelId	;//登陆安全级别	int32	
	String serverType;//	服务器类型	string	
	Integer	siteTypeId;//	网站站点类别	int32	
	Integer	sourceType;//	系统来源: 巨龙/第三方/移动警务等	int32	
	Integer	start_;//	开始条数	int32	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date start_time;//	开始时间,格式yyyy-MM-dd HH:mm:ss	date-time	
	String sysFramework;//	系统架构	string	
	String sysLevel;//	系统等级	string	
	String sysState;//	系统状态	string	
	String sysType;//	系统类别	string	
	String systemId;//	系统编号 对应巨龙应用系统编号	string	
	String tags;//	标签	string	
	String tel;//	联系电话	string	
	String upSysId	;//上级系统ID	string	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date updateTime;//	修改时间 数据更新时间	date-time	
	String userId;//	用户ID	string	
	String userLoginType;//	个人用户登录方式	string	
	String userName;//	网站负责人	string	
	Integer	webType	;//系统标识:1.系统 2.网站	int32
}

package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.UnitInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventLogSupplementField extends BaseEventLog {
 
	String	op_description;
	
	String	std_username;   //责任人姓名
	String	std_user_no;   //责任人编号
	String	std_user_station;   //责任人岗位
	String	std_user_department;   //责任人部门
	String	std_user_role;   //责任人角色
	String	std_user_level;   //责任人密级
	String	std_user_type;   //用户类型1-用户，2-管理员
	
	String	std_dev_type;   //设备类型
	String	std_dev_name;   //设备名称
	String	std_dev_level;   //设备密级
	String	std_dev_os_type;   //操作系统类型
	
	String	std_dev_id;
	String	std_dev_ip;   //IP
	String	std_dev_software_version;   //软件系统版本号
	String	std_dev_hardware_model;   //硬件设备型号
	String	std_dev_brand_model;   //设备品牌型号
	String	std_dev_net_time;   //设备入网事件
	String	std_dev_mac;   //MAC
	String	std_dev_hardware_identification;   //硬盘序列号
	String	std_dev_safety_marign;   //所属安全域
	
	String	std_sys_id;   //应用系统编号
	String	std_sys_name;   //应用系统名称
	String	std_sys_ip;   //所在服务器IP
	String	std_sys_secret_level;   //应用系统涉密等级非密1，内部2，秘密3，机密4，绝密5
	String	std_sys_protocal;   //通讯协议名
	String	std_sys_parameter;   //通信参数
	String	std_sys_port;   //服务端口
	String	std_org_code;   //单位编码
	String	std_org_name;   //单位名称
	String	std_org_type;   //单位类别
	String	std_secret_qualifications;   //保密资格
	String	std_secret_level;   //密级
	String	std_protection_level;   //防护等级
	
	//文件相关字段
 
	//dev_ip	设备IP		"devIp":"139.211.222.235"
	String		op_type;//	操作类别	0-创建、1-修改、2-删除、3-打开、4-重命名、5-复制、6-移动	0
	String	op_code	;//操作类别编码	参考右侧库	"op_code":21
	String	src_file_dir	;//源文件路径		/root/test/a.txt.txt
	String	src_file_name	;//	通过绝对路径获取	
	String	dst_file_dir	;//源文件名称		/root/test/a.txt.txt
	String	dst_file_name	;//目标文件名称	通过绝对路径获取	
	String	file_level;//	最高密级	含义不一致	0-公开、1-内部、2-秘密、3-机密、4-绝密
	String	business_list	;//业务列表	无该字段	
	String	md5	;//文件内容md5	指纹字段	d41d8cd98f00b204e9800998ecf8427e
	
	String	file_name;//	文件名称		"266.pdf"
	String	file_size;//	大小	打印无大小	取整，单位kb，如14.92323寸为15
	String	file_type;//	类别	文件类型，如doc、png等	"pdf"
	@Override
	public List<DeviceInfo> getDeviceInfos() {
		
		
		if(StringUtils.isEmpty(this.getStd_dev_ip()))
		{
			return null;
		}
		List<DeviceInfo>  list=new ArrayList<>();
		DeviceInfo device=new DeviceInfo();
		device.setDeviceMac(this.getStd_dev_mac());
		device.setDeviceDiskSeq(this.getStd_dev_hardware_identification());
		device.setDeviceBrand(this.getStd_dev_brand_model());//std_dev_brand_model
		device.setDeviceIp(this.getStd_dev_ip());
		device.setDeviceModel(this.getStd_dev_hardware_model());
		device.setDeviceName(this.getStd_dev_name());
		device.setDeviceNetTime(this.getStd_dev_net_time());
		device.setDeviceOs(this.getStd_dev_os_type());
		device.setDeviceSecurityDomain(this.getStd_dev_safety_marign());
		device.setDeviceVersion(this.getStd_dev_software_version());
		device.setDeviceLevel(this.getStd_dev_level());
		device.setDeviceType(this.getStd_dev_type());
		
		device.setDeviceId(this.getStd_dev_id());
		
		PersonLiable personLiable=new PersonLiable();
		personLiable.setPersonLiableCode(this.getStd_user_no());
		personLiable.setPersonLiableName(this.getStd_username());
		personLiable.setPersonLiableOrg(this.getStd_user_department());
		personLiable.setPersonLiableRole(this.getStd_user_role());
		
		device.setPersonLiable(personLiable);
		//setDeviceInfoForAsset(device);
		list.add(device);
		return list;
		
	}
	@Override
	public List<StaffInfo> getStaffInfos() {
		if(StringUtils.isEmpty(this.getStd_user_no()))
		{
			return  null;
		}
		
		List<StaffInfo>  list=new ArrayList<>();
		
		StaffInfo staff=new StaffInfo();
		staff.setStaffAccount(this.getStd_user_no());
		staff.setStaffDepartment(this.getStd_user_department());
		staff.setStaffLevel(this.getStd_user_level());
		staff.setStaffName(this.getStd_username());
		staff.setStaffNo(this.getStd_user_no());
		staff.setStaffRole(this.getStd_user_role());
		staff.setStaffPost(this.getStd_user_station());
		
		list.add(staff);
		return list;
	}
	@Override
	public List<ApplicationInfo> getApplicationInfos() {
		if(StringUtils.isEmpty(this.getStd_sys_id()) )
		{
			return  null;
		}
		
		List<ApplicationInfo>  list=new ArrayList<>();
		
		ApplicationInfo applicationInfo=new ApplicationInfo();
		applicationInfo.setApplicationIp(this.getStd_sys_ip());
		applicationInfo.setApplicationId(this.getStd_sys_id());
		applicationInfo.setApplicationPort(this.getStd_sys_port());
		applicationInfo.setApplicationLabel(this.getStd_sys_name());
		applicationInfo.setApplicationArg(this.getStd_sys_parameter());
		applicationInfo.setApplicationProtocal(this.getStd_sys_protocal());
		
		list.add(applicationInfo);
		return list;
	}
	@Override
	public List<FileInfo> getFileInfos() {
		if(StringUtils.isEmpty(this.getMd5()))
		{
			return null;
		}
		
		List<FileInfo>  list=new ArrayList<>();
		
		FileInfo fileInfo=new FileInfo();
		fileInfo.setFileBusiness(this.getBusiness_list());
		fileInfo.setFileDataDownloadAddress(this.getSrc_file_dir());
		fileInfo.setFileDataToAddress(this.getDst_file_dir());
		fileInfo.setFileMd5(this.getMd5());
		if(StringUtils.isNotEmpty(this.getFile_name()))
		{

			fileInfo.setFileName(this.getFile_name());
		}else
		{
			fileInfo.setFileName(this.getSrc_file_name());
		}
		
		fileInfo.setFileSecurityLevel(this.getFile_level());
		fileInfo.setFileStoragePath(this.getSrc_file_name());
		fileInfo.setFileType(this.getFile_type());
		
		list.add(fileInfo);
		return list;
	}

	@Override
	public UnitInfo getUnitInfo(String principalIp) {
		UnitInfo unitInfo=new UnitInfo();
		unitInfo.setUnitGeoIdent(this.getStd_org_code());
		unitInfo.setUnitDepartName(this.getStd_org_name());
		return unitInfo;
	}

	@Override
	public boolean matchIndexPrefix(String indexName) {		
		return true;
	}
	

 
}

package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.UnitInfo;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl.EventSearchCache;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEventLog { 

	public abstract List<DeviceInfo>  getDeviceInfos();
 
	public abstract List<StaffInfo>  getStaffInfos();
	
	public abstract List<ApplicationInfo>  getApplicationInfos();
	
	public abstract List<FileInfo>  getFileInfos();
	
	
	public abstract UnitInfo getUnitInfo(String principalIp);
	
	public abstract boolean matchIndexPrefix(String indexName);
	
	public void  InitEvent(AlarmEventAttribute doc)
	{
						//通过日志  获取设备信息
		UnitInfo unitInfo = this.getUnitInfo(doc.getPrincipalIp());
		//todo 20230714 需要进行补全
//		if (unitInfo != null) {
//			if (doc.getUnitInfo() == null||StringUtils.isEmpty(doc.getUnitInfo().getUnitDepartSubCode())) {
//				EventSearchCache.updateUnitInfo(unitInfo);
//				doc.setUnitInfo(unitInfo);
//			}
//		}
		
		//设备赋值
		List<DeviceInfo> deviceInfos = doc.getDeviceInfos();;
		if(doc.getDeviceInfos()==null){
			doc.setDeviceInfos(new ArrayList<DeviceInfo>());
			deviceInfos = doc.getDeviceInfos();
		}
		
		List<DeviceInfo> devicesTemp = this.getDeviceInfos();
		if(devicesTemp!=null&&!devicesTemp.isEmpty())
		{
			for(DeviceInfo device :devicesTemp) {
				if(device==null||StringUtils.isEmpty(device.getDeviceIp()))
				{
					continue;
				}
				EventSearchCache.setDeviceInfoForAsset(device);
				deviceInfos.add(device);
			}
		}
		
		
		//人员赋值
		List<StaffInfo> staffInfos = doc.getStaffInfos();
		if(staffInfos==null)
		{ 
			doc.setStaffInfos(new ArrayList<>());
			staffInfos = doc.getStaffInfos();
		}
		List<StaffInfo> staffsTemp = this.getStaffInfos();
		if(staffsTemp!=null&&!staffsTemp.isEmpty())
		{
			for(StaffInfo staffInfo : staffsTemp){
				if(staffInfo != null && !staffInfos.contains(staffInfo)){
					staffInfos.add(staffInfo);
				}
			}
		}

		String staffInfoNo = "";
		// 获取关联设备责任人code
		for(DeviceInfo deviceInfo : deviceInfos){
			if(deviceInfo.getDeviceIp().equals(doc.getPrincipalIp())){
				staffInfoNo = deviceInfo.getPersonLiable().getPersonLiableCode();
			}
		}

		// 匹配到的人员，设置是否关联，用于显示
		for(StaffInfo staffInfo : staffInfos){
			if(staffInfo.getStaffNo().equals(staffInfoNo)){
				staffInfo.setIsRelation(1);
			}
		}

		//应用系统
		List<ApplicationInfo> applicationInfos = doc.getApplicationInfos();
		if(applicationInfos==null)
		{ 
			doc.setApplicationInfos(new ArrayList<>());
			applicationInfos = doc.getApplicationInfos();
		}
		List<ApplicationInfo> appsTemp = this.getApplicationInfos();
		if(appsTemp!=null&&!appsTemp.isEmpty())
		{
			for(ApplicationInfo applicationInfo : appsTemp){
				if(applicationInfo != null){
					applicationInfos.add(applicationInfo);
				}
			}
		}
		
		//文件
		List<FileInfo> fileInfos = doc.getFileInfos();
		if(fileInfos==null)
		{ 
			doc.setFileInfos(new ArrayList<>());
			fileInfos = doc.getFileInfos();
		}
		List<FileInfo> filesTemp = this.getFileInfos();
		if(filesTemp!=null&&!filesTemp.isEmpty())
		{
			for(FileInfo fileInfo : filesTemp){
				if(fileInfo != null){
					fileInfos.add(fileInfo);
				}
			}
		}
	}
	
}

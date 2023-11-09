package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import org.apache.commons.lang3.StringUtils;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceInfo {
	
 
	@ApiModelProperty(value = "设备类Id")
	String deviceId;
	
	// 设备类型
	@EsField("device_type")
	@ApiModelProperty(value = "设备类型")
	String deviceType;
	// 设备名称
	@EsField("device_name")
	@ApiModelProperty(value = "设备名称")
	String deviceName;
	// 设备密级
	@EsField("device_level")
	@ApiModelProperty(value = "设备密级")
	String deviceLevel;
	// 操作系统类型
	@EsField("device_os")
	@ApiModelProperty(value = "操作系统类型")
	String deviceOs;
	// IP
	@EsField("device_ip")
	@ApiModelProperty(value = "ip")
	String deviceIp;
	// 软件系统版本号
	@EsField("device_version")
	@ApiModelProperty(value = "软件系统版本号")
	String deviceVersion;
	// 硬件设备型号
	@EsField("device_model")
	@ApiModelProperty(value = "硬件设备型号")
	String deviceModel;
	// 设备品牌型号
	@EsField("device_brand")
	@ApiModelProperty(value = "设备品牌型号")
	String deviceBrand;
	// 设备入网时间
	@EsField("device_net_time")
	@ApiModelProperty(value = "设备入网时间")
	String deviceNetTime;
	// MAC
	@EsField("device_mac")
	@ApiModelProperty(value = "MAC")
	String deviceMac;
	// 硬盘序列号/设备ID
	@EsField("device_disk_seq")
	@ApiModelProperty(value = "硬盘序列号/设备ID")
	String deviceDiskSeq;
	// 所属安全域
	@EsField("device_security_domain")
	@ApiModelProperty(value = "所属安全域")
	String deviceSecurityDomain;

	@EsField("domainInfo")
	@ApiModelProperty(value = "安全域信息")
	BaseSecurityDomain domainInfo;
	
 
	@ApiModelProperty(value = "单位编码")
	String orgCode;
	
	
 
	@ApiModelProperty(value = "单位名称")
	String orgName;
	
	@EsField("personLiable")
	@ApiModelProperty(value = "责任人")
    PersonLiable personLiable;

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;//地址相等
        }

        if(obj == null){
            return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }

        if(obj instanceof DeviceInfo){
        	DeviceInfo other = (DeviceInfo) obj;
            //需要比较的字段相等，则这两个对象相等
            if(equalsStr(this.deviceIp, other.deviceIp)
            		&& equalsStr(this.deviceType, other.deviceType)&& equalsStr(this.deviceDiskSeq, other.deviceDiskSeq)){
                return true;
            }
        }

        return false;
    }
    
    
    private boolean equalsStr(String str1, String str2){
        if(StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)){
            return true;
        }
        if(!StringUtils.isEmpty(str1) && str1.equals(str2)){
            return true;
        }
        return false;
    }
    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isEmpty(this.deviceId))
        {
        	 sb.append(this.deviceIp);
        }else
        {
        	 sb.append(this.deviceId);
        }

//        sb.append(this.deviceType);
//        sb.append(this.deviceDiskSeq);
        char[] charArr = sb.toString().toCharArray();
        int hash = 0;
        for(char c : charArr) {
            hash = hash * 131 + c;
        }
        return hash;
    }
	
	
}
package com.vrv.vap.admin.vo.supervise.putdata;

 

import com.vrv.vap.admin.config.PutField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationInfo {
 
 
	@ApiModelProperty(value = "应用系统id")
	String applicationId;
	
	// 应用系统名称
	@PutField("application_label")
	@ApiModelProperty(value = "应用系统名称")
	String applicationLabel;
	// 所在服务器IP
	@PutField("application_ip")
	@ApiModelProperty(value = "所在服务器IP")
	String applicationIp;
	// 通讯协议名
	@PutField("application_protocal")
	@ApiModelProperty(value = "通讯协议名")
	String applicationProtocal;
	// 通信参数
	@PutField("application_arg")
	@ApiModelProperty(value = "通信参数")
	String applicationArg;
	// 服务端口
	@PutField("applicationPort")
	@ApiModelProperty(value = "服务端口")
	String applicationPort;
	
	
    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.applicationId); 
        char[] charArr = sb.toString().toCharArray();
        int hash = 0;
        for(char c : charArr) {
            hash = hash * 131 + c;
        }
        return hash;
    }
}
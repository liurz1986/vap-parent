package com.vrv.vap.admin.vo.supervise;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class ServerRegisterResult  extends BaseResult  {
	//OAuth2ClientKey accessKey; 
	@SerializedName("client_id")
	String clientId;
	@SerializedName("client_secret")
	String clientSecret;
	
	public static ServerRegisterResult  result(String code,String msg){
		ServerRegisterResult t=new ServerRegisterResult();
		t.setMsg(msg);
		t.setCode(code);
		t.setCodeDescript(getDescript(code)) ;
		return t;
	}
	
	public static ServerRegisterResult  error(String msg){
		
		return result("-1",msg);
	}
	 
	public static String getDescript(String code)
	{
		if(!StringUtils.isEmpty(code))
		{
//			200：成功
//			99：认证校验错误
//			98：没有接口权限
//			97：端口错误
//			95：上级节点未认证
//			94：加解密错误
//			93：网络连接不通或无上级节点

			switch (code) {
			case "200":
				return "认证成功";
			case "99":
				return "认证校验错误";
			case "98":
				return "没有接口权限";
			case "97":
				return "端口错误";
			case "95":
				return "上级节点未认证";
			case "94":
				return "加解密错误";
			case "93":
				return "网络连接不通或无上级节点";
			case "404":
				return "网络错误";
			default:
				break;
			}
		}
		
		return "未知异常";
	}
	 
}

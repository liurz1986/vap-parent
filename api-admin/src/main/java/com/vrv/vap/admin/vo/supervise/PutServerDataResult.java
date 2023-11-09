package com.vrv.vap.admin.vo.supervise;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class PutServerDataResult extends BaseResult {
	
 
	public static PutServerDataResult  result(String code,String msg){
		PutServerDataResult t=new PutServerDataResult();
		t.setMsg(msg);
		t.setCode(code);
		t.setCodeDescript(getDescript(code)) ;
		return t;
	}
	public static PutServerDataResult  error(String msg){
		
		return result("-1",msg);
	}

	public static String getDescript(String code)
	{
		if(!StringUtils.isEmpty(code))
		{
//			200	success
//			99	没有接口权限
//			98	接口调用超过最大次数，请明天再试
//			95	无上级节点或上级节点未认证
//			94	加解密错误
//			50	离线导入成功
//			49	校验成功
//			48	校验错误
//			47	数据包格式有误

			switch (code) {
			case "200":
				return "success"; 
			case "99":
				return "没有接口权限"; 
			case "98":
				return "接口调用超过最大次数，请明天再试"; 
			case "95":
				return "无上级节点或上级节点未认证"; 
			case "94":
				return "加解密错误"; 
			case "50":
				return "离线导入成功"; 
			case "49":
				return "校验成功"; 
			case "48":
				return "校验错误"; 
			case "47":
				return "数据包格式有误"; 
			case "404":
				return "网络错误"; 
			default:
				break;
			}
			
			
		}
		
		return "未知异常";
	}
}

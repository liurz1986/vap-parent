package com.vrv.vap.admin.vo.supervise;

public class PutServerStatusResult  extends BaseResult{

	public static PutServerStatusResult  result(String code,String msg){
		PutServerStatusResult t=new PutServerStatusResult();
		t.setMsg(msg);
		t.setCode(code);
		t.setCodeDescript(msg);
		return t;
	}
	public static PutServerStatusResult  error(String msg){
		
		return result("-1",msg);
	}
}

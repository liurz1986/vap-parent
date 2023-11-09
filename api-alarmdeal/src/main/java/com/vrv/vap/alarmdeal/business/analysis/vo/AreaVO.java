package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年7月26日 下午3:19:16 
* 类说明 
*/
@Data
public class AreaVO {
     
	private String areaName;
	private String areaCode;
	private long sTime; //进入缓存的初始时间
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AreaVO areaVO = (AreaVO) o;

        return areaCode.equals(areaVO.areaCode);
    }

    @Override
    public int hashCode() {
        int result = areaCode.hashCode();
        return result;
    }
	
}

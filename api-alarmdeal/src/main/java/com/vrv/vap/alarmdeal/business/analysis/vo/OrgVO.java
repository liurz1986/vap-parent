package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月11日 下午4:25:08 
* 类说明: 组织机构VO
*/
@Data
public class OrgVO {
     
	private String orgCode;
	private String orgName;
	private long sTime; //进入缓存的初始时间
	
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgVO orgVO = (OrgVO) o;

        return orgCode.equals(orgVO.orgCode);
    }

    @Override
    public int hashCode() {
        int result = orgCode.hashCode();
        return result;
    }
	
}

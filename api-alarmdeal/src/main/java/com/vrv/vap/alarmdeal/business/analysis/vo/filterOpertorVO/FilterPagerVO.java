package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import com.vrv.vap.jpa.web.page.PageReq;
import lombok.Data;

/**
 * 过滤器分页VO
 * @author wd-pc
 *
 */
@Data
public class FilterPagerVO extends PageReq {
	private String name;
	private String operatorType;
	private String guids;
}

package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;

/**
 * 阻断返回信息
 * @author wd-pc
 *
 */
@Data
public class BlockResponseVO {

	private boolean success; //返回结果 success:true(返回成功)；success:fasle
	private String msg; //提示消息
	
}

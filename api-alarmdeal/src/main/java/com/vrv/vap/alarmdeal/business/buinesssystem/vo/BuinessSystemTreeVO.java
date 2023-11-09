package com.vrv.vap.alarmdeal.business.buinesssystem.vo;


import lombok.Data;

import java.util.List;


/**
 *
 * 业务系统展示左边树
 *
 */
@Data
public class BuinessSystemTreeVO {
	private String name;  // 业务系统名称

	private String code;  // 业务系统id

	private List<BuinessSystemTreeVO> children; // 子业务系统
}

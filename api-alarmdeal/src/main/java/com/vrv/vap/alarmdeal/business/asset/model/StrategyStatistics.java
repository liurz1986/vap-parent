package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 执行策略统计
 */
@Data
@Table(name = "strategy_statistics")
@Entity
@ApiModel(value = "执行策略统计实体类")
public class StrategyStatistics {
	/**
	 * 主键id
	 */
	@Id
	@Column(name = "guid")
	private String guid;
	/**
	 * 策略名称
	 */
	@Column(name="strategy_name")
	private String strategyName;
	/**
	 * 下发策略执行统计数量
	 */
	@Column(name="strategy_num")
	private Long strategyNum;
	/**
	 * 注册设备数量
	 */
	@Column(name = "register_device_num")
	private Long registerDeviceNum;
}

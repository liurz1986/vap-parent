package com.vrv.rule.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class SwitchTableVO {

    private String assetGuid;
    private String ifNumber;
	private String collectorIp;
	private Timestamp triggerTime;
	private Integer counts;
	
}

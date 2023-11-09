package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OperationLog {
	Integer  userId;
	String userName;
	List<String> roleIds;  
	String log;
	Date time;
}

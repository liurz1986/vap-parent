package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import java.util.Map;

import com.vrv.vap.alarmdeal.frameworks.contract.user.User;

import lombok.Data;
@Data
public class DealTaskstaticVO {
       
	private User user;
	private Map<String, Object> info;
}

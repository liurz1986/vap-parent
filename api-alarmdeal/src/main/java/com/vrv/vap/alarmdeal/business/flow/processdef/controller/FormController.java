package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("form")
public class FormController {
	
	@RequestMapping("default")
	public String defaultform() {
		return "form/default";
	}
	
	@RequestMapping("gatewayCheckForm")
	public String gatewayCheckForm() {
		return "form/inner/gatewayCheckForm";
	}
	
	@RequestMapping("diyForm")
	public String diyForm() {
		return "form/diy/diyForm";
	}
}

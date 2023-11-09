package com.vrv.rule.model.filter;

import lombok.Data;

@Data
public class Attach {
   
	private String id; //对应的主键
	private String type; //key or window or dimension or splicing
	private String options;// value of key or value of window
}

package com.flink.demo.flinksql.model;

import lombok.Data;

@Data
public class DimensionUser {

	private Long user_id;
	private String name;
	private int age;
	
}

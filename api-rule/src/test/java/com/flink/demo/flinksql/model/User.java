package com.flink.demo.flinksql.model;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class User {
    
	private long user_id;
	private long item_id;
	private long behavior;
	private Timestamp ts;
}

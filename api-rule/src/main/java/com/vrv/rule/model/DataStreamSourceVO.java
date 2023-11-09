package com.vrv.rule.model;

import java.util.List;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.vo.FieldInfoVO;

import lombok.Data;

@Data
public class DataStreamSourceVO {
    
	private String sourceId;
	private DataStream<Row> dataStreamSource;
	private List<FieldInfoVO> fieldInfoVOs;
	private List<Attach> attachs;
	
}

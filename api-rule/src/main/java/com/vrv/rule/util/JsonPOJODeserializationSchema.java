package com.vrv.rule.util;

import java.io.IOException;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import com.vrv.rule.logVO.venustech.ids.IdsLog;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月27日 下午5:56:26 
* 类说明  存在问题
*/
public class JsonPOJODeserializationSchema extends AbstractDeserializationSchema<IdsLog> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	/** Type information describing the result type. */
	private final TypeInformation<IdsLog> typeInfo;
	
	
	public JsonPOJODeserializationSchema(TypeInformation<IdsLog> typeInfo){
		this.typeInfo = typeInfo;
	}
	
	
	@Override
	public IdsLog deserialize(byte[] message) throws IOException {
		try {
			final JsonNode root = objectMapper.readTree(message);
			return null;
		} catch (Throwable t) {
			throw new IOException("Failed to deserialize JSON object.", t);
		}
	
	}
	
	


	

}

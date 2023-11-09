package com.vrv.rule.ruleInfo.exchangeType.flatMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.vrv.rule.vo.FieldInfoVO;

import lombok.Data;

@Data
public class RowInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<FieldInfoVO> fields;

	public int getFlatColSize() {
		int colSize = 0;
		for (FieldInfoVO fieldInfo : fields) {
			if(fieldInfo.getFieldType().equals("pojo") || fieldInfo.getFieldType().equals("pojoArray")) {
				colSize += fieldInfo.getHints().getFlatColSize();
			} else {
				colSize += 1;
			}
		}
		
		return colSize;
	}

	public List<FieldInfoVO> flatFields(int beginOrder,String parentFieldName) {
		List<FieldInfoVO> result = new ArrayList<>();
		for (FieldInfoVO fieldInfo : fields) {
			if(fieldInfo.getFieldType().equals("pojo") || fieldInfo.getFieldType().equals("pojoArray")) {
				List<FieldInfoVO> flatFields = fieldInfo.getHints().flatFields(beginOrder,fieldInfo.getFieldName());
				result.addAll(flatFields);
				beginOrder += flatFields.size();
			} else {
				FieldInfoVO f = fieldInfo.copyOne();
				if(StringUtils.isNotEmpty(parentFieldName)){
					f.setFieldName(parentFieldName+"_"+f.getFieldName());
				}else {
					f.setFieldName(f.getFieldName());
				}
				f.setOrder(beginOrder++);
				result.add(f);
			}
		}
		return result;
	}
	
	public void printInfo() {
		Gson json = new Gson();
		String json2 = json.toJson(this);
		System.out.println(json2);
	}
}

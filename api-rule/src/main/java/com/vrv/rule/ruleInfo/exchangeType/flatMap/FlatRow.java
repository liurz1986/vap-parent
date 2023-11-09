package com.vrv.rule.ruleInfo.exchangeType.flatMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.types.Row;

import com.vrv.rule.vo.FieldInfoVO;

/**
 * 对应的row
 * @author wd-pc
 *
 */
public class FlatRow implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RowInfo rowInfo;



	

	public RowInfo getRowInfo() {
		return rowInfo;
	}


	public void setRowInfo(RowInfo rowInfo) {
		this.rowInfo = rowInfo;
	}


	public int flatColSize() {
		if(rowInfo == null) {
			throw new RuntimeException("没有设置列信息");
		}
		return rowInfo.getFlatColSize();
	}
	
	
	/**
	 * 如果存在复杂结构，则将复杂结构转成平面结构返回
	 * 当前row没有复杂结构，则构造row，最终返回
	 * 如果当前row有复杂结构，则构造子节点的rows，并和其他结构cross。
	 * 当有多个复杂结构的时候，则是依次cross出最新的row
	 * @return
	 */
	public List<Row> flatMap(Row row,String parentFieldName) {
		int flatColSize = flatColSize();
		essembleColRef(parentFieldName);
		List<Row> flatMap = flatMap(flatColSize, rowInfo,row);
		return flatMap;
	}

	private void essembleColRef(String parentFieldName) {
		flatFields(0,parentFieldName);	
	}
	

	
	
	
	public List<FieldInfoVO> flatFields(int beginOrder,String parentFieldName) {
		if(rowInfo == null) {
			throw new RuntimeException("没有设置列信息");
		}
	      return rowInfo.flatFields(beginOrder,parentFieldName);
	}
	
	/**
	 * copy对应的row
	 * @param nRow
	 * @param inputRow
	 */
	private void copy(Row nRow,Row inputRow){
		for (int i = 0; i < inputRow.getArity(); i++) {
			Object field = inputRow.getField(i);
			if(field != null){
				nRow.setField(i, field);
			}
		}
	}
	
	
	
	
	private List<Row> crossRow(List<Row> rows1, List<Row> rows2) {
		if(rows2 == null || rows2.size() == 0) {
			return rows1;
		}
		List<Row> result = new ArrayList<>();
		Row row = rows2.get(0);
		int arity = row.getArity();
		for (Row frow : rows1) {
			for (Row srow : rows2) {
				Row nRow = new Row(arity);
				copy(nRow, srow);
				copy(nRow, frow);
				result.add(nRow);
			}
		}
		
		return result;
	}

	
	private List<Row> flatMap(int flatColSize, RowInfo hints,Row inputRow) {
		List<Row> result = new ArrayList<>();
		Row nRow = new Row(flatColSize);
		List<FieldInfoVO> fields = hints.getFields();
		Map<Integer, List<Row>> childFlatRows = new HashMap<>();
		for (int i = 0; i < fields.size(); i++) {
			FieldInfoVO f = fields.get(i);
			if(f.getFieldType().equals("pojoArray")) {
				Row[] children = (Row[])inputRow.getField(f.getOrder());
				for (Row row : children) {
					List<Row> cRows = flatMap(flatColSize, f.getHints(),row);
					if(!childFlatRows.containsKey(i)) {
						childFlatRows.put(i, new ArrayList<>());
					}
					childFlatRows.get(i).addAll(cRows);
				}
			} else if(f.getFieldType().equals("pojo")) {
				Row child = (Row) inputRow.getField(f.getOrder());
				List<Row> flatMap = flatMap(flatColSize, f.getHints(),child);
				if(!childFlatRows.containsKey(i)) {
					childFlatRows.put(i, new ArrayList<>());
				}
				childFlatRows.get(i).addAll(flatMap);
			} else {
				FieldInfoVO flatFieldRef = f.getFlatFieldRef();
				nRow.setField(flatFieldRef.getOrder(),inputRow.getField(i));
			}
		}
		result.add(nRow);
		
		for (Integer index : childFlatRows.keySet()) {
			List<Row> list = childFlatRows.get(index);
			result = crossRow(result, list);
		}
		
		return result;
	}
	
	
	
	
	
	
}

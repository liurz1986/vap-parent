package com.flink.demo.flatMap;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.types.Row;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vrv.rule.ruleInfo.exchangeType.flatMap.FlatRow;
import com.vrv.rule.ruleInfo.exchangeType.flatMap.RowInfo;
import com.vrv.rule.vo.FieldInfoVO;


public class RowTest {
	
	private Row r;
	private FlatRow flatRow;
	
	@Before
	public void before() {
		System.out.println("before");
		r = new Row(5);
		r.setField(0, 1);
		r.setField(1, "a");
		List<Row> children = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Row rChild = new Row(3);
			rChild.setField(0, i);
			rChild.setField(1, "child-" + i);
			List<Row> cccRows = new ArrayList<>();
			for (int j = 0; j < 2; j++) {
				Row rCCC = new Row(2);
				rCCC.setField(0, "cc-guid" + j);
				rCCC.setField(1, "child-" + i +"ccc-title" + j);
				cccRows.add(rCCC);
			}
			rChild.setField(2, cccRows);
			children.add(rChild);
		}
		r.setField(2, children);
		r.setField(3, "b");
		Row pojoF = new Row(3);
		pojoF.setField(0, "pojo-name-1");
		pojoF.setField(1, "pojo-title-1");
		List<Row> pojoArraVal = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Row rChild = new Row(2);
			rChild.setField(0, "pojo-arra-guid-" + i);
			rChild.setField(1, "pojo-arra-title-" + i);
			pojoArraVal.add(rChild);
		}
		pojoF.setField(2, pojoArraVal);
		r.setField(4, pojoF);
		
		RowInfo rInfo = new RowInfo();
		List<FieldInfoVO> fields = new ArrayList<>();
		fields.add(FieldInfoVO.builder().fieldName("id").order(0).fieldType("int").build());
		fields.add(FieldInfoVO.builder().fieldName("name").order(1).fieldType("string").build());
		
		RowInfo childRowInfo = new RowInfo();
		List<FieldInfoVO> childFields = new ArrayList<>();
		childFields.add(FieldInfoVO.builder().fieldName("c1-id").order(0).fieldType("int").build());
		childFields.add(FieldInfoVO.builder().fieldName("c1-name").order(1).fieldType("String").build());
		RowInfo cccInfo = new RowInfo();
		
		List<FieldInfoVO> cccF = new ArrayList<>();
		cccF.add(FieldInfoVO.builder().fieldName("cc2-guid").order(0).fieldType("String").build());
		cccF.add(FieldInfoVO.builder().fieldName("cc2-title").order(1).fieldType("String").build());
		cccInfo.setFields(cccF);
		childFields.add(FieldInfoVO.builder().fieldName("ccc").order(2).fieldType("pojoArray").hints(cccInfo).build());
		
		childRowInfo.setFields(childFields);
		fields.add(FieldInfoVO.builder().fieldName("name").order(2).fieldType("pojoArray").hints(childRowInfo).build());
		fields.add(FieldInfoVO.builder().fieldName("title").order(3).fieldType("string").build());
		RowInfo PojoRow = new RowInfo();
		List<FieldInfoVO> pojoFields = new ArrayList<>();
		pojoFields.add(FieldInfoVO.builder().fieldName("pojo-name").order(0).fieldType("string").build());
		pojoFields.add(FieldInfoVO.builder().fieldName("pojo-title").order(1).fieldType("string").build());
		RowInfo pojoCArray = new RowInfo();
		List<FieldInfoVO> pojoCArrayFields = new ArrayList<>();
		pojoCArrayFields.add(FieldInfoVO.builder().fieldName("pojo-carr-name").order(0).fieldType("string").build());
		pojoCArrayFields.add(FieldInfoVO.builder().fieldName("pojo-carr-title").order(1).fieldType("string").build());
		pojoCArray.setFields(pojoCArrayFields);
		
		pojoFields.add(FieldInfoVO.builder().fieldName("pojo-array").order(2).fieldType("pojoArray").hints(pojoCArray).build());
		PojoRow.setFields(pojoFields);
		fields.add(FieldInfoVO.builder().fieldName("pojo-obj").order(4).fieldType("pojo").hints(PojoRow).build());
		
		rInfo.setFields(fields);
		flatRow= new FlatRow();
		flatRow.setRowInfo(rInfo);
	}
	
	@Test
	public void test2SimplefieldType() {
		
		System.out.println(r);
		RowInfo rowInfo = flatRow.getRowInfo();
		rowInfo.printInfo();
		
		System.out.println("------------------");
		
		List<Row> rows = flatRow.flatMap(r,"");
		for (Row row : rows) {
			System.out.println(row);
		}
	}
	
	@Test
	public void testFlatColSize() {
		int flatColSize = flatRow.flatColSize();
		Assert.assertEquals(7, flatColSize);
	}
	
	@Test
	public void testFlatCols() {
		RowInfo rowInfo = flatRow.getRowInfo();
		rowInfo.printInfo();
		
		List<FieldInfoVO> flatFields = flatRow.flatFields(0,"");
		
		for (FieldInfoVO FieldInfoVO : flatFields) {
			System.out.println(FieldInfoVO);
		}
	}
	
	@Test
	public void testBoolean() {
		Row row = Row.of(true);
		boolean field = (boolean)row.getField(0);
		String result = "true";
		Boolean valueOf = Boolean.valueOf(result);
		System.out.println(field);
	}
	
	
}

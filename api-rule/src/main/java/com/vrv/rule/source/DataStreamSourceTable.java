package com.vrv.rule.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;

import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.FieldTypeEnum;

/**
* @author wudi E-mail:wudi891012@163.com
* @version 创建时间：2018年11月5日 下午4:11:53
* 类说明 采用jdbc的方式获得对应的方式
*/
public class DataStreamSourceTable implements StreamTableSource<Row>,Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<FieldInfoVO> fieldInfoVOs;  //类型名称
	private DataStream<Row> inputDataStream;//输入流

	public DataStreamSourceTable(){
		
	}
	
	public DataStreamSourceTable(List<FieldInfoVO> fieldInfoVOs,DataStream<Row> inputDataStream) {
		this.fieldInfoVOs = fieldInfoVOs;
		this.inputDataStream = inputDataStream;
	}
	
	
	
	
	public List<FieldInfoVO> getFieldInfoVOs() {
		return fieldInfoVOs;
	}

	public void setFieldInfoVOs(List<FieldInfoVO> fieldInfoVOs) {
		this.fieldInfoVOs = fieldInfoVOs;
	}

	public DataStream<Row> getInputDataStream() {
		return inputDataStream;
	}

	public void setInputDataStream(DataStream<Row> inputDataStream) {
		this.inputDataStream = inputDataStream;
	}

	@Override
	public String explainSource() {
		return "JDBCTableSource";
	}

	@Override
	public TypeInformation<Row> getReturnType() {
		String[] field = getField(fieldInfoVOs);
		TypeInformation[] types = getTypeInformation();
		TypeInformation<Row> row = Types.ROW(field, types);
		return row;
	
	}

	@Override
	public TableSchema getTableSchema() {
		String[] field = getField(fieldInfoVOs);
		TypeInformation[] types = getTypeInformation();
		return new TableSchema(field,types);
	}

	@Override
	public DataStream<Row> getDataStream(StreamExecutionEnvironment env){
	        return inputDataStream;
	}
	
	
	/**
	 * 获得对应的匹配类型
	 * @param fieldInfos
	 * @return
	 */
	private TypeInformation[] getTypeInformation(){
		List<TypeInformation> list = new ArrayList<>();
		for (FieldInfoVO fieldInfoVO : fieldInfoVOs){
			String fieldType = fieldInfoVO.getFieldType();
			switch (fieldType) {
			case "pojo":
			case "pojoArray":
				List<FieldInfoVO> pojoFields = fieldInfoVO.getChildFields();
				TypeInformation[] pojoTypeInformation = getTypeInformation(pojoFields);
				String[] pojoField = getField(pojoFields);
				TypeInformation flinkTypeInformation = FieldTypeEnum.getFlinkType(fieldType,pojoField,pojoTypeInformation);
				list.add(flinkTypeInformation);
				break;
			default:
				TypeInformation typeInformation = FieldTypeEnum.getFlinkType(fieldType);
				list.add(typeInformation);
				break;
			}
		}
		TypeInformation[] types = list.toArray(new TypeInformation[list.size()]);
		return types;
	
	}
	
	 
	 /**
	  * 根据对应的fieldInfoVO获得对应的TypeInformation[]
	  * @param fieldInfoList
	  * @return
	  */
	 private  TypeInformation[] getTypeInformation(List<FieldInfoVO> fieldInfoList){
		 List<TypeInformation> list = new ArrayList<>();
		 for (FieldInfoVO fieldInfoVO : fieldInfoList){
				String fieldType = fieldInfoVO.getFieldType();
				TypeInformation flinkType = FieldTypeEnum.getFlinkType(fieldType);
				list.add(flinkType);
		}
		 TypeInformation[] types = list.toArray(new TypeInformation[list.size()]);
		return types;
	 }
	
	
	
	
	/**
	 * 获得原始日志字段
	 * @return
	 */
	public  String[] getField(List<FieldInfoVO> fieldInfoList){
		List<String> fieldList = new ArrayList<>();
		for (FieldInfoVO fieldInfoVO : fieldInfoList) {
			String fieldName = fieldInfoVO.getFieldName();
			fieldList.add(fieldName);
		}
		String[] field=fieldList.toArray(new String[fieldList.size()]);
	    return field;
	}
	

	
	
	
}
